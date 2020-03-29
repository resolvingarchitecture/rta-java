package ra.rta.rfm.conspref.sources.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.rfm.conspref.sources.MessageManager;

import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileSplitter extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(FileSplitter.class);

    private int groupId;
    private String topic;
    private Path dataFileName;
    private Path markerFileName;
    private int startingLine;
    private Path sourceDir;
    private Path archiveDir;
    private MessageManager messageManager;

    private boolean terminate = false;
    private boolean completed = false;

    public FileSplitter(int groupId,
                        String topic,
                        Path dataFileName,
                        Path markerFileName,
                        int startingLine,
                        Path sourceDir,
                        Path archiveDir,
                        MessageManager messageManager) {
        this.groupId = groupId;
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

                // Add message
                LOG.info(".");
                messageManager.send(topic, line, true);
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
