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
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.StorageCSVHeaders;
import com.moglix.wms.constants.StorageCSVHeadersUpdate;
import com.moglix.wms.util.TaskUtil;

@Component
public class StorageImportTask {

	
	private static Logger  log = LogManager.getLogger(BatchImportTask.class);
	
	@Autowired
	TaskUtil taskUtil;

	private CSVParser parseCSV(String filePath) throws IOException {
		if(!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + Constants.STORAGE_CSV_FILE_PATH);
		}
		
		 Reader reader = Files.newBufferedReader(Paths.get(filePath));
		
		return CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
	}
	
	public void process(int numberOfThreads) throws IOException {

		if (!Paths.get(Constants.STORAGE_FAILURE_CSV_FILE_PATH).toFile().exists())
		    Files.createFile(Paths.get(Constants.STORAGE_FAILURE_CSV_FILE_PATH));
			
		ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
		CSVParser parsedCsv = parseCSV(Constants.STORAGE_CSV_FILE_PATH);
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
							new StorageImport(records.subList(start, end), threadIndex));
					start = end;
					end = end + range;
					threadIndex++;
				}
			}
		}
	}
	
	
	public void updateProcess(int numberOfThreads) throws IOException {

		if (!Paths.get(Constants.STORAGE_FAILURE_CSV_FILE_PATH).toFile().exists())
		    Files.createFile(Paths.get(Constants.STORAGE_FAILURE_CSV_FILE_PATH));
			
		ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
		CSVParser parsedCsv = parseCSV(Constants.STORAGE_CSV_FILE_PATH);
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
							new StorageImportUpdate(records.subList(start, end), threadIndex));
					start = end;
					end = end + range;
					threadIndex++;
				}
			}
		}
	}

	
	
	public class StorageImport implements Runnable{

		private final List<CSVRecord> csvRecords;

		private int threadIndex = 1;
		
		public StorageImport(List<CSVRecord> csvRecords, int threadIndex) {
			this.csvRecords = csvRecords;
			this.threadIndex = threadIndex;
		}

		@Override
		public void run() {
			log.info("Processing batch from Index: " + threadIndex);
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(Constants.STORAGE_FAILURE_CSV_FILE_PATH));
					CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(StorageCSVHeaders.class));) {
				log.info("Creating CsvPrinter to log error entries");
				ObjectMapper mapper = new ObjectMapper();
				for (CSVRecord record : csvRecords) {
					log.info("Processing record for warehouse: " + record.get("Warehouse") + " and Zone: "
							+ record.get("Zone"));
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
				taskUtil.processStorageRecord(mapper, record);
			} catch (Exception e) {
				log.error("Error occured in processing record for warehouse: " + record.get("Warehouse") + "and Zone: "
						+ record.get("Zone"), e);
				csvPrinter.printRecord(record);
			}
		}
	}
	
	public class StorageImportUpdate implements Runnable{

		private final List<CSVRecord> csvRecords;

		private int threadIndex = 1;
		
		public StorageImportUpdate(List<CSVRecord> csvRecords, int threadIndex) {
			this.csvRecords = csvRecords;
			this.threadIndex = threadIndex;
		}

		@Override
		public void run() {
			log.info("Processing batch from Index: " + threadIndex);
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(Constants.STORAGE_FAILURE_CSV_FILE_PATH));
					CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(StorageCSVHeadersUpdate.class));) {
				log.info("Creating CsvPrinter to log error entries");
				ObjectMapper mapper = new ObjectMapper();
				for (CSVRecord record : csvRecords) {
					log.info("Processing record for warehouse: " + record.get("Warehouse") + " and Zone: "
							+ record.get("Zone"));
					processRecordUpdate(csvPrinter, mapper, record);
				}
				log.info("Flushing Error Records to Disk..");
				csvPrinter.flush();
			} catch (IOException e) {
				log.error("Error Occurred in Processing Batch: " + threadIndex ,e);
			}			
		}

		private void processRecordUpdate(CSVPrinter csvPrinter, ObjectMapper mapper, CSVRecord record) throws IOException {
			try {
				taskUtil.processStorageRecordUpdate(mapper, record);
			} catch (Exception e) {
				log.error("Error occured in processing record for warehouse: " + record.get("Warehouse") + "and Zone: "
						+ record.get("Zone"), e);
				csvPrinter.printRecord(record);
			}
		}
	}
}
