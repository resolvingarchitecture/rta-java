package ra.rta.producers.timer.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ra.rta.models.Envelope;
import ra.rta.models.Partner;
import ra.rta.models.Record;
import ra.rta.models.Transaction;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.producers.MessageManager;
import ra.rta.services.data.DataServiceManager;
import ra.rta.services.data.TransactionDataService;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class SuspendReprocessJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(SuspendReprocessJob.class.getSimpleName());

    public static final String KAFKA_TOPIC = "kafkaTopic";
    public static final String KAFKA_CONFIG = "kafkaConfig";
    public static final String KAFKA_PRODUCER = "kafkaProducer";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            reprocess(jobExecutionContext.getMergedJobDataMap());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }

    }

    public void reprocess(JobDataMap map) throws Exception {
        String msg = "Transaction Suspend Reprocess Job initiated...";
        LOG.info(msg);
        MessageManager messageManager = (MessageManager) map.get(KAFKA_PRODUCER);
        String topic = (String) map.get(KAFKA_TOPIC);
        DataServiceManager dataServiceManager = (DataServiceManager)map.get(DataServiceManager.class.getSimpleName());
        Map<String, Partner> activePartners = dataServiceManager.getPartnerDataService().getAllActivePartnersMap();
        TransactionDataService transactionDataService = dataServiceManager.getTransactionDataService();
        for(String partnerName : activePartners.keySet()) {
            List<Transaction> suspendedTransactions = transactionDataService.getSuspended(partnerName);
            for (Transaction transaction : suspendedTransactions) {
                Envelope envelope = new Envelope();
                envelope.getHeader().setCommand("TransactionSuspendReprocess");
                Record record = new Record();
                envelope.getBody().getRecords().add(record);
                record.getPartner().setName(partnerName);
                record.setTransaction(transaction);
                record.setTransformed(true);
                transactionDataService.loadSuspended(transaction);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                MAPPER.writeValue(os, envelope);
                LOG.info(".");
                messageManager.send(topic, new String(os.toByteArray()), false);
                msg = "Transaction Suspend Reprocess Job service operation signal sent.";
                LOG.info(msg);
            }
        }
    }
}
