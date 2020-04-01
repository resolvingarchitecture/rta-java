package ra.rta.rfm.conspref.services;

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
        String cql =  "SELECT * FROM "+type+"_transaction WHERE gId="+t.gId+" and cId='"+t.cId+"' AND date='"+DateUtility.timestampToString(t.date)+"';";
        ResultSet rs = session.execute(cql);
        Row row = rs.one();
        if(row!=null){
            t.status = FinancialTransaction.Status.values()[row.getInt("status")];
            t.postDate = new Date(row.getDate("post_date").getMillisSinceEpoch());
            t.processDate = new Date(row.getDate("process_date").getMillisSinceEpoch());
            t.amount = row.getDouble("amount");
            t.memo = row.getString("memo");
            t.type = FinancialTransaction.Type.values()[row.getInt("type")];
            t.category = FinancialTransaction.Category.values()[row.getInt("category")];
            t.payee = row.getString("payee");
        }
    }

	public void save(FinancialTransaction t) throws Exception {
        PreparedStatement stmt = session.prepare("INSERT INTO transaction (gId, cId, id, date, status, post_date, process_date, amount, memo, type, category, payee) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        BoundStatement boundStmt = new BoundStatement(stmt);
        boundStmt.bind(t.gId, t.cId, t.id, t.date.getTime(), t.status.ordinal(), t.postDate.getTime(),
                t.processDate.getTime(), t.amount, t.memo, t.type.ordinal(), t.category.ordinal(), t.payee);
        session.execute(boundStmt);
    }

    public void suspend(FinancialTransaction t) {
        String cql = "INSERT INTO suspend_transaction (gId, cId, id, date, type) VALUES ("
                + t.gId + ", "+t.cId+", "+t.id+", " + t.date.getTime() + ", " + t.type.ordinal() + ");";
        session.execute(new SimpleStatement(cql));
        t.suspended = true;
    }

    public List<FinancialTransaction> getSuspended(int groupId) {
        List<FinancialTransaction> trxs = new ArrayList<>();
        String cql = "SELECT * FROM suspend_transaction where gId="+groupId+";";
        ResultSet rows = session.execute(cql);
        FinancialTransaction trx;
        for(Row row : rows){
            trx = new FinancialTransaction();
            trx.gId = groupId;
            trx.cId = row.getInt("cId");
            trx.id = row.getInt("id");
            trx.date = new Date(row.getLong("date"));
            trx.type = FinancialTransaction.Type.values()[row.getInt("type")];
            trx.suspended = true;
            trxs.add(trx);
        }
        return trxs;
    }

    public void unsuspend(FinancialTransaction t) throws Exception {
        String cql = "DELETE suspend_transaction WHERE gId="+t.gId+" AND id="+t.id+");";
        session.execute(new SimpleStatement(cql));
        t.suspended = false;
        save(t);
    }

}
