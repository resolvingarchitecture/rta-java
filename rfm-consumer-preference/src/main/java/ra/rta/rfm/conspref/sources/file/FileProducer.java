package ra.rta.rfm.conspref.sources.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.rfm.conspref.sources.MessageManager;

public class FileProducer implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(FileProducer.class);

	private Map<String,Object> args;

	public void init(Map<String,Object> args) {
	    this.args = args;
    }

	public void run() {
		LOG.info("{} starting...", FileProducer.class.getSimpleName());
		try {
            // 16,000,000 transactions in 1 message chunks in 45 minutes with 2013 Macbook Pro using 150Mb
            // 16,000,000 transactions in 100 message chunks in 3 minutes with 2013 Macbook Pro using 300Mb
            // 16,000,000 transactions in 10,000 message chunks in 2 minutes with 2013 Macbook Pro using 1.3Gb
            Integer groupId = (Integer)args.get("groupId");
            String topic = (String)args.get("topic");
            String kafkaURLList = (String)args.get("brokerList");
            String inboundFolder = (String)args.get("inboundFolder");
            String archiveFolder =(String) args.get("archiveFolder");

            LOG.info("Group.id=" + groupId);
            MessageManager messageManager = new MessageManager(args);

            // Register Shutdown Hook with File Splitter list
            List<FileSplitter> fileSplitters = new ArrayList<>();
            Runtime.getRuntime().addShutdownHook(new FileProducerShutdown(fileSplitters, messageManager));

            // Get source and archive folder paths
            Path sourceDir = Paths.get(inboundFolder);
            Path archiveDir = null;
            if (archiveFolder != null) archiveDir = Paths.get(inboundFolder);

            // Check to see if there are any files in the folder to watch indicating that a failure took place.
            List<Path> oldFiles = new ArrayList<>();
            DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDir);
            for (Path entry : stream) {
                oldFiles.add(entry);
            }
            stream.close();
            if (oldFiles.size() > 0) {
                for (Path path : oldFiles) {
                    // Resume from failure point
                    Path dataFile = null;
                    Path markerFile = null;
                    // Find marker and data files
                    int startingLine = 0;
                    // Only look for marker files
                    if (path.toString().contains(".mkr")) {
                        // Marker file
                        markerFile = path;
                        // Find corresponding data file
                        dataFile = sourceDir.resolve(Paths.get(markerFile.toString().substring(0, markerFile.toString().lastIndexOf("."))));
                        if (dataFile == null) {
                            // Corresponding data file not found therefore move marker file to archive directory
                            Files.move(markerFile, archiveDir.resolve(markerFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            // Read marker file's recording of last line processed and add one to it
                            startingLine = Integer.parseInt(new String(Files.readAllBytes(markerFile))) + 1;
                            String msg = "Resuming " + dataFile.toString() + " from failure with starting line: " + startingLine;
                            LOG.info(msg);
                            // Fast Forward processing of data file to next line to process and continue processing
                            FileSplitter fileSplitter = new FileSplitter(groupId, topic, dataFile.getFileName(), markerFile.getFileName(), startingLine, sourceDir, archiveDir, messageManager);
                            fileSplitters.add(fileSplitter);
                            fileSplitter.start();
                        }
                    }
                }
            }

            // Create Folder WatchService on source directory and its sub-directories.
            final WatchService watcher = sourceDir.getFileSystem().newWatchService();
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dir.toString().contains("processed")) {
                        dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

//			WatchKey key = sourceDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey key;
            for (; ; ) {

                // wait for key to be signaled
                try {
                    String msg = "Listening on folder: " + inboundFolder + " and its children (except any that contain 'processed' in its name).";
                    LOG.info(msg);
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // This key is registered only
                    // for ENTRY_CREATE events,
                    // but an OVERFLOW event can
                    // occur regardless if events
                    // are lost or discarded.
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    // The filename is the
                    // context of the event.
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path dateFileName = ev.context();
                    LOG.info("File found : {}", dateFileName);
                    String dataFileNameStr = dateFileName.toString();
                    String fileExtension = dataFileNameStr.substring(dataFileNameStr.lastIndexOf("."));
                    if (!(fileExtension.equals(".csv") || fileExtension.equals(".txt"))) {
                        LOG.info("File {} not csv or text.", dateFileName);
                        continue;
                    }
                    // Only process Transactions for now
                    if (!dataFileNameStr.toUpperCase().contains("TRANSACTION") && !dataFileNameStr.toUpperCase().contains("TXN")) {
                        LOG.info("File {} not a transaction. Only processing transaction files currently.", dateFileName);
                        continue;
                    }
                    File markerFile = new File(dateFileName.toString() + ".mkr");
                    markerFile.createNewFile();
                    Path markerFileName = markerFile.toPath();
                    FileSplitter fileSplitter = new FileSplitter(groupId, topic, dateFileName, markerFileName, 0, sourceDir, archiveDir, messageManager);
                    fileSplitters.add(fileSplitter);
                    fileSplitter.start();
                }

                // Reset the key -- this step is critical if you want to
                // receive further watch events.  If the key is no longer valid,
                // the directory is inaccessible so exit the loop.
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException ex) {
            LOG.error("IOException in " + FileProducer.class.getSimpleName() +": "+ ex);
            return;
        }
        LOG.info("{} normal shutdown completed.", FileProducer.class.getSimpleName());
	}

}
