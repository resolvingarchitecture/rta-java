package ra.rta.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.Cluster;
import ra.rta.models.Event;

import java.util.Map;

public class TransformerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TransformerFactory.class);

    private Map<Long, Cluster> allActiveGroups;
    private Map<Long,Map<Integer, Map<String,Map<String,Object>>>> transformMap;

    public TransformerFactory(Map<Long, Cluster> allActiveGroups, Map<Long,Map<Integer, Map<String,Map<String,Object>>>> transformMap) throws Exception {
        this.allActiveGroups = allActiveGroups;
        this.transformMap = transformMap;
    }

    public Transformer build(Event event) throws Exception {
        String className = allActiveGroups.get(event.groupId).formatters.get(event.getClass().getSimpleName());
        if(className==null) {
            throw new Exception("Transformer class not found for event: "+event.getClass().getSimpleName());
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
