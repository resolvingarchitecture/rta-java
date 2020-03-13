package ra.rta.services.data;

import java.util.*;

import com.datastax.driver.core.*;
import ra.rta.models.ChaseFailure;
import ra.rta.models.ExactMatchFailure;
import ra.rta.models.KPI;

public class WANDDataService extends BaseDataService {

	private static final String IS_LEAF = "is_leaf";
	private static final String CLASS_CODE = "class_code";
	private static final String TERM_CODE = "term_code";
	private static final String TERM_DESC = "term_desc";
	private static final String PARENT_TERMS = "parent_terms";

	private PreparedStatement lookupTermcodePS;
	private BoundStatement lookupTermcodeBS;

	public static final int CompetitiveTermcode = 8784781;

	public WANDDataService(Session session) {
		super(session);
		lookupTermcodePS = session.prepare("SELECT term_code, is_leaf, parent_terms FROM wand.wandchase_lookup WHERE term_desc = ?");
		lookupTermcodeBS = new BoundStatement(lookupTermcodePS);
	}

	public LinkedHashSet<KPI> lookupTermcode(String description) {
		LinkedHashSet<KPI> termcodes = new LinkedHashSet<>();
		lookupTermcodeBS.bind(description);
		ResultSet rs = session.execute(lookupTermcodeBS);
		Row row = rs.one();
		if(row!=null && row.getBool("is_leaf")) {
			KPI kpi = new KPI(row.getInt("term_code"));
			termcodes.add(kpi);
			Set<Integer> parents = row.getSet("parent_terms",Integer.class);
			boolean competitive = false;
			for(int parent : parents) {
				if (8784781 == parent) {
					competitive = true;
				}
			}
			if (competitive) {
				kpi.setWindowDays(90);
				kpi.setType(KPI.Type.Competitive);
			} else {
				kpi.setWindowDays(365); // Default
				kpi.setType(KPI.Type.NonCompetitive);
			}
			for(int parent : parents) {
				kpi = new KPI(parent);
				if (competitive) {
					kpi.setWindowDays(90);
					kpi.setType(KPI.Type.Competitive);
				} else {
					kpi.setWindowDays(365); // Default
					kpi.setType(KPI.Type.NonCompetitive);
				}
				termcodes.add(kpi);
			}
		}
		return termcodes;
	}

	public ExactMatchFailure loadExactMatchFailure(String partnerName, String tradeName) {
		ExactMatchFailure exactMatchFailure = null;
		String cql = "SELECT * FROM " + partnerName + ".wand_exactmatch_failures WHERE tradename = '" + tradeName
				+ "';";
		//        LOG.info(cql);
		ResultSet rs = session.execute(new SimpleStatement(cql));
		Row row = rs.one();
		if (row != null) {
			exactMatchFailure = new ExactMatchFailure(tradeName);
			exactMatchFailure.setType(row.getString("type"));
			exactMatchFailure.setVehicle(row.getString("vehicle"));
			exactMatchFailure.setCount(row.getInt("count"));
			exactMatchFailure.setFirstSeen(row.getString("first_seen"));
			exactMatchFailure.setLastSeen(row.getString("last_seen"));
			exactMatchFailure.setPosted(row.getBool("posted"));
		}
		return exactMatchFailure;
	}

	public void save(String partnerName, ExactMatchFailure exactMatchFailure) throws Exception {
		session.execute(new SimpleStatement(
				"INSERT INTO "
						+ partnerName
						+ ".wand_exactmatch_failures (tradename, type, vehicle, count, first_seen, last_seen, posted) VALUES ('"
						+ exactMatchFailure.getTradename() + "', '" + exactMatchFailure.getType() + "', '"
						+ exactMatchFailure.getVehicle() + "', " + exactMatchFailure.getCount() + ", '"
						+ exactMatchFailure.getFirstSeen() + "', '" + exactMatchFailure.getLastSeen() + "', "
						+ exactMatchFailure.getPosted() + ");"));
	}

	public List<ExactMatchFailure> unpostedExactMatchFailures(String partnerName) {
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

	public List<ExactMatchFailure> getExactMatchFailures(String partnerName) {
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

	public ChaseFailure loadChaseFailure(String partnerName, int termCode) {
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

	public void save(String partnerName, ChaseFailure chaseFailure) throws Exception {
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

	public List<ChaseFailure> unpostedChaseFailures(String partnerName) {
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

	public List<ChaseFailure> getChaseFailures(String partnerName) {
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

	public void loadExactMatchAndParentTermcodes(Map<String,Integer> exactMatchTermcodeLookup, Map<Integer,Set<Integer>> parentTermcodesLookup) throws Exception {
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

	public void buildWindowSelector(Map<Integer,Integer> windowSelector) {
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
}
