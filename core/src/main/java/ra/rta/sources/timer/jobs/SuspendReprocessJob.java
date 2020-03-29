package ra.rta.rfm.conspref.sources.timer.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import ra.rta.rfm.conspref.models.Envelope;
import ra.rta.rfm.conspref.models.FinancialTransaction;
import ra.rta.rfm.conspref.models.Cluster;
import ra.rta.rfm.conspref.models.Record;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.rfm.conspref.sources.MessageManager;
import ra.rta.persistence.PersistenceManager;
import ra.rta.persistence.cassandra.TransactionDataService;

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
        PersistenceManager persistenceManager = (PersistenceManager)map.get(PersistenceManager.class.getSimpleName());
        Map<String, Cluster> activePartners = persistenceManager.getGroupDataService().getAllActivePartnersMap();
        TransactionDataService transactionDataService = persistenceManager.getTransactionDataService();
        for(String partnerName : activePartners.keySet()) {
            List<FinancialTransaction> suspendedFinancialTransactions = transactionDataService.getSuspended(partnerName);
            for (FinancialTransaction financialTransaction : suspendedFinancialTransactions) {
                Envelope envelope = new Envelope();
                envelope.getHeader().setCommand("TransactionSuspendReprocess");
                Record record = new Record();
                envelope.getBody().getRecords().add(record);
                record.getGroup().setName(partnerName);
                record.setTransaction(financialTransaction);
                record.setTransformed(true);
                transactionDataService.loadSuspended(financialTransaction);
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
