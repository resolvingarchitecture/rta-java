package ra.rta.services.business.events;

import ra.rta.models.Envelope;
import ra.rta.models.Event;
import ra.rta.models.Partner;
import ra.rta.models.PartnerNotFoundException;

import java.util.Map;
import java.util.Set;

public class EventFactory {

    private Map<String,Partner> partners;

    public EventFactory(Map<String, Partner> partners) {
        this.partners = partners;
    }

    public Event build(String partnerName, Envelope envelope) throws Exception {
        Partner partner = partners.get(partnerName);
        if(partner==null) throw new PartnerNotFoundException("Partner with name "+partnerName+" either doesn't exist or is not active.");
        envelope.getBody().getRecords().get(0).setPartner(partner);
        String inboundCommand = envelope.getHeader().getCommand();
        inboundCommand = inboundCommand.toUpperCase();
        String eventStr = null;
        Set<String> commands = partner.getCommandMaps().keySet();
        for(String command : commands) {
            if(inboundCommand.contains(command))
                eventStr = partner.getCommandMaps().get(command);
        }
        if(eventStr==null) throw new Exception("Command mapping for command "+envelope.getHeader().getCommand()+" does not exist. Please add to ra.partner table.");
        BaseEvent event = (BaseEvent)Class.forName("ra.rta.services.business.events."+eventStr).getConstructor().newInstance();
        event.setEnvelope(envelope);
        event.getEntity().setPartner(partner);
        return event;
    }

}
