package ra.rta.services.business.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.Event;
import ra.rta.models.Group;
import ra.rta.models.Transformer;

import java.util.HashMap;
import java.util.Map;

public class TransformerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TransformerFactory.class);

    private Map<Long, Group> allActivePartners;
    private Map<Long,Map<Integer, Map<String,Map<String,Object>>>> transformMap;

    public TransformerFactory(Map<Long, Group> allActivePartners, Map<Long,Map<Integer, Map<String,Map<String,Object>>>> transformMap) throws Exception {
        this.allActivePartners = allActivePartners;
        this.transformMap = transformMap;
    }

    public Transformer build(Event event) throws Exception {
        String className = allActivePartners.get(event.groupId).formatters.get(event.getClass().getSimpleName());
        if(className==null) {
            throw new Exception("Transformer class not found for event: "+event.getClass().getSimpleName()+". Please add to ra.group.formatters");
        } else {
            BaseTransformer transformer = (BaseTransformer)Class.forName("ra.rta.services.business.tasks."+className).getConstructor().newInstance();
            Map<String,Map<String,Object>> fieldMetaMap = transformMap.get(event.groupId).get(event.command);
            if(fieldMetaMap==null) {
                String errMsg = "Unable to find fieldMetaMap in transformMap for group (id="+event.groupId+") and command="+event.command;
                LOG.error(errMsg);
                throw new Exception(errMsg);
            }
            transformer.setFieldMetaMap(fieldMetaMap);
            return transformer;
        }
    }

}
