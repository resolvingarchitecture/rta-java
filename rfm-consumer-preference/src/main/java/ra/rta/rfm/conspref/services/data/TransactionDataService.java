package ra.rta.rfm.conspref.services.data;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import ra.rta.rfm.conspref.models.FinancialTransaction;
import ra.rta.rfm.conspref.utilities.DateUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionDataService extends BaseDataService {

	TransactionDataService(Session session) {
		super(session);
	}

    private Logger LOG = LoggerFactory.getLogger(TransactionDataService.class);

    public void loadSuspended(FinancialTransaction t) throws Exception {
        String type = null;
        String cql =  "SELECT * FROM "+t.getPartner().getName()+"."+type+"_transaction WHERE uaid='"+t.getUaId()+"' AND date='"+DateUtility.timestampToString(t.getDate())+"';";
        ResultSet rs = session.execute(cql);
        Row row = rs.one();
        if(row!=null){
            t.setAdId(row.getUUID("adid"));
            t.setUhId(row.getString("uhid"));
            t.setStatus(FinancialTransaction.Status.values()[row.getInt("status")]);
            t.setPostDate(new Date(row.getDate("post_date").getMillisSinceEpoch()));
            t.setProcessDate(new Date(row.getDate("process_date").getMillisSinceEpoch()));
            t.setAmount(row.getDouble("amount"));
            t.setMemo(row.getString("memo"));
            t.setType(FinancialTransaction.Type.values()[row.getInt("type")]);
            t.setCategory(FinancialTransaction.Category.values()[row.getInt("category")]);
            t.setPayee(row.getString("payee"));
        }
    }

	public void save(FinancialTransaction t) throws Exception {
        PreparedStatement stmt = session.prepare("INSERT INTO " + t.getPartner().getName() + ".transaction (id, uaid, date, adid, status, post_date, process_date, amount, memo, type, category, check_number, payee) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        BoundStatement boundStmt = new BoundStatement(stmt);
        boundStmt.bind(t.getId(), t.getUaId(), t.getDate(), t.getAdId(), t.getStatus().ordinal(), t.getPostDate(),
                t.getProcessDate(), t.getAmount(), t.getMemo(), t.getType().ordinal(), t.getCategory().ordinal(), t.getPayee());
        session.execute(boundStmt);
    }

    public void suspend(FinancialTransaction t) throws Exception {
        String type = null;
        String cql = "INSERT INTO " + t.getPartner().getName() + ".suspend_transaction (uaid, date, type) VALUES ('"
                + t.getUaId() + "', '" + DateUtility.timestampToString(t.getDate()) + "', '" + type + "');";
        session.execute(new SimpleStatement(cql));
        t.setSuspended(true);
    }

    public List<FinancialTransaction> getSuspended(String partnerName) throws Exception {
        List<FinancialTransaction> financialTransactions = new ArrayList<>();
        String cql = "SELECT * FROM "+partnerName+".suspend_transaction;";
        ResultSet rows = session.execute(cql);
        FinancialTransaction financialTransaction;
        for(Row row : rows){
            String uaid = row.getString("uaid");
            String dateStr = row.getString("date");
            String type = row.getString("type");
            // TODO: Instantiate concrete Transaction
//            transaction.setUaId(uaid);
//            transaction.setDate(DateUtility.timestampStringToDate(dateStr));
//            transaction.setSuspended(true);
//            transactions.add(transaction);
        }
        return financialTransactions;
    }

    public void unsuspend(FinancialTransaction t) throws Exception {
        String cql = "DELETE " + t.getPartner().getName() + ".suspend_transaction WHERE uaic='"
                + t.getUaId() + "' AND date='" + DateUtility.timestampToString(t.getDate()) + "');";
        session.execute(new SimpleStatement(cql));
        t.setSuspended(false);
        save(t);
    }

}
