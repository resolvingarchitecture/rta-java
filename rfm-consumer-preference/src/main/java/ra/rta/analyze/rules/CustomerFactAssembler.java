package ra.rta.analyze.rules;

import java.util.ArrayList;
import java.util.List;

import ra.rta.models.Application;
import ra.rta.models.Individual;
import ra.rta.models.Entity;

public class CustomerFactAssembler {

	private CustomerFactAssembler() {
		//
	}

	public static List<Entity> getFacts(Individual individual) {
		List<Entity> facts = new ArrayList<>();

		if (individual == null) {
			return facts;
		}

		// Add Customer and entire object tree
		facts.add(individual);
		// Now walk down tree adding each node directly so that the entire tree
		// is also flattened

		// Add Partner
		facts.add(individual.getGroup());

		// Add Applications
		for (Application application : individual.getApplications()) {
			facts.add(application);
		}

		return facts;
	}

}
