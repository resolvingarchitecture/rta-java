package ra.rta.services.business.tasks;

import ra.rta.models.*;
import ra.rta.models.utilities.Generator;

import java.util.UUID;

public class ADIDEnricher implements Enricher {

	@Override
	public void enrich(EnrichableEvent event) throws Exception {
		Entity entity = event.getEntity();
		if(entity instanceof CustomerRelated) {
			CustomerRelated customerRelatedEntity = (CustomerRelated)entity;
            Customer customer = customerRelatedEntity.getCustomer();
			UUID ucid = customer.getUcId();
			if(ucid != null) {
				// UCID provided therefore just generate
                customer.setAdId(UUID.fromString(Generator.adid(entity.getPartner().getName(), ucid.toString())));
				customer.setUcId(null);
			}
		}
	}

}
