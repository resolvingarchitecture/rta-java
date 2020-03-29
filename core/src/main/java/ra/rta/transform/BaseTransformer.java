package ra.rta.transform;

import java.util.HashMap;
import java.util.Map;

import ra.rta.models.Event;
import org.apache.commons.beanutils.converters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.utilities.DateUtility;

public abstract class BaseTransformer implements Transformer {

	static Logger LOG = LoggerFactory.getLogger(BaseTransformer.class);

	protected Map<String, Map<String, Object>> fieldMetaMap;
	protected Event event;
	protected Map<String, Object> fieldNameValues = new HashMap<>();
	protected Map<String, Object> fieldValuesMapped = new HashMap<>();

	public BaseTransformer() {
		DateConverter dateConverter = new DateConverter();
		dateConverter.setPatterns(DateUtility.knownFormats());
	}

	public void setFieldMetaMap(Map<String, Map<String, Object>> fieldMetaMap) {
		this.fieldMetaMap = fieldMetaMap;
	}

	@Override
	public final void transform(@SuppressWarnings("hiding") Event event) throws Exception {
		this.event = event;
		select();
		clean();
        concatenate();
		split();
		format();
		substitute();
		map();
	}

	protected abstract void select() throws Exception;

	protected void clean() throws Exception {
		Map<String, Object> fieldNameValuesCleaned = new HashMap<>();
		for(String fieldName : fieldNameValues.keySet()) {
            Object fieldValue = fieldNameValues.get(fieldName);
            if (fieldValue instanceof String) {
                fieldValue = ((String) fieldValue).trim();
            }
            fieldNameValuesCleaned.put(fieldName, fieldValue);
		}
		fieldNameValues = fieldNameValuesCleaned;
	}

    /**
     * Concatenates
     */
    protected void concatenate() throws Exception {
        // Set up concatenation ordered lists
        Map<String, Map<Integer, String>> concatenateFieldsMap = new HashMap<>();
        Map<String, Object> newNameFieldValues = new HashMap<>();
        for (String fieldName : fieldNameValues.keySet()) {
            String fieldValue = (String) fieldNameValues.get(fieldName);
            int fieldOrder = (Integer)fieldMetaMap.get(fieldName).get("field_order");
            if (fieldOrder > 0) { // Concatenation field
                String attributeName = (String) fieldMetaMap.get(fieldName).get("attribute_name");
//				LOG.info("Concatenating: fieldName="+fieldName+", attributeName="+attributeName+", fieldValue="+fieldValue);
                Map<Integer, String> concatenationFields = concatenateFieldsMap.get(attributeName);
                if (concatenationFields == null) {
                    concatenationFields = new HashMap<>();
                    concatenateFieldsMap.put(attributeName, concatenationFields);
                }
                concatenationFields.put(fieldOrder, " "+fieldValue);
            } else {
                newNameFieldValues.put(fieldName, fieldValue);
            }
        }

        for (String attributeName : concatenateFieldsMap.keySet()) {
            // Concatenate values
            Map<Integer, String> concatenationFields = concatenateFieldsMap.get(attributeName);
            StringBuilder sb = new StringBuilder(concatenationFields.size());
            for (int i = 1; i <= concatenationFields.size(); i++) {
                sb.append(concatenationFields.get(i));
            }
            String concatenatedField = sb.toString();
//            LOG.info("Concatenated field: attribute name="+attributeName+"; value="+concatenatedField);
            fieldValuesMapped.put(attributeName, concatenatedField);
        }
        fieldNameValues = newNameFieldValues;
    }

	/**
	 * Splits
	 */
	protected void split() throws Exception {
		//
	}

	protected void format() throws Exception {
        //
	}

	/**
	 * Substitutes a fields value if a value to be substituted is provided.
	 */
	protected void substitute() throws Exception {
		Map<String, Object> fieldValuesSubstituted = new HashMap<>();
		fieldValuesSubstituted.putAll(fieldNameValues);
		for (String fieldName : fieldNameValues.keySet()) {
			String fieldValue = ((String) fieldNameValues.get(fieldName)).trim();
			Map<String, String> substitutionMap = (Map<String, String>) fieldMetaMap.get(fieldName).get("substitution_map");
			if (substitutionMap.containsKey(fieldValue)) {
				fieldValue = substitutionMap.get(fieldValue);
			}
			fieldValuesSubstituted.put(fieldName, fieldValue);
		}
		fieldNameValues = fieldValuesSubstituted;
	}

	/**
	 * Replaces field names with attribute names then populates the supplied
	 * Event.
	 */
	protected void map() throws Exception {
		for (String fieldName : fieldNameValues.keySet()) {
			String attributeName = (String) fieldMetaMap.get(fieldName).get("attribute_name");
			if (attributeName != null) {
                String fieldValue = (String) fieldNameValues.get(fieldName);
                if ((attributeName.equals("date") || attributeName.endsWith("Date")) && fieldValue.length() > 23) fieldValue = fieldValue.substring(0,23);
//                LOG.info("attribute_name: "+attributeName+", value: "+fieldValue);
				fieldValuesMapped.put(attributeName, fieldValue);
			}
		}
		event.payload = fieldValuesMapped;
	}

}
