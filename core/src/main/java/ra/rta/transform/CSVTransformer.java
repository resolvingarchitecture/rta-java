package ra.rta.transform;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CSVTransformer extends BaseTransformer {

    @Override
    protected void select() throws Exception {
        String body = new String(raw);
        List<String> rawValues = null;
        for(String fieldName : fieldMetaMap.keySet()) {
            Map<String,Object> fieldMetaPropertyMap = fieldMetaMap.get(fieldName);
            int startPosition = (int) fieldMetaPropertyMap.get("select_start_position");
            if(rawValues == null) {
                String delimiter = (String) fieldMetaPropertyMap.get("type");
                rawValues = Arrays.asList(body.split(delimiter));
            }
            fieldNameValues.put(fieldName, rawValues.get(startPosition));
        }
    }
}
