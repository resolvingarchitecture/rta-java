package ra.rta.services.business.tasks;

import java.util.*;

import ra.rta.models.*;
import ra.rta.models.utilities.DateUtility;
import ra.rta.services.data.DataServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositTransactionEnricher implements Enricher {

    private static Logger LOG = LoggerFactory.getLogger(DepositTransactionEnricher.class);

	@Override
	public void enrich(EnrichableEvent event) throws Exception {
        DepositTransaction transaction = (DepositTransaction)event.getEntity();
        transaction.setId(UUID.randomUUID());
        if(transaction.getPostDate()!=null) {
           transaction.setStatus(Transaction.Status.Processed);
        }
        Partner partner = transaction.getPartner();
        if(partner.getName() != null && transaction.getUaId() != null && transaction.getProcessDate() != null) {
            Date processDate = new Date(transaction.getProcessDate().getTime());
            String processDateString = DateUtility.timestampToSimpleDateString(processDate);
            short maxTries = 5;
            short tries = 0;
            Set<Account> depositAccounts = null;
            boolean loaded = false;
            do {
                depositAccounts = DataServiceManager.getAccountDataService().findDepositAccounts(partner, transaction.getUaId(), processDateString);
                if(depositAccounts.size() > 0) {
                    loaded = true;
                } else {
                    processDate = DateUtility.changeDate(processDate, -1); // Look back one day
                    tries++;
                }
            } while (!loaded && tries < maxTries);
            if (depositAccounts.size() == 0) {
                LOG.warn("No accounts found for transaction during enrichment.");
            } else {
                transaction.setAccounts(depositAccounts);
                Set<KPI> kpis = transaction.getKPIS();
                for (Account account : depositAccounts) {
                    for (KPI kpi : kpis) {
                        KPI kpiClone = (KPI) kpi.clone();
//                        int termcode = kpi.getTermcode();
                        kpiClone.setAdId(account.getUuid());
                        kpiClone.setDate(new Date(transaction.getDate().getTime()));
                        kpiClone.setRecency(DateUtility.dateToInt(transaction.getDate()));
                        kpiClone.setFrequency(1);
                        kpiClone.setMonetary(transaction.getAmount());

                        // Load yesterday's partner_KPI_rfm_windowed information for this KPI
//                        KPIPartnerSummary kpiPartnerSummary = DataServiceManager.getRfmSummaryDataService().getLatestKPIPartnerSummary(partner, termcode);
//                        kpiClone.setKPIPartnerSummary(kpiPartnerSummary);

                        // Load yesterday's customer_KPI_rfm_windowed information for this transaction KPI
//                        KPICustomerSummary kpiCustomerSummary = DataServiceManager.getRfmSummaryDataService().getLatestKPICustomerSummary(partner, account.getCustomer().getAdid(), termcode);
//                        kpiClone.setKPICustomerSummary(kpiCustomerSummary);
//                        account.getCustomer().addKPI(kpiClone);
                    }
                }
            }
        } else {
            LOG.warn("Partner name, transaction uaid, and transaction process date required to lookup accounts.");
        }
	}

}
