package ra.rta.rfm.conspref.classify;

import java.io.*;
import java.util.*;

import com.datastax.driver.core.*;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.BaseEventEmitterBolt;
import ra.rta.connectors.cassandra.CassandraMgr;
import ra.rta.classify.KPIClassifiable;
import ra.rta.Event;
import ra.rta.classify.KPI;
import ra.rta.rfm.conspref.models.ChaseFailure;
import ra.rta.rfm.conspref.models.ExactMatchFailure;
import ra.rta.rfm.conspref.models.RFMKPI;

/**
 * Add associated KPIs.
 */
public class WANDClassificationBolt extends BaseEventEmitterBolt {

	private static final long serialVersionUID = 1L;

	private Logger LOG = LoggerFactory.getLogger(WANDClassificationBolt.class);

	private static final String IS_LEAF = "is_leaf";
	private static final String CLASS_CODE = "class_code";
	private static final String TERM_CODE = "term_code";
	private static final String TERM_DESC = "term_desc";
	private static final String PARENT_TERMS = "parent_terms";

	public static final int CompetitiveTermcode = 8784781;

	private Session session;
	private File exactMatchTermcodeFile;
	private Map<String, LinkedHashSet<? extends KPI>> exactMatchTermcodeCache;

	@SuppressWarnings("hiding")
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
		super.prepare(map, topologyContext, outputCollector);
		session = CassandraMgr.getInstance().getSession();
		// TODO: Load from file
//		lookupTermcodePS = session.prepare("SELECT term_code, is_leaf, parent_terms FROM wand.wandchase_lookup WHERE term_desc = ?");
//		lookupTermcodeBS = new BoundStatement(lookupTermcodePS);
		exactMatchTermcodeFile = new File((String)map.get("topology.classify.wand.file"));
		// File line format is: is_leaf, term_code, [parent_terms]

//		exactMatchTermcodeCache = CacheBuilder.newBuilder()
//				.maximumSize(10000000) // 10M
//				.build(new CacheLoader<String, LinkedHashSet<KPI>>() {
//							@Override
//							public LinkedHashSet<KPI> load(String description) throws Exception {
//								return lookupTermcode(description);
//							}
//						}
//				);
	}

	public void execute(Event event) throws Exception {
		if(event instanceof KPIClassifiable) {
			((KPIClassifiable)event).classify(exactMatchTermcodeCache);
		}
	}

	private Set<KPI> lookupTermcode(String description) {
		Set<KPI> termcodes = new LinkedHashSet<>();
		lookupTermcodeBS.bind(description);
		ResultSet rs = session.execute(lookupTermcodeBS);
		Row row = rs.one();
		if(row!=null && row.getBool("is_leaf")) {
			RFMKPI kpi = new RFMKPI(row.getInt("term_code"));
			termcodes.add(kpi);
			Set<Integer> parents = row.getSet("parent_terms",Integer.class);
			boolean competitive = false;
			for(int parent : parents) {
				if (8784781 == parent) {
					competitive = true;
					break;
				}
			}
			if (competitive) {
				kpi.windowDays = 90;
				kpi.type = RFMKPI.Type.Competitive;
			} else {
				kpi.windowDays = 365; // Default
				kpi.type = RFMKPI.Type.NonCompetitive;
			}
			for(int parent : parents) {
				kpi = new RFMKPI(parent);
				if (competitive) {
					kpi.windowDays = 90;
					kpi.type = RFMKPI.Type.Competitive;
				} else {
					kpi.windowDays = 365; // Default
					kpi.type = RFMKPI.Type.NonCompetitive;
				}
				termcodes.add(kpi);
			}
		}
		return termcodes;
	}

	private ExactMatchFailure loadExactMatchFailure(String groupName, String tradeName) {
		ExactMatchFailure exactMatchFailure = null;
		String cql = "SELECT * FROM " + groupName + ".wand_exactmatch_failures WHERE tradename = '" + tradeName
				+ "';";
		//        LOG.info(cql);
		ResultSet rs = session.execute(new SimpleStatement(cql));
		Row row = rs.one();
		if (row != null) {
			exactMatchFailure = new ExactMatchFailure(tradeName);
			exactMatchFailure.type = row.getString("type");
			exactMatchFailure.vehicle = row.getString("vehicle");
			exactMatchFailure.count = row.getInt("count");
			exactMatchFailure.firstSeen = row.getString("first_seen");
			exactMatchFailure.lastSeen = row.getString("last_seen");
			exactMatchFailure.posted = row.getBool("posted");
		}
		return exactMatchFailure;
	}

	private void save(String partnerName, ExactMatchFailure exactMatchFailure) throws Exception {
		session.execute(new SimpleStatement(
				"INSERT INTO "
						+ partnerName
						+ ".wand_exactmatch_failures (tradename, type, vehicle, count, first_seen, last_seen, posted) VALUES ('"
						+ exactMatchFailure.getTradename() + "', '" + exactMatchFailure.getType() + "', '"
						+ exactMatchFailure.getVehicle() + "', " + exactMatchFailure.getCount() + ", '"
						+ exactMatchFailure.getFirstSeen() + "', '" + exactMatchFailure.getLastSeen() + "', "
						+ exactMatchFailure.getPosted() + ");"));
	}

	private List<ExactMatchFailure> unpostedExactMatchFailures(String partnerName) {
		List<ExactMatchFailure> exactMatchFailures = new ArrayList<>();
		ExactMatchFailure exactMatchFailure;
		ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM " + partnerName
				+ ".wand_exactmatch_failures WHERE posted = false;"));
		for (Row row : rs) {
			exactMatchFailure = new ExactMatchFailure(row.getString("tradename"));
			exactMatchFailure.setType(row.getString("type"));
			exactMatchFailure.setVehicle(row.getString("vehicle"));
			exactMatchFailure.setCount(row.getInt("count"));
			exactMatchFailure.setFirstSeen(row.getString("first_seen"));
			exactMatchFailure.setLastSeen(row.getString("last_seen"));
			exactMatchFailure.setPosted(row.getBool("posted"));
			exactMatchFailures.add(exactMatchFailure);
		}
		return exactMatchFailures;
	}

	private List<ExactMatchFailure> getExactMatchFailures(String partnerName) {
		List<ExactMatchFailure> exactMatchFailures = new ArrayList<>();
		ExactMatchFailure exactMatchFailure;
		ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM " + partnerName
				+ ".wand_exactmatch_failures;"));
		for (Row row : rs) {
			exactMatchFailure = new ExactMatchFailure(row.getString("tradename"));
			exactMatchFailure.setType(row.getString("type"));
			exactMatchFailure.setVehicle(row.getString("vehicle"));
			exactMatchFailure.setCount(row.getInt("count"));
			exactMatchFailure.setFirstSeen(row.getString("first_seen"));
			exactMatchFailure.setLastSeen(row.getString("last_seen"));
			exactMatchFailure.setPosted(row.getBool("posted"));
			exactMatchFailures.add(exactMatchFailure);
		}
		return exactMatchFailures;
	}

	private ChaseFailure loadChaseFailure(String partnerName, int termCode) {
		ChaseFailure chaseFailure = null;
		ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM " + partnerName
				+ ".wand_chase_failures WHERE term_code = " + termCode + ";"));
		Row row = rs.one();
		if (row != null) {
			chaseFailure = new ChaseFailure(termCode);
			chaseFailure.setFrequency(row.getInt("frequency"));
			chaseFailure.setTransFrequency(row.getInt("trans_frequency"));
			chaseFailure.setTermDesc(row.getString(TERM_DESC));
			chaseFailure.setType(row.getString("type"));
			chaseFailure.setVehicle(row.getString("vehicle"));
			chaseFailure.setFirstSeen(row.getString("first_seen"));
			chaseFailure.setLastSeen(row.getString("last_seen"));
			chaseFailure.setPosted(row.getBool("posted"));
		}
		return chaseFailure;
	}

	private void save(String partnerName, ChaseFailure chaseFailure) throws Exception {
		session.execute(new SimpleStatement(
				"INSERT INTO "
						+ partnerName
						+ ".wand_chase_failures (term_code, frequency, trans_frequency, term_desc, type, vehicle, first_seen, last_seen, posted) VALUES ("
						+ chaseFailure.getTermCode() + ", " + chaseFailure.getFrequency() + ", "
						+ chaseFailure.getTransFrequency() + ", '" + chaseFailure.getTermDesc() + "', '"
						+ chaseFailure.getType() + "', '" + chaseFailure.getVehicle() + "', '"
						+ chaseFailure.getFirstSeen() + "', '" + chaseFailure.getLastSeen() + "', "
						+ chaseFailure.getPosted() + ");"));
	}

	private List<ChaseFailure> unpostedChaseFailures(String partnerName) {
		List<ChaseFailure> chaseFailures = new ArrayList<>();
		ChaseFailure chaseFailure;
		ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM " + partnerName
				+ ".wand_chase_failures WHERE posted = false;"));
		for (Row row : rs) {
			chaseFailure = new ChaseFailure(row.getInt(TERM_CODE));
			chaseFailure.setFrequency(row.getInt("frequency"));
			chaseFailure.setTransFrequency(row.getInt("trans_frequency"));
			chaseFailure.setTermDesc(row.getString(TERM_DESC));
			chaseFailure.setType(row.getString("type"));
			chaseFailure.setVehicle(row.getString("vehicle"));
			chaseFailure.setFirstSeen(row.getString("first_seen"));
			chaseFailure.setLastSeen(row.getString("last_seen"));
			chaseFailure.setPosted(row.getBool("posted"));
			chaseFailures.add(chaseFailure);
		}
		return chaseFailures;
	}

	private List<ChaseFailure> getChaseFailures(String partnerName) {
		List<ChaseFailure> chaseFailures = new ArrayList<>();
		ChaseFailure chaseFailure;
		ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM " + partnerName + ".wand_chase_failures;"));
		for (Row row : rs) {
			chaseFailure = new ChaseFailure(row.getInt(TERM_CODE));
			chaseFailure.setFrequency(row.getInt("frequency"));
			chaseFailure.setTransFrequency(row.getInt("trans_frequency"));
			chaseFailure.setTermDesc(row.getString(TERM_DESC));
			chaseFailure.setType(row.getString("type"));
			chaseFailure.setVehicle(row.getString("vehicle"));
			chaseFailure.setFirstSeen(row.getString("first_seen"));
			chaseFailure.setLastSeen(row.getString("last_seen"));
			chaseFailure.setPosted(row.getBool("posted"));
			chaseFailures.add(chaseFailure);
		}
		return chaseFailures;
	}

	private void loadExactMatchAndParentTermcodes(Map<String,Integer> exactMatchTermcodeLookup, Map<Integer,Set<Integer>> parentTermcodesLookup) throws Exception {
		SimpleStatement stmt = new SimpleStatement("SELECT * FROM wand.wandchase_lookup;");
		// TODO tweak fetch size and available without fetching threshold
		stmt.setFetchSize(10000);
		ResultSet rows = session.execute(stmt);
		int termcode;
		String description;
		int classCode = 0;
		boolean isLeaf = false;
		Set<Integer> parentTermcodes;
		for (Row row : rows) {
			if (rows.getAvailableWithoutFetching() < 1000 && !rows.isFullyFetched()) {
				// NB: fetchMoreResults is non-blocking
				rows.fetchMoreResults();
			}
			isLeaf = row.getBool(IS_LEAF);
			classCode = row.getInt(CLASS_CODE);
			if (isLeaf && (classCode == 958 || classCode == 968)) {
				termcode = row.getInt(TERM_CODE);
				description = row.getString(TERM_DESC);
				exactMatchTermcodeLookup.put(description, termcode);
				parentTermcodes = row.getSet(PARENT_TERMS, Integer.class);
				parentTermcodesLookup.put(termcode, parentTermcodes);
			}
		}
	}

	private void buildWindowSelector(Map<Integer,Integer> windowSelector) {
		SimpleStatement stmt = new SimpleStatement("SELECT * FROM wand.wandchase_lookup;");
		stmt.setFetchSize(10000);
		ResultSet rows = session.execute(stmt);
		int termcode;
		Set<Integer> parentTermcodes;
		for (Row row : rows) {
			if (rows.getAvailableWithoutFetching() < 1000 && !rows.isFullyFetched()) {
				// NB: fetchMoreResults is non-blocking
				rows.fetchMoreResults();
			}
			if (row.getBool(IS_LEAF)) {
				termcode = row.getInt(TERM_CODE);
				parentTermcodes = row.getSet(PARENT_TERMS, Integer.class);
				boolean competitive = false;
				for (int parentTermcode : parentTermcodes) {
					if (CompetitiveTermcode == parentTermcode) {
						competitive = true;
					}
				}
				if (competitive) {
					windowSelector.put(termcode,90);
				} else {
					windowSelector.put(termcode,365);
				}
				for (int parentTermcode : parentTermcodes) {
					if (competitive) {
						windowSelector.put(parentTermcode,90);
					}  else {
						windowSelector.put(parentTermcode,365);
					}
				}
			}
		}
	}

	private void loadExactMatchAndParentTermcodesFromFile(String wandChaseLookupFile, Map<String,Integer> exactMatchTermcodeLookup, Map<Integer,Set<Integer>> parentTermcodesLookup) {

		String[] fields;
		String[] values;
		Set<Integer> parentTermcodes;
		boolean first = true;
		String line;
		String termcodeStr;
		List<String> termcodeStrings;
		String termcodeDescription;
		int termcode;

		try (BufferedReader br = new BufferedReader(new FileReader(wandChaseLookupFile));) {
			while ((line = br.readLine()) != null) {
				if (first) {
					fields = line.split("\\|");
					first = false;
				} else {
					values = line.split("\\|");
					if ("1".equals(values[9])) { // Termcode is leaf
						termcodeStr = values[0];
						if(termcodeStr!=null && !"".equals(termcodeStr)) {
							termcode = Integer.parseInt(termcodeStr);
							termcodeDescription = values[3];
							if(termcodeDescription!=null && !"".equals(termcodeDescription))
								exactMatchTermcodeLookup.put(termcodeDescription, termcode);
							String valueListStr = values[4];
							if (valueListStr != null && !"".equals(valueListStr)) {
								valueListStr = valueListStr.replace("{", "").replace("}", "");
								parentTermcodes = new HashSet<>();
								if (valueListStr.contains(",")) {
									termcodeStrings = Arrays.asList(valueListStr.split(","));
									for (String t : termcodeStrings) {
										if(t!=null && !"".equals(t)) {
											termcode = Integer.parseInt(t);
											parentTermcodes.add(termcode);
										}
									}
								} else if(!"".equals(valueListStr)) {
									termcode = Integer.parseInt(valueListStr);
									parentTermcodes.add(termcode);
								}
								parentTermcodesLookup.put(termcode, parentTermcodes);
							}
						}
					}
				}
			}

		} catch (FileNotFoundException e) {
			LOG.error("WANDHelper.loadFromFile() failed: unable to find WAND file: {}", wandChaseLookupFile);
		} catch (IOException e) {
			LOG.error("WANDHelper.loadFromFile() failed: IOException loading WAND file (" + wandChaseLookupFile + "): \n" + e);
		}
	}

	private Map<Integer,Integer> buildWindowSelector(String wandChaseLookupFile) {
		Map<Integer,Integer> windowSelector = new HashMap<>();
		String[] fields;
		String[] values;
		Set<Integer> parentTermcodes;
		boolean first = true;
		String line;
		String termcodeStr;
		int termcode;
		List<String> termcodeStrings;

		try (BufferedReader br = new BufferedReader(new FileReader(wandChaseLookupFile));) {
			while ((line = br.readLine()) != null) {
				if (first) {
					fields = line.split("\\|");
					first = false;
				} else {
					values = line.split("\\|");
					if ("1".equals(values[9])) { // Termcode is leaf
						termcodeStr = values[0];
						if(termcodeStr!=null && !"".equals(termcodeStr)) {
							termcode = Integer.parseInt(termcodeStr);
							String valueListStr = values[4];
							if (valueListStr != null && !"".equals(valueListStr)) {
								valueListStr = valueListStr.replace("{", "").replace("}", "");
								parentTermcodes = new HashSet<>();
								if (valueListStr.contains(",")) {
									termcodeStrings = Arrays.asList(valueListStr.split(","));
									for (String t : termcodeStrings) {
										if (t != null && !"".equals(t)) {
											termcode = Integer.parseInt(t);
											parentTermcodes.add(termcode);
										}
									}
								} else if (!"".equals(valueListStr)) {
									termcode = Integer.parseInt(valueListStr);
									parentTermcodes.add(termcode);
								}
								if(parentTermcodes.size() > 0) {
									boolean competitive = false;
									for (int parentTermcode : parentTermcodes) {
										competitive = (CompetitiveTermcode == parentTermcode);
									}
									if (competitive) {
										windowSelector.put(termcode, 90);
									} else {
										windowSelector.put(termcode, 365);
									}
									for (int parentTermcode : parentTermcodes) {
										if (competitive) {
											windowSelector.put(parentTermcode, 90);
										} else {
											windowSelector.put(parentTermcode, 365);
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (FileNotFoundException e) {
			LOG.error("WANDHelper.loadFromFile() failed: unable to find WAND file: {}", wandChaseLookupFile);
		} catch (IOException e) {
			LOG.error("WANDHelper.loadFromFile() failed: IOException loading WAND file (" + wandChaseLookupFile + "): \n" + e);
		}
		return windowSelector;
	}

}
