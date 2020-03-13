package ra.rta.services.data;

import com.datastax.driver.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.*;

import java.util.*;

public class AccountDataService extends BaseDataService {

    private static Logger LOG = LoggerFactory.getLogger(AccountDataService.class);

    public AccountDataService(Session session) {
        super(session);
    }

    public Set<Account> findDepositAccounts(Partner partner, String uaic, String processDate) throws Exception {
        CustomerDataService customerDataService = new CustomerDataService(session);
        Set<Account> accounts = new HashSet<>();
        DepositAccount account = null;
        String cql = "SELECT * FROM " + partner.getName() + ".deposit_account WHERE uaic='" + uaic + "' and process_date='" + processDate + "';";
        ResultSet rows = session.execute(new SimpleStatement(cql));
        for (Row row : rows) {
            account = new DepositAccount();
            account.setaId(uaic);
            account.setUuid(row.getUUID("adic"));
            account.setProcessDate(new Date(row.getDate("process_date").getMillisSinceEpoch()));
            account.sethId(row.getString("uhic"));
            account.setOpenDate(new Date(row.getDate("open_date").getMillisSinceEpoch()));
            account.setCloseDate(new Date(row.getDate("close_date").getMillisSinceEpoch()));
            account.setReltype(Relationship.values()[row.getInt("customer_relationship_type")]);
            account.setType(row.getString("type"));
            account.setEstatementIndicator(row.getBool("estatement_indicator"));
            account.setBalance(row.getFloat("balance"));
            account.setAvailableBalance(row.getFloat("available_balance"));
            account.setMaturityDate(new Date(row.getDate("maturity_date").getMillisSinceEpoch()));
            account.setRenewalDate(new Date(row.getDate("renewal_date").getMillisSinceEpoch()));
            account.setRenewalTimes(row.getInt("renewal_times"));
            account.setOverdraftProtection(row.getBool("overdraft_protection"));
            account.setInterestRate(row.getFloat("interest_rate"));
            account.setInterestEarnedYTD(row.getFloat("interest_earned_ytd"));
            account.setPrevYearInterest(row.getFloat("prev_year_interest"));
            account.setFeesPaidYTD(row.getFloat("fees_paid_ytd"));
            account.setFeesPaidMTD(row.getFloat("fees_paid_mtd"));
            account.setFeesWaivedYTD(row.getFloat("fees_waived_ytd"));
            account.setFeesWaivedMTD(row.getFloat("fees_waived_mtd"));
            account.setLastOverdraftDate(new Date(row.getDate("last_overdraft_date").getMillisSinceEpoch()));
            account.setNumOverdrawnYTD(row.getFloat("num_overdrawn_ytd"));
            account.setNumOverdrawnMTD(row.getFloat("num_overdrawn_mtd"));
            account.setStatus(DepositAccount.Status.values()[row.getInt("account_status_code")]);
            account.setStatusDate(new Date(row.getDate("account_status_date").getMillisSinceEpoch()));
            account.setSubtypeCode1(row.getString("account_subtype_code1"));
            account.setSubtypeCode2(row.getString("account_subtype_code2"));
            account.setSubtypeCode3(row.getString("account_subtype_code3"));
            Customer customer = new Customer();
            customer.setPartner(partner);
            customer.setAdId(account.getUuid());
            customerDataService.loadCustomer(customer, processDate);
            account.setCustomer(customer);
            accounts.add(account);
        }
        return accounts;
    }

}
