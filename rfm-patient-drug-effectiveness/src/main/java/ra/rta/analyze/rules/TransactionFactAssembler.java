package ra.rta.analyze.rules;

import ra.rta.models.Entity;
import ra.rta.models.FinancialTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TransactionFactAssembler {

    private TransactionFactAssembler () {
        //
    }

    /**
     * Add Transaction and entire object tree
     * @param financialTransaction
     * @return
     */
    public static List<Entity> getFacts(FinancialTransaction financialTransaction) {
        List<Entity> facts = new ArrayList<>();

        if (financialTransaction == null) return facts;

        facts.add(financialTransaction);

        return facts;
    }

}
