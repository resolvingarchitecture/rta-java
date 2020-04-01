package ra.rta.rfm.conspref.sources.timer.jobs;

import ra.rta.Event;
import ra.rta.connectors.kafka.KafkaMgr;
import ra.rta.rfm.conspref.models.FinancialTransaction;
import ra.rta.rfm.conspref.models.Record;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.rfm.conspref.services.DataServiceMgr;
import ra.rta.rfm.conspref.services.TransactionDataService;
import ra.rta.utilities.JSONUtil;

import java.util.List;

public class SuspendReprocessJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(SuspendReprocessJob.class.getSimpleName());

    public static final String KAFKA_TOPIC = "kafkaTopic";
    public static final String KAFKA_MGR = "kafkaMgr";

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
        KafkaMgr kafkaMgr = (KafkaMgr) map.get(KAFKA_MGR);
        String topic = (String) map.get(KAFKA_TOPIC);
        DataServiceMgr dataServiceMgr = (DataServiceMgr)map.get(DataServiceMgr.class.getSimpleName());
        List<Integer> activeGroups = dataServiceMgr.getGroupDataService().getAllActiveGroups();
        TransactionDataService transactionDataService = dataServiceMgr.getTransactionDataService();
        for(int gId : activeGroups) {
            List<FinancialTransaction> suspendedFinancialTransactions = transactionDataService.getSuspended(gId);
            for (FinancialTransaction trx : suspendedFinancialTransactions) {
                Event event = new Event();
                event.sourceId = 15; // SuspendReprocessJob
                event.commandId = 1200; // TransactionSuspendReprocess
                Record record = new Record();
                record.gId = gId;
                record.trx = trx;
                event.payload.put("record",record);
                LOG.info(".");
                kafkaMgr.send(topic, JSONUtil.MAPPER.writeValueAsBytes(event), false);
                msg = "Transaction Suspend Reprocess Job service operation signal sent.";
                LOG.info(msg);
            }
        }
    }
}
