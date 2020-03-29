package ra.rta.rfm.conspref.sources.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.rfm.conspref.sources.MessageManager;

/**
 *
 */
public class FTPProducer extends DefaultFtplet implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(FTPProducer.class);

	private FtpletContext ftpletContext;
	private boolean durable;
	private String topic;
	private MessageManager messageManager;
	private int batchSize = 1;
	private int port;

	public FTPProducer(String topic, int port, int batchSize, String messageBrokerList, boolean durable) {
		this.topic = topic;
		this.port = port;
		this.batchSize = batchSize;
		this.durable = durable;
		Map<String,Object> args = new HashMap<>();
		args.put("brokerList", messageBrokerList);
		messageManager = new MessageManager(args);
	}

	public static void main(String... args) {
		int port = Integer.parseInt(args[0]);
		int messageChunk = Integer.parseInt(args[1]);
		String messagingBrokerList = args[2];
		String transactionIdentifier = args[3];
		String topic = args[4];
		boolean durable = Boolean.parseBoolean(args[5]);
		FTPProducer producer = new FTPProducer(topic, port, messageChunk, messagingBrokerList, durable);
		producer.run();
	}

	@Override
	public void run() {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory listenerFactory = new ListenerFactory();
		listenerFactory.setPort(port);
		// define SSL configuration
		//        SslConfigurationFactory ssl = new SslConfigurationFactory();
		//        ssl.setKeystoreFile(new File("src/test/resources/ftpserver.jks"));
		//        ssl.setKeystorePassword("password");
		// set the SSL configuration for the listener
		//        factory.setSslConfiguration(ssl.createSslConfiguration());
		//        factory.setImplicitSsl(true);
		serverFactory.addListener("default",listenerFactory.createListener());
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(new File("users.properties"));
		serverFactory.setUserManager(userManagerFactory.createUserManager());
		FtpServer server = serverFactory.createServer();
		try {
			server.start();
		} catch (FtpException e) {
			LOG.error("FTPException from FTP Server: " + e);
		}
	}

	@Override
	public void init(FtpletContext ftpletContext) {
		this.ftpletContext = ftpletContext;
	}

	@Override
	public FtpletResult onLogin(FtpSession session, FtpRequest request) {
		File userRoot = new File(session.getUser().getHomeDirectory());
		if(userRoot.mkdirs())
			return FtpletResult.DEFAULT;
		else
			return FtpletResult.NO_FTPLET;
	}

	@Override
	public FtpletResult onMkdirStart(FtpSession session, FtpRequest request) throws FtpException {
		session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "Directory creation on this server not allowed."));
		return FtpletResult.SKIP;
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException {
		String userRoot = session.getUser().getHomeDirectory();
		String currDir = session.getFileSystemView().getWorkingDirectory().getAbsolutePath();
		String fileName = request.getArgument();
		File file = new File(userRoot + currDir + fileName);
		try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
			String line;
			StringBuilder sb = new StringBuilder();
			int current = 0;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				current++;
				if(current==batchSize) {
					messageManager.send(topic, sb.toString(), durable);
					sb = new StringBuilder();
				}
			}
		} catch (IOException e) {
			LOG.warn(e.getLocalizedMessage());
		}
		return FtpletResult.DEFAULT;
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
