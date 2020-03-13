package ra.rta.rules;

import ra.rta.models.Account;
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
        // Now walk down tree adding each node directly so that the entire tree
        // is also flattened

        // Add Account
        facts.addAll(transaction.getAccounts());

        // Add Customers
        for(Account account : transaction.getAccounts()) {
            facts.add(account.getCustomer());
        }

        return facts;
    }

}
