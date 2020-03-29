package ra.rta.bolts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import ra.rta.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import ra.rta.services.business.TransformService;
import ra.rta.services.data.DataServiceManager;

/**
 * Transform the raw data into internal data structure in Java format (ra.rta.models).
 */
public class TransformBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(TransformBolt.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private Map map;
	private TopologyContext topologyContext;
	private OutputCollector outputCollector;
	private TransformService transformService;

	@Override
	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		this.map = map;
		this.topologyContext = topologyContext;
		this.outputCollector = outputCollector;
		DataServiceManager.setProperties(map);
		load();
	}

	@Override
	public void execute(Tuple tuple) {
		Event eventIn = null;
		byte[] bytes = tuple.getBinary(0);
		try {
			eventIn = MAPPER.readValue(new ByteArrayInputStream(bytes), Event.class);
			try {
				Event event = transformService.transform(eventIn);
				String description = ((FinancialTransaction)event.getEntity()).getPayee();
				outputCollector.emit(tuple, new Values(description, event));
			} catch (Exception e) {
				LOG.warn("Exception caught in " + TransformBolt.class.getSimpleName() + ".execute()", e);
				outputCollector.reportError(e);
			}
		} catch (Exception e) {
			LOG.warn("Exception caught attempting to read Envelope from JSON stream: {}", new String(bytes));
			outputCollector.reportError(e);
		}
		outputCollector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare(new Fields("description",Event.class.getSimpleName()));
	}

	@Override
	public void cleanup() {
		super.cleanup();
		DataServiceManager.close();
	}

	private void load() {
		try {
			long timeStart = new Date().getTime();
			Map<String, Group> allActivePartnersMap = DataServiceManager.getGroupDataService()
					.getAllActivePartnersMap();
			Map<String, Map<String, Map<String,Map<String,Object>>>> transformMaps = new HashMap<>();
			for(String partnerName : allActivePartnersMap.keySet()) {
				Map<String, Map<String, Map<String, Object>>> partnerTransformMap = DataServiceManager
						.getTransformDataService().getPartnerTransformMaps(partnerName);
				transformMaps.put(partnerName, partnerTransformMap);
			}
			long timeEnd = new Date().getTime();
			LOG.info(TransformBolt.class.getSimpleName()+" loaded in "+(timeEnd - timeStart)/1000+" seconds.");
			LOG.info("All active partners map: {}", allActivePartnersMap);
			LOG.info("Transforms map: {}",transformMaps);
			transformService = new TransformService(allActivePartnersMap, transformMaps);
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("IOException caught while attempting to load TransformService's transform files: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Exception caught while attempting to load TransformService's transform files: " + e);
		}
	}

}
