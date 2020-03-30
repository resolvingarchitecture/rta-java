package ra.rta.sources.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.connectors.kafka.KafkaMgr;

import java.util.List;

public class FileSourceShutdown extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(FileSourceShutdown.class);

    private List<FileSplitter> fileSplitters;
    private KafkaMgr kafkaMgr;
    private int maxWait = 20000;
    private int waitTime = 2000;
    private int accumulatedWait = 0;

    public FileSourceShutdown(List<FileSplitter> fileSplitters, KafkaMgr kafkaMgr) {
        this.fileSplitters = fileSplitters;
        this.kafkaMgr = kafkaMgr;
    }

    @Override
    public void run() {
        LOG.info("File Producer stopping worker threads...");
        for(FileSplitter fileSplitter : fileSplitters)
            fileSplitter.terminate(); // Signal all FileSplitters to complete
        int fileSplittersCompleted = 0;
        do {
            try {
                LOG.info("Waiting {} seconds for thread workers to complete.",waitTime/1000);
                accumulatedWait += waitTime;
                Thread.currentThread().sleep(waitTime);
            } catch(InterruptedException ie) {
                fileSplittersCompleted = 0; // reset
                for (FileSplitter fileSplitter : fileSplitters) {
                    if (fileSplitter.getCompleted()) fileSplittersCompleted++;
                }
                LOG.info(fileSplittersCompleted + " out of " + fileSplitters.size() + " thread workers have completed.");
                if(accumulatedWait >= maxWait) {
                    LOG.info("Max wait time of {} seconds has occurred. Force exiting...",maxWait/1000);
                    break;
                }
            }
        } while(fileSplitters.size() < fileSplittersCompleted);
        kafkaMgr.shutdown();
        if(accumulatedWait < maxWait)
            LOG.info("All worker threads stopped. {} gracefully shutdown.", FileSource.class.getSimpleName());
        else
            LOG.info("{} forcefully shutdown.", FileSource.class.getSimpleName());
    }
}
