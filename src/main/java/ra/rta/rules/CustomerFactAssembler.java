package ra.rta.rules;

import java.util.ArrayList;
import java.util.List;

import ra.rta.models.Account;
import ra.rta.models.Application;
import ra.rta.models.Customer;
import ra.rta.models.Entity;

public class CustomerFactAssembler {

	private CustomerFactAssembler() {
		//
	}

	public static List<Entity> getFacts(Customer customer) {
		List<Entity> facts = new ArrayList<>();

		if (customer == null) {
			return facts;
		}

		// Add Customer and entire object tree
		facts.add(customer);
		// Now walk down tree adding each node directly so that the entire tree
		// is also flattened

		// Add Partner
		facts.add(customer.getPartner());

		// Add Applications
		for (Application application : customer.getApplications()) {
			facts.add(application);
		}

		// Add Accounts
		for (Account account : customer.getAccounts()) {
			facts.add(account);
		}
		return facts;
	}

}
