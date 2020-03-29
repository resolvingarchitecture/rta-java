package ra.rta.sources.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import ra.rta.models.Envelope;
import ra.rta.models.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.sources.MessageManager;

import java.io.ByteArrayOutputStream;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileSplitter extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(FileSplitter.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String partnerNamespace;
    private Path dataFileName;
    private Path markerFileName;
    private int startingLine;
    private Path sourceDir;
    private Path archiveDir;
    private MessageManager messageManager;

    private boolean terminate = false;
    private boolean completed = false;

    public FileSplitter(String partnerNamespace,
                        Path dataFileName,
                        Path markerFileName,
                        int startingLine,
                        Path sourceDir,
                        Path archiveDir,
                        MessageManager messageManager) {
        this.partnerNamespace = partnerNamespace;
        this.dataFileName = dataFileName;
        this.markerFileName = markerFileName;
        this.startingLine = startingLine;
        this.sourceDir = sourceDir;
        this.archiveDir = archiveDir;
        this.messageManager = messageManager;
    }

    public void terminate() {
        terminate = true;
    }

    public boolean getCompleted() {
        return completed;
    }

    @Override
    public void run() {

        String dataFileNameStr = dataFileName.toString();
        String commandEvent = dataFileNameStr.substring(0, dataFileNameStr.lastIndexOf(".")).trim();
        long i = System.currentTimeMillis();
        try {
            // Verify that the new
            //  file is a text file.
            Path dataFile = sourceDir.resolve(dataFileName);
            Path markerFile = sourceDir.resolve(markerFileName);

            // TODO replace w/ SLF4J
            LOG.info("Sending file contents...");
            String topic = "transaction";
            if (!dataFileNameStr.toUpperCase().contains("TRANSACTION") && !dataFileNameStr.toUpperCase().contains("TXN")) {
                topic = "reference";
            }
            // Read Data from File Line by Line
            LineNumberReader lineNumberReader = new LineNumberReader(Files.newBufferedReader(dataFile, StandardCharsets.UTF_8));
            String line;
            // Send Each Line to Kafka Producer and Sleep
            int totalNumLinesSent = 0;
            while ((line = lineNumberReader.readLine()) != null && !terminate) {
                if(lineNumberReader.getLineNumber() < startingLine)
                {
                    continue; // Fast-forward to starting line
                }
                Envelope envelope = new Envelope();
                envelope.getHeader().setCommand(commandEvent);
                Record record = new Record();
                envelope.getBody().getRecords().add(record);
                record.getGroup().setName(partnerNamespace);
                record.setRaw(line);
                // Add message
                // TODO: Protostuff/Kryo serialization here replacing JSON serialization
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                MAPPER.writeValue(os, envelope);
                LOG.info(".");
                messageManager.send(topic, new String(os.toByteArray()), !"transaction".equals(topic));
                Files.write(markerFile,(lineNumberReader.getLineNumber()+"").getBytes());
                totalNumLinesSent++;
            }
            // Move file to archived folder
            if(!terminate) {
                // Move data file (don't archive yet while sharing with v4)
//                Files.move(dataFile, archiveDir.resolve(dataFileNameStr), StandardCopyOption.REPLACE_EXISTING);
                // Move marker file
                Files.move(markerFile, archiveDir.resolve(markerFileName), StandardCopyOption.REPLACE_EXISTING);
            }
            LOG.info("File content sent. Total lines sent: " + totalNumLinesSent);
        } catch(Exception e) {
            String msg = "Exception caught while loading file: "+e;
            LOG.warn(msg);
        }
        completed = true;
    }
}
