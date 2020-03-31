package ra.rta.sources.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.connectors.kafka.KafkaMgr;
import ra.rta.Event;
import ra.rta.utilities.JSONUtil;
import ra.rta.utilities.RandomUtil;

import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileSplitter extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(FileSplitter.class);

    private long sourceId;
    private Path dataFileName;
    private Path markerFileName;
    private String payloadTransformerClass;
    private int startingLine;
    private Path sourceDir;
    private Path archiveDir;
    private KafkaMgr kafkaMgr;

    private boolean terminate = false;
    private boolean completed = false;

    public FileSplitter(long sourceId,
                        Path dataFileName,
                        Path markerFileName,
                        String payloadTransformerClass,
                        int startingLine,
                        Path sourceDir,
                        Path archiveDir,
                        KafkaMgr kafkaMgr) {
        this.sourceId = sourceId;
        this.dataFileName = dataFileName;
        this.markerFileName = markerFileName;
        this.payloadTransformerClass = payloadTransformerClass;
        this.startingLine = startingLine;
        this.sourceDir = sourceDir;
        this.archiveDir = archiveDir;
        this.kafkaMgr = kafkaMgr;
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
        String commandTopicDurabilityStr = dataFileNameStr.substring(0, dataFileNameStr.lastIndexOf(".")).trim();
        String[] commandTopicDurabilityTriple = commandTopicDurabilityStr.split("-");
        int command = Integer.parseInt(commandTopicDurabilityTriple[0]);
        String topic = commandTopicDurabilityTriple[1];
        boolean durable = Boolean.parseBoolean(commandTopicDurabilityTriple[2]);
        long i = System.currentTimeMillis();
        try {
            // Verify that the new
            //  file is a text file.
            Path dataFile = sourceDir.resolve(dataFileName);
            Path markerFile = sourceDir.resolve(markerFileName);

            LOG.info("Sending file contents...");
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
                Event event = new Event();
                event.id = RandomUtil.nextRandomLong();
                event.commandId = command;
                event.sourceId = sourceId;
                event.rawPayload = line.getBytes();
                event.payloadTransformerClass = payloadTransformerClass;
                // Add message
                LOG.info(".");
                kafkaMgr.send(topic, JSONUtil.MAPPER.writeValueAsBytes(event), durable);
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
