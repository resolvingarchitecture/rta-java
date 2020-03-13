package ra.rta.services.data;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import ra.rta.models.DepositTransaction;
import ra.rta.models.Transaction;
import ra.rta.models.utilities.DateUtility;
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

    public void loadSuspended(Transaction t) throws Exception {
        String type = null;
        if(t instanceof DepositTransaction) type = "deposit";
        else throw new Exception("Transaction type not supported.");
        String cql =  "SELECT * FROM "+t.getPartner().getName()+"."+type+"_transaction WHERE uaic='"+t.getUaId()+"' AND date='"+DateUtility.timestampToString(t.getDate())+"';";
        ResultSet rs = session.execute(cql);
        Row row = rs.one();
        if(row!=null){
            t.setAdId(row.getUUID("adic"));
            t.setUhId(row.getString("uhic"));
            t.setStatus(Transaction.Status.values()[row.getInt("status")]);
            t.setPostDate(new Date(row.getDate("post_date").getMillisSinceEpoch()));
            t.setProcessDate(new Date(row.getDate("process_date").getMillisSinceEpoch()));
            t.setAmount(row.getDouble("amount"));
            t.setMemo(row.getString("memo"));
            t.setType(Transaction.Type.values()[row.getInt("type")]);
            t.setCategory(Transaction.Category.values()[row.getInt("category")]);
            t.setPayee(row.getString("payee"));
            if(t instanceof DepositTransaction)
                ((DepositTransaction)t).setCheckNumber(row.getInt("check_number"));
        }
    }

	public void save(DepositTransaction t) throws Exception {
        PreparedStatement stmt = session.prepare("INSERT INTO " + t.getPartner().getName() + ".deposit_transaction (id, uaic, date, adic, status, post_date, process_date, amount, memo, type, category, check_number, payee) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        BoundStatement boundStmt = new BoundStatement(stmt);
        boundStmt.bind(t.getId(), t.getUaId(), t.getDate(), t.getAdId(), t.getStatus().ordinal(), t.getPostDate(),
                t.getProcessDate(), t.getAmount(), t.getMemo(), t.getType().ordinal(), t.getCategory().ordinal(),
                t.getCheckNumber(), t.getPayee());
        session.execute(boundStmt);
    }

    public void suspend(Transaction t) throws Exception {
        String type = null;
        if(t instanceof DepositTransaction) type = "deposit";
        else throw new Exception("Transaction Subtype not supported");
        String cql = "INSERT INTO " + t.getPartner().getName() + ".suspend_transaction (uaic, date, type) VALUES ('"
                + t.getUaId() + "', '" + DateUtility.timestampToString(t.getDate()) + "', '" + type + "');";
        session.execute(new SimpleStatement(cql));
        t.setSuspended(true);
    }

    public List<Transaction> getSuspended(String partnerName) throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        String cql = "SELECT * FROM "+partnerName+".suspend_transaction;";
        ResultSet rows = session.execute(cql);
        Transaction transaction;
        for(Row row : rows){
            String uaic = row.getString("uaic");
            String dateStr = row.getString("date");
            String type = row.getString("type");
            if("deposit".equals(type)) {
                transaction = new DepositTransaction();
            } else {
                throw new Exception("Entity type ("+type+") not yet supported.");
            }
            transaction.setUaId(uaic);
            transaction.setDate(DateUtility.timestampStringToDate(dateStr));
            transaction.setSuspended(true);
            transactions.add(transaction);
        }
        return transactions;
    }

    public void unsuspend(Transaction t) throws Exception {
        String cql = "DELETE " + t.getPartner().getName() + ".suspend_transaction WHERE uaic='"
                + t.getUaId() + "' AND date='" + DateUtility.timestampToString(t.getDate()) + "');";
        session.execute(new SimpleStatement(cql));
        t.setSuspended(false);
        save((DepositTransaction)t);
    }

}
