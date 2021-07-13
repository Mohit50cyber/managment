package com.moglix.wms.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moglix.wms.constants.BatchCSVHeaders;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.util.TaskUtil;

@Component
public class BatchImportTask {
	
	private static Logger  log = LogManager.getLogger(BatchImportTask.class);
	
	@Autowired
	TaskUtil taskUtil;

	private CSVParser parseCSV(String filePath) throws IOException {
		if(!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + Constants.CSV_FILE_PATH);
		}
		
		 Reader reader = Files.newBufferedReader(Paths.get(filePath));
		
		return CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
	}
	
	public void process(int numberOfThreads) throws IOException {

		if (!Paths.get(Constants.FAILURE_CSV_FILE_PATH).toFile().exists())
		    Files.createFile(Paths.get(Constants.FAILURE_CSV_FILE_PATH));
			
		ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
		CSVParser parsedCsv = parseCSV(Constants.CSV_FILE_PATH);
		List<CSVRecord> records = parsedCsv.getRecords();
		int numOfRecords = records.size();
		if(!records.isEmpty()) {
			int start = 0;
			int range = (numOfRecords / numberOfThreads);
			int end = range;
			
			int threadIndex = 0;
			
			if (range != 0) {
				while (end <= numOfRecords) {					
					service.execute(
							new BatchImport(records.subList(start, end), threadIndex));
					start = end;
					end = end + range;
					threadIndex++;
				}
			}
		}
	}
	
	
	public class BatchImport implements Runnable{

		private final List<CSVRecord> csvRecords;

		private int threadIndex = 1;
		
		public BatchImport(List<CSVRecord> csvRecords, int threadIndex) {
			this.csvRecords = csvRecords;
			this.threadIndex = threadIndex;
		}

		@Override
		public void run() {
			log.info("Processing batch from Index: " + threadIndex);
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(Constants.FAILURE_CSV_FILE_PATH));
					CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(BatchCSVHeaders.class));) {
				log.info("Creating CsvPrinter to log error entries");
				ObjectMapper mapper = new ObjectMapper();
				for (CSVRecord record : csvRecords) {
					log.info("Processing record for refNo: " + record.get("ref_no") + " and productMsn: "
							+ record.get("product_msn"));
					processRecord(csvPrinter, mapper, record);
				}
				log.info("Flushing Error Records to Disk..");
				csvPrinter.flush();
			} catch (IOException e) {
				log.error("Error Occurred in Processing Batch: " + threadIndex ,e);
			}			
		}

		private void processRecord(CSVPrinter csvPrinter, ObjectMapper mapper, CSVRecord record) throws IOException {
			try {
				taskUtil.processRecord(mapper, record);
			} catch (Exception e) {
				log.error("Error occured in processing record for refNo: " + record.get("ref_no") + "and productMsn: "
						+ record.get("product_msn"), e);
				csvPrinter.printRecord(record);
			}
		}
	}
}
