package ra.rta.sources.ftp;

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
import ra.rta.connectors.kafka.KafkaMgr;
import ra.rta.Event;
import ra.rta.utilities.JSONUtil;
import ra.rta.utilities.RandomUtil;

/**
 *
 */
public class FTPSource extends DefaultFtplet implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(FTPSource.class);

	private FtpletContext ftpletContext;
	private KafkaMgr kafkaMgr;
	private long sourceId = 0;
	private int commandId = 0;
	private String topic;
	private String payloadTransformerClass;
	private boolean durable = false;
	private int port;

	public FTPSource(int port,
					 int batchSize,
					 String messageBrokerList,
					 long sourceId,
					 int commandId,
					 String topic,
					 String payloadTransformerClass,
					 boolean durable) {
		this.port = port;
		this.sourceId = sourceId;
		this.commandId = commandId;
		this.topic = topic;
		this.payloadTransformerClass = payloadTransformerClass;
		this.durable = durable;
		Map<String,Object> args = new HashMap<>();
		args.put("topology.kafka.broker.list", messageBrokerList);
		kafkaMgr = new KafkaMgr(args);
	}

	public void init(String... args) {
		Map<String,Object> params = new HashMap<>();
		int i=0;
		port = Integer.parseInt(args[i++]);
		params.put("topology.kafka.broker.list", args[i++]);
		sourceId = Long.parseLong(args[i++]);
		commandId = Integer.parseInt(args[i++]);
		topic = args[i++];
		durable = "durable".equals(args[i++]);
		kafkaMgr = new KafkaMgr(params);
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
			while ((line = br.readLine()) != null) {
				Event event = new Event();
				event.id = RandomUtil.nextRandomLong();
				event.sourceId = sourceId;
				event.commandId = commandId;
				event.rawPayload = line.getBytes();
				event.payloadTransformerClass = payloadTransformerClass;
				kafkaMgr.send(topic, JSONUtil.MAPPER.writeValueAsBytes(event), durable);
				LOG.info(".");
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
