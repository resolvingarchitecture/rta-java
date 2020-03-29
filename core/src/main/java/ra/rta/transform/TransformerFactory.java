package ra.rta.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.models.Event;

import java.util.Map;

public class TransformerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TransformerFactory.class);

    // Source Maps, Command Maps, Attribute Map
    public Map<Long,Map<Integer, Map<String,Map<String,Object>>>> transformMap;

    public Transformer build(Event event) throws Exception {
        BaseTransformer transformer = (BaseTransformer)Class.forName(event.payloadTransformerClass).getConstructor().newInstance();
        Map<String,Map<String,Object>> fieldMetaMap = transformMap.get(event.sourceId).get(event.commandId);
        if(fieldMetaMap==null) {
            String errMsg = "Unable to find fieldMetaMap in transformMap for source (id="+event.sourceId+") and command="+event.commandId;
            LOG.error(errMsg);
            throw new Exception(errMsg);
        }
        transformer.setFieldMetaMap(fieldMetaMap);
        return transformer;
    }

}
