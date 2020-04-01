/*
  This is free and unencumbered software released into the public domain.

  Anyone is free to copy, modify, publish, use, compile, sell, or
  distribute this software, either in source code form or as a compiled
  binary, for any purpose, commercial or non-commercial, and by any
  means.

  In jurisdictions that recognize copyright laws, the author or authors
  of this software dedicate any and all copyright interest in the
  software to the public domain. We make this dedication for the benefit
  of the public at large and to the detriment of our heirs and
  successors. We intend this dedication to be an overt act of
  relinquishment in perpetuity of all present and future rights to this
  software under copyright law.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE.

  For more information, please refer to <http://unlicense.org/>
 */
package ra.rta.rfm.conspref.classify;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.classify.KPI;
import ra.rta.connectors.cassandra.CassandraMgr;
import ra.rta.rfm.conspref.models.ChaseFailure;
import ra.rta.rfm.conspref.models.ExactMatchFailure;

import java.io.*;
import java.util.*;

public class WANDMgr {

    private static Logger LOG = LoggerFactory.getLogger(WANDMgr.class);

    private static final String IS_LEAF = "is_leaf";
    private static final String CLASS_CODE = "class_code";
    private static final String TERM_CODE = "term_code";
    private static final String TERM_DESC = "term_desc";
    private static final String PARENT_TERMS = "parent_terms";

    public static final int CompetitiveTermcode = 8784781;

    private static WANDMgr instance;
    private static final Object lock = new Object();

    private Session session;

    private Map<String, LinkedHashSet<? extends KPI>> exactDescMatchTermcodeLookup = new HashMap<>();
    private Map<String,Integer> exactMatchTermcodeLookup = new HashMap<>();
    private Map<Integer,Set<Integer>> parentTermcodesLookup = new HashMap<>();
    private Map<Integer,Integer> windowSelector = new HashMap<>();

    private WANDMgr(){}

    public static WANDMgr init(Map map) {
        synchronized (lock) {
            if (instance == null) {
                String filePath = (String) map.get("topology.classify.wand.file");
                if(filePath!=null) {
                    File file = new File(filePath);
                    if(file.exists()) {
                        instance = new WANDMgr();
                        instance.loadExactMatchAndParentTermcodesFromFile(filePath);
                    }
                }
                instance.session = CassandraMgr.init(map).getSession();
            }
        }
        return instance;
    }

    public static WANDMgr getInstance() {
        return instance;
    }

    public LinkedHashSet<? extends KPI> lookupTermcode(String description) {
        return exactDescMatchTermcodeLookup.get(description);
    }

    public ExactMatchFailure loadExactMatchFailure(int groupId, String tradeName) {
        ExactMatchFailure exactMatchFailure = null;
        String cql = "SELECT * FROM wand_exactmatch_failures WHERE gId="+groupId+" and tradename = '" + tradeName + "';";
        //        LOG.info(cql);
        ResultSet rs = session.execute(new SimpleStatement(cql));
        Row row = rs.one();
        if (row != null) {
            exactMatchFailure = new ExactMatchFailure();
            exactMatchFailure.tradename = row.getString("tradename");
            exactMatchFailure.type = row.getString("type");
            exactMatchFailure.vehicle = row.getString("vehicle");
            exactMatchFailure.count = row.getInt("count");
            exactMatchFailure.firstSeen = row.getLong("first_seen");
            exactMatchFailure.lastSeen = row.getLong("last_seen");
            exactMatchFailure.posted = row.getBool("posted");
        }
        return exactMatchFailure;
    }

    public void save(int groupId, ExactMatchFailure exactMatchFailure) throws Exception {
        session.execute(new SimpleStatement(
                "INSERT INTO wand_exactmatch_failures (gId, tradename, type, vehicle, count, first_seen, last_seen, posted) VALUES ("
                        + groupId +", '"
                        + exactMatchFailure.tradename + "', '" + exactMatchFailure.type + "', '"
                        + exactMatchFailure.vehicle + "', " + exactMatchFailure.count + ", '"
                        + exactMatchFailure.firstSeen + "', '" + exactMatchFailure.lastSeen + "', "
                        + exactMatchFailure.posted + ");"));
    }

    public List<ExactMatchFailure> unpostedExactMatchFailures(int groupId) {
        List<ExactMatchFailure> exactMatchFailures = new ArrayList<>();
        ExactMatchFailure exactMatchFailure;
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM wand_exactmatch_failures WHERE gId="+groupId+" and posted = false;"));
        for (Row row : rs) {
            exactMatchFailure = new ExactMatchFailure();
            exactMatchFailure.tradename = row.getString("tradename");
            exactMatchFailure.type = row.getString("type");
            exactMatchFailure.vehicle = row.getString("vehicle");
            exactMatchFailure.count = row.getInt("count");
            exactMatchFailure.firstSeen = row.getLong("first_seen");
            exactMatchFailure.lastSeen = row.getLong("last_seen");
            exactMatchFailure.posted = row.getBool("posted");
            exactMatchFailures.add(exactMatchFailure);
        }
        return exactMatchFailures;
    }

    public List<ExactMatchFailure> getExactMatchFailures(int groupId) {
        List<ExactMatchFailure> exactMatchFailures = new ArrayList<>();
        ExactMatchFailure exactMatchFailure;
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM wand_exactmatch_failures where gId="+groupId+";"));
        for (Row row : rs) {
            exactMatchFailure = new ExactMatchFailure();
            exactMatchFailure.tradename = row.getString("tradename");
            exactMatchFailure.type = row.getString("type");
            exactMatchFailure.vehicle = row.getString("vehicle");
            exactMatchFailure.count = row.getInt("count");
            exactMatchFailure.firstSeen = row.getLong("first_seen");
            exactMatchFailure.lastSeen = row.getLong("last_seen");
            exactMatchFailure.posted = row.getBool("posted");
            exactMatchFailures.add(exactMatchFailure);
        }
        return exactMatchFailures;
    }

    public ChaseFailure loadChaseFailure(int groupId, int termCode) {
        ChaseFailure chaseFailure = null;
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM wand_chase_failures WHERE gId="+groupId+" and term_code = " + termCode + ";"));
        Row row = rs.one();
        if (row != null) {
            chaseFailure = new ChaseFailure();
            chaseFailure.termCode = row.getInt("termcode");
            chaseFailure.frequency = row.getInt("frequency");
            chaseFailure.transFrequency = row.getInt("trans_frequency");
            chaseFailure.termDesc = row.getString(TERM_DESC);
            chaseFailure.type = row.getString("type");
            chaseFailure.vehicle = row.getString("vehicle");
            chaseFailure.firstSeen = row.getString("first_seen");
            chaseFailure.lastSeen = row.getString("last_seen");
            chaseFailure.posted = row.getBool("posted");
        }
        return chaseFailure;
    }

    public void save(int groupId, ChaseFailure chaseFailure) throws Exception {
        session.execute(new SimpleStatement(
                "INSERT INTO wand_chase_failures (gId, term_code, frequency, trans_frequency, term_desc, type, vehicle, first_seen, last_seen, posted) VALUES ("
                        + groupId + ", "
                        + chaseFailure.termCode + ", " + chaseFailure.frequency + ", "
                        + chaseFailure.transFrequency + ", '" + chaseFailure.termDesc + "', '"
                        + chaseFailure.type + "', '" + chaseFailure.vehicle + "', '"
                        + chaseFailure.firstSeen + "', '" + chaseFailure.lastSeen + "', "
                        + chaseFailure.posted + ");"));
    }

    public List<ChaseFailure> unpostedChaseFailures(int groupId) {
        List<ChaseFailure> chaseFailures = new ArrayList<>();
        ChaseFailure chaseFailure;
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM wand_chase_failures WHERE gId="+groupId+" and posted = false;"));
        for (Row row : rs) {
            chaseFailure = new ChaseFailure();
            chaseFailure.termCode = row.getInt("termcode");
            chaseFailure.frequency = row.getInt("frequency");
            chaseFailure.transFrequency = row.getInt("trans_frequency");
            chaseFailure.termDesc = row.getString(TERM_DESC);
            chaseFailure.type = row.getString("type");
            chaseFailure.vehicle = row.getString("vehicle");
            chaseFailure.firstSeen = row.getString("first_seen");
            chaseFailure.lastSeen = row.getString("last_seen");
            chaseFailure.posted = row.getBool("posted");
            chaseFailures.add(chaseFailure);
        }
        return chaseFailures;
    }

    public List<ChaseFailure> getChaseFailures(int groupId) {
        List<ChaseFailure> chaseFailures = new ArrayList<>();
        ChaseFailure chaseFailure;
        ResultSet rs = session.execute(new SimpleStatement("SELECT * FROM wand_chase_failures where gId="+groupId+";"));
        for (Row row : rs) {
            chaseFailure = new ChaseFailure();
            chaseFailure.termCode = row.getInt("termcode");
            chaseFailure.frequency = row.getInt("frequency");
            chaseFailure.transFrequency = row.getInt("trans_frequency");
            chaseFailure.termDesc = row.getString(TERM_DESC);
            chaseFailure.type = row.getString("type");
            chaseFailure.vehicle = row.getString("vehicle");
            chaseFailure.firstSeen = row.getString("first_seen");
            chaseFailure.lastSeen = row.getString("last_seen");
            chaseFailure.posted = row.getBool("posted");
            chaseFailures.add(chaseFailure);
        }
        return chaseFailures;
    }

    private void loadExactMatchAndParentTermcodes(Map<String,Integer> exactMatchTermcodeLookup, Map<Integer,Set<Integer>> parentTermcodesLookup) throws Exception {
        SimpleStatement stmt = new SimpleStatement("SELECT * FROM wandchase_lookup;");
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
        SimpleStatement stmt = new SimpleStatement("SELECT * FROM wandchase_lookup;");
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

    private void loadExactMatchAndParentTermcodesFromFile(String wandChaseLookupFile) {
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

    private void buildWindowSelector(String wandChaseLookupFile) {
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
    }

}
