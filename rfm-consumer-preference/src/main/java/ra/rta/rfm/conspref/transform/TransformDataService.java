package ra.rta.rfm.conspref.transform;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import ra.rta.persistence.cassandra.BaseDataService;

import java.util.*;

public class TransformDataService extends BaseDataService {

    TransformDataService(Session session) {
        super(session);
    }

    public Map<String, Map<String,Map<String,Object>>> getPartnerTransformMaps(String partnerName) {
        Map<String, Map<String,Map<String,Object>>> transformMaps = new HashMap<>();
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM "+partnerName+".transform"));
        Iterator<Row> i = rs.iterator();
        while(i.hasNext()) {
            Row row = i.next();
            String objectName = row.getString("object_name");
            Map<String,Map<String,Object>> objectMap = transformMaps.get(objectName);
            if(objectMap==null){
                objectMap = new HashMap<>();
                transformMaps.put(objectName, objectMap);
            }
            String fieldName = row.getString("field_name");
            Map<String,Object> fieldMap = objectMap.get(fieldName);
            if(fieldMap==null) {
                fieldMap = new HashMap<>();
                objectMap.put(fieldName, fieldMap);
            }
            Integer selectStartPosition = row.getInt("select_start_position");
            fieldMap.put("select_start_position", selectStartPosition);
            Integer selectEndPosition = row.getInt("select_end_position");
            fieldMap.put("select_end_position", selectEndPosition);
            Integer fieldOrder = row.getInt("field_order");
            fieldMap.put("field_order",fieldOrder);
            Map<String,String> substitutionMap = row.getMap("substitution_map", String.class, String.class);
            fieldMap.put("substitution_map", substitutionMap);
            String splitOn = row.getString("split_on");
            fieldMap.put("split_on", splitOn);
            String entityName = row.getString("entity_name");
            fieldMap.put("entity_name", entityName);
            String attributeName = row.getString("attribute_name");
            fieldMap.put("attribute_name", attributeName);
        }
        return transformMaps;
    }
}
