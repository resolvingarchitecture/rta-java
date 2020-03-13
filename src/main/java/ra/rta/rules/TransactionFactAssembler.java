package ra.rta.rules;

import ra.rta.models.Entity;
import ra.rta.models.Transaction;

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
     * @param transaction
     * @return
     */
    public static List<Entity> getFacts(Transaction transaction) {
        List<Entity> facts = new ArrayList<>();

        if (transaction == null) return facts;

        facts.add(transaction);

        return facts;
    }

}
