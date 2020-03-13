package ra.rta.producers.ftp;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class FTPProducer extends DefaultFtplet {

	private static final Logger LOG = LoggerFactory.getLogger(FTPProducer.class);

	private FtpServer server;
	private Producer<String, String> transactionProducer;
	private Producer<String, String> referenceProducer;

	private static FTPProducer producer;

	public FTPProducer(int port, int messageChunk, String messageBrokerList) {
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
		server = serverFactory.createServer();
		try {
			server.start();
		} catch (FtpException e) {
			LOG.error("FTPException from FTP Server: " + e);
			e.printStackTrace();
		}


		Properties tProps = new Properties();
		tProps.put(ProducerConfig.CLIENT_ID_CONFIG, "TransactionProducer");
		tProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, messageBrokerList);
		tProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		tProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		tProps.put(ProducerConfig.ACKS_CONFIG, "1");
//        tProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "ra.rta.producers.utilities.partitioners.KafkaRoundRobinPartitioner");
		transactionProducer = new KafkaProducer<>(tProps);

		Properties dProps = new Properties();
		dProps.put(ProducerConfig.CLIENT_ID_CONFIG, "ReferenceProducer");
		dProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, messageBrokerList);
		dProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		dProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		dProps.put(ProducerConfig.ACKS_CONFIG, "-1");
//        dProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "ra.rta.producers.utilities.partitioners.KafkaRoundRobinPartitioner");
		referenceProducer = new KafkaProducer<>(dProps);

	}

	public static void main(String... args) {
		int port = Integer.parseInt(args[0]);
		int messageChunk = Integer.parseInt(args[1]);
		String messagingBrokerList = args[2];

		// TODO: Register Ftplet with server to submit FTP events to Kafka
		producer = new FTPProducer(port, messageChunk, messagingBrokerList);

	}

	@Override
	public void init(FtpletContext ftpletContext) throws FtpException {
		super.init(ftpletContext);
	}

	@Override
	public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {
		File userRoot = new File(session.getUser().getHomeDirectory());
		userRoot.mkdirs();
		return super.onLogin(session, request);
	}

	@Override
	public FtpletResult onMkdirStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
		session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "Directory creation on this server not allowed."));
		return FtpletResult.SKIP;
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		String userRoot = session.getUser().getHomeDirectory();
		String currDir = session.getFileSystemView().getWorkingDirectory().getAbsolutePath();
		String fileName = request.getArgument();
		File file = new File(userRoot + currDir + fileName);

		return FtpletResult.DEFAULT;
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
