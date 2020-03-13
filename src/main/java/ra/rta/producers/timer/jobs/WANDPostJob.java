package ra.rta.producers.timer.jobs;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ra.rta.models.ChaseFailure;
import ra.rta.models.ExactMatchFailure;
import ra.rta.models.Partner;
import ra.rta.models.utilities.DateUtility;
import ra.rta.services.data.DataServiceManager;
import ra.rta.services.data.PartnerDataService;
import ra.rta.services.data.WANDDataService;

public class WANDPostJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(WANDPostJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			postToWAND(jobExecutionContext.getMergedJobDataMap());
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	public void postToWAND(JobDataMap map) throws Exception {
		String msg = WANDPostJob.class.getSimpleName()+" started...";
		Date today = new Date();
		String fileDate = DateUtility.timestampToSimpleDateStringWithSeparator(today, "_");
		LOG.info(msg);
		System.out.println(msg);
		String wandPostPathString = (String)map.get("wandPostPath");
		String tempPostPathString = "/tmp";
		PartnerDataService partnerDataService = DataServiceManager.getPartnerDataService();
		WANDDataService wandDataService = DataServiceManager.getWandDataService();
		// Get list of active Partners
		Map<String,Partner> partners = partnerDataService.getAllActivePartnersMap();
		Set<String> partnerNames = partners.keySet();
		InputStream is = null;
		GZIPOutputStream os = null;
		for(String partnerName : partnerNames) {
			// Select term codes yet to be sent
			// Create csv files in staging area to send
			Path exactMatchFailuresCSV = FileSystems.getDefault().getPath(tempPostPathString, partnerName+"_suspend_"+fileDate+".csv");
			String exactMatchFailuresHeader = "tradename|type|vehicle|count|first_seen|last_seen\n";
			Files.write(exactMatchFailuresCSV, exactMatchFailuresHeader.getBytes(), StandardOpenOption.CREATE_NEW);
			BufferedWriter writer = Files.newBufferedWriter(exactMatchFailuresCSV, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			List<ExactMatchFailure> exactMatchFailures = wandDataService.getExactMatchFailures(partnerName);
			for (ExactMatchFailure e : exactMatchFailures) {
				// Append failure to csv file
				writer.write(e.getTradename() + "|" + e.getType() + "|" + e.getVehicle() + "|" + e.getCount() + "|" + e.getFirstSeen() + "|" + e.getLastSeen() + "\n");
				writer.flush();
				// Update each posted attribute to true
				e.setPosted(true);
				wandDataService.save(partnerName, e);
			}
			// Compress
			is = Files.newInputStream(exactMatchFailuresCSV);
			Path exactMatchFailuresCSVGZ =  FileSystems.getDefault().getPath(tempPostPathString, partnerName+"_suspend_"+fileDate+".csv.gz");
			os = new GZIPOutputStream(Files.newOutputStream(exactMatchFailuresCSVGZ,StandardOpenOption.CREATE_NEW));
			int len; byte[] buffer = new byte[1024];
			while((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			is.close();
			os.finish();
			os.close();
			// Delete csv
			Files.delete(exactMatchFailuresCSV);
			// Move
			Files.move(exactMatchFailuresCSVGZ, Paths.get(wandPostPathString + exactMatchFailuresCSVGZ.getFileName()), StandardCopyOption.REPLACE_EXISTING);

			Path chaseFailuresCSV = FileSystems.getDefault().getPath(tempPostPathString, partnerName+"_KPI_suspend_"+fileDate+".csv");
			String chaseFailuresHeader = "term_code|frequency|trans_frequency|term_desc|type|vehicle|first_seen|last_seen\n";
			Files.write(chaseFailuresCSV, chaseFailuresHeader.getBytes(), StandardOpenOption.CREATE_NEW);
			writer = Files.newBufferedWriter(chaseFailuresCSV, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			List<ChaseFailure> chaseFailures = wandDataService.getChaseFailures(partnerName);
			for (ChaseFailure c : chaseFailures) {
				// Append failure to csv file
				writer.write(c.getTermCode() + "|" + c.getFrequency() + "|" + c.getTransFrequency() + "|" + c.getTermDesc() + "|" + c.getType() + "|" + c.getVehicle() + "|" + c.getVehicle() + "|" + c.getFirstSeen() + "|" + c.getLastSeen() + "\n");
				writer.flush();
				// Update each posted attribute to true
				c.setPosted(true);
				wandDataService.save(partnerName, c);
			}
			// Compress
			is = Files.newInputStream(chaseFailuresCSV);
			Path chaseFailuresCSVGZ =  FileSystems.getDefault().getPath(tempPostPathString, partnerName+"_KPI_suspend_"+fileDate+".csv.gz");
			os = new GZIPOutputStream(Files.newOutputStream(chaseFailuresCSVGZ,StandardOpenOption.CREATE_NEW));
			buffer = new byte[1024];
			while((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			is.close();
			os.finish();
			os.close();
			// Delete csv
			Files.delete(chaseFailuresCSV);
			// Move
			Files.move(chaseFailuresCSVGZ, Paths.get(wandPostPathString + chaseFailuresCSVGZ.getFileName()), StandardCopyOption.REPLACE_EXISTING);
		}
		msg = WANDPostJob.class.getSimpleName() + " completed.";
		LOG.info(msg);
		// TODO delete System.out.println
		System.out.println(msg);
	}

	public static void main(String[] args) throws Exception {
		Map<String,String> properties = new HashMap<>();
		properties.put("topology.cassandra.seednode","165.13.51.145");
		DataServiceManager.setProperties(properties);
		String wandPostPath = "/tmp/end/";

		JobDataMap wandPostJobDataMap = new JobDataMap();
		wandPostJobDataMap.put("wandPostPath", wandPostPath);

		new WANDPostJob().postToWAND(wandPostJobDataMap);
	}
}
