package ra.rta.services.business.tasks;

import ra.rta.transform.BaseTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CSVTransformer extends BaseTransformer {

    @Override
    protected void select() throws Exception {
        List<String> rawValues = null;
        for(String fieldName : fieldMetaMap.keySet()) {
            Map<String,Object> fieldMetaPropertyMap = fieldMetaMap.get(fieldName);
            int startPosition = (int) fieldMetaPropertyMap.get("select_start_position");
            if(rawValues == null) {
                String delimiter = (String) fieldMetaPropertyMap.get("type");
                rawValues = Arrays.asList(raw.split(delimiter));
            }
            try {
                fieldNameValues.put(fieldName, rawValues.get(startPosition));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
