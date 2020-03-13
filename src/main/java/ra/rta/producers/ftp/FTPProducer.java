package ra.rta.producers.ftp;

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
import ra.rta.producers.MessageManager;

/**
 *
 */
public class FTPProducer extends DefaultFtplet implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(FTPProducer.class);

	private FtpletContext ftpletContext;
	private MessageManager messageManager;
	private String transactionIdentifier;
	private String topicIdentifier;
	private int messageChunk = 1;
	private int port;

	public FTPProducer(int port, int messageChunk, String messageBrokerList, String transactionIdentifier, String topicIdentifier) {
		this.port = port;
		this.messageChunk = messageChunk;
		this.transactionIdentifier = transactionIdentifier;
		this.topicIdentifier = topicIdentifier;
		Map<String,Object> args = new HashMap<>();
		args.put("brokerList", messageBrokerList);
		messageManager = new MessageManager(args);
	}

	public static void main(String... args) {
		int port = Integer.parseInt(args[0]);
		int messageChunk = Integer.parseInt(args[1]);
		String messagingBrokerList = args[2];
		String transactionIdentifier = args[3];
		String topicIdentifier = args[4];

		// TODO: Register Ftplet with server to submit FTP events to Kafka
		FTPProducer producer = new FTPProducer(port, messageChunk, messagingBrokerList, transactionIdentifier, topicIdentifier);
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
				if(current==messageChunk) {
					messageManager.send(determineTopic(line), sb.toString(), !line.matches(transactionIdentifier));
					sb = new StringBuilder();
				}
			}
		} catch (IOException e) {
			LOG.warn(e.getLocalizedMessage());
		}
		return FtpletResult.DEFAULT;
	}

	private String determineTopic(String line) {
		return line.contains("transaction") ? "transaction" : "reference";
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
