package ra.rta.rfm.conspref.services.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WANDService {

    private static Logger LOG = LoggerFactory.getLogger(WANDService.class);

    public static final int CompetitiveTermcode = 2464364;

    public static void loadExactMatchAndParentTermcodesFromFile(String wandChaseLookupFile, Map<String,Integer> exactMatchTermcodeLookup, Map<Integer,Set<Integer>> parentTermcodesLookup) {

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

    public static Map<Integer,Integer> buildWindowSelector(String wandChaseLookupFile) {
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
