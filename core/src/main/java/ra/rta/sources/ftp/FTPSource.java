package ra.rta.sources.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import ra.rta.models.Event;
import ra.rta.sources.MessageManager;
import ra.rta.utilities.JSONUtil;
import ra.rta.utilities.RandomUtil;

/**
 *
 */
public class FTPSource extends DefaultFtplet implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(FTPSource.class);

	private FtpletContext ftpletContext;
	private MessageManager messageManager;
	private int groupId = 0;
	private int command = 0;
	private String topic;
	private boolean durable = false;
	private int batchSize = 1;
	private int port;

	public FTPSource(int port, int batchSize, String messageBrokerList, int groupId, int command, String topic, boolean durable) {
		this.port = port;
		this.batchSize = batchSize;
		this.groupId = groupId;
		this.command = command;
		this.topic = topic;
		this.durable = durable;
		Map<String,Object> args = new HashMap<>();
		args.put("topology.kafka.broker.list", messageBrokerList);
		messageManager = new MessageManager(args);
	}

	public void init(String... args) {
		Map<String,Object> params = new HashMap<>();
		port = Integer.parseInt(args[0]);
		batchSize = Integer.parseInt(args[1]);
		params.put("topology.kafka.broker.list", args[2]);
		groupId = Integer.parseInt(args[3]);
		command = Integer.parseInt(args[4]);
		topic = args[5];
		durable = "durable".equals(args[6]);
		messageManager = new MessageManager(params);
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
			int current = 0;
			List<Event> batch = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				Event event = new Event();
				event.command = command;
				event.id = RandomUtil.nextRandomLong();
				event.groupId = groupId;
				event.payload.put("topic",topic);
				event.payload.put("durable",durable);
				event.payload.put("line",line);
				current++;
				if(current==batchSize) {
					if(batchSize>1) {
						messageManager.send(topic, JSONUtil.MAPPER.writeValueAsBytes(batch), durable);
						batch.clear();
					} else {
						messageManager.send(topic, JSONUtil.MAPPER.writeValueAsBytes(event), durable);
					}
					current = 0;
					LOG.info(".");
				} else {
					batch.add(event);
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
