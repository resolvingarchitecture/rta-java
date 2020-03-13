package ra.rta.services.business.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.Event;
import ra.rta.models.Partner;
import ra.rta.models.Transformer;

import java.util.HashMap;
import java.util.Map;

public class TransformerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TransformerFactory.class);

    private Map<String, Partner> allActivePartners;
    private Map<String,Map<String, Map<String,Map<String,Object>>>> transformMap = new HashMap<>();

    public TransformerFactory(Map<String, Partner> allActivePartners, Map<String,Map<String, Map<String,Map<String,Object>>>> transformMap) throws Exception {
        this.allActivePartners = allActivePartners;
        this.transformMap = transformMap;
    }

    public Transformer build(String partnerName, Event event) throws Exception {
        String className = allActivePartners.get(partnerName).getFormatters().get(event.getClass().getSimpleName());
        if(className==null) {
            throw new Exception("Transformer class not found for event: "+event.getClass().getSimpleName()+". Please add to sgmt.partner.formatters");
        } else {
            BaseTransformer transformer = (BaseTransformer)Class.forName("com.segmint.rt.services.business.tasks."+className).newInstance();
            String command = event.getEnvelope().getHeader().getCommand();
            Map<String,Map<String,Object>> fieldMetaMap = transformMap.get(partnerName).get(command);
            if(fieldMetaMap==null) {
                String errMsg = "Unable to find fieldMetaMap in transformMap for partner (name="+partnerName+") and command="+command;
                LOG.error(errMsg);
                throw new Exception(errMsg);
            }
            transformer.setFieldMetaMap(fieldMetaMap);
            return transformer;
        }
    }

}
