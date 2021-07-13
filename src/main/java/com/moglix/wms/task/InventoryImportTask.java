	package com.moglix.wms.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moglix.wms.api.request.BatchRefRequest;
import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.api.request.CreateInboundStorageRequest;
import com.moglix.wms.api.request.CreateInboundStorageRequest.LocationToQuantity;
import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.BatchRefResponse;
import com.moglix.wms.api.response.CreateInboundStorageResponse;
import com.moglix.wms.api.response.FileUploadResponse;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.dto.EMSInventory;
import com.moglix.wms.dto.EMSInventoryDTO;
import com.moglix.wms.dto.InboundPoItemIdQuntityDTO;
import com.moglix.wms.dto.ProductInboundInventoryImportCSVContent;
import com.moglix.wms.dto.ProductInboundInventoryImportCSVContentWithoutBin;
import com.moglix.wms.dto.ProductInboundInventoryStorageLocationImportCSVContent;
import com.moglix.wms.dto.ProductInventoryImportCSVContent;
import com.moglix.wms.dto.ProductInventoryImportCSVContentCSV;
import com.moglix.wms.dto.ProductInventoryImportCSVContentCSVWithoutBin;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.StorageLocation;
import com.moglix.wms.mapper.BatchMapper;
import com.moglix.wms.mapper.InboundMapper;
import com.moglix.wms.repository.BatchRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.StorageLocationRepository;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.impl.EMSInventoryServiceImpl;
import com.moglix.wms.util.DateUtil;
import com.moglix.wms.util.NumberUtil;
import com.opencsv.CSVWriter;

@Service
public class InventoryImportTask {

	Logger log = LogManager.getLogger(InventoryImportTask.class);

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private EMSInventoryServiceImpl emsInventoryServiceImpl;

	@Autowired
	private IInboundStorageService inboundStorageService;


	@Autowired
	IProductService prodService;

	@Autowired
	ProductsRepository prodRepo;

	@Autowired
	BatchRepository batchrepository;

	@Autowired
	StorageLocationRepository storageLocationRepository;

	@Value("${queue.allocation}")
	private String ALLOCATION_QUEUE;


	@Autowired
	private InboundRepository inboundRepo;

	@Autowired
	IBatchService batchService;




	@SuppressWarnings("unused")
	public BaseResponse migrate(int numberOfThreads, String filename) throws IOException {

		BaseResponse response = new BaseResponse();

		Map<String, Batch> mrnBatchMap = new HashMap<>();

		Map<String, Product> productMsnMap = new HashMap<>();

		List<EMSInventory> emsInventory = emsInventoryServiceImpl.getProductMrnInventory();

		Map<String, List<EMSInventory>> emsInventoryMap = emsInventory.stream().collect(Collectors
				.groupingBy(p -> getGroupingByKey(p), Collectors.mapping((EMSInventory p) -> p, Collectors.toList())));

		Map<String, ProductInventoryImportCSVContent> productInventoryImportContentMap = getProductInventoryImportContent(
				Constants.UPLOADED_FOLDER + filename);

		for (Map.Entry<String, ProductInventoryImportCSVContent> entry : productInventoryImportContentMap.entrySet()) {

			List<EMSInventoryDTO> emsInventoryDTOs = new ArrayList<>();

			String productMsn = entry.getKey();
			Double sheetQuantity = NumberUtil.round4(Double.parseDouble(entry.getValue().getQuantity()));
			Integer poId = Integer.parseInt(entry.getValue().getPoId());
			Integer mrnId = Integer.parseInt(entry.getValue().getMrnId());

			List<EMSInventory> emsInventories = emsInventoryMap.get(productMsn);

			if (sheetQuantity <= 0 || CollectionUtils.isEmpty(emsInventories)) {
				continue;
			}

			emsInventories = emsInventories.stream().filter(s -> s.getPoId().equals(poId))
					.filter(s -> s.getMrnId().equals(mrnId)).collect(Collectors.toList());

			//			emsInventories.sort(Comparator.comparing(EMSInventory::getMrnDate).reversed());

			for (EMSInventory emsInventoryRecord : emsInventories) {

				EMSInventoryDTO inventoryDTO = new EMSInventoryDTO(emsInventoryRecord);

				Double partQuantity = Math.min(sheetQuantity, emsInventoryRecord.getArrivedQuantity());

				inventoryDTO.setArrivedQuantity(partQuantity);

				emsInventoryDTOs.add(inventoryDTO);

				sheetQuantity = sheetQuantity - partQuantity;

				if (sheetQuantity == 0) {
					break;
				}
			}

			for (EMSInventoryDTO emsInventoryEntry : emsInventoryDTOs) {

				Product product;
				if (productMsnMap.containsKey(emsInventoryEntry.getProductMpn())) {
					log.info("Product Already exists in cache");
					product = productMsnMap.get(emsInventoryEntry.getProductMpn());
				} else {
					log.info("Product doesn't exist in cache. Fetching from DB: " + emsInventoryEntry.getProductMpn());
					product = prodRepo.getUniqueByProductMsn(emsInventoryEntry.getProductMpn());
					if (product == null) {
						log.info("Product here is: " + product);
					}
					productMsnMap.put(emsInventoryEntry.getProductMpn(), product);
				}

				Batch batch = BatchMapper.createBatchFromEmsInventory(emsInventoryEntry);

				batch.setWarehouseName(entry.getValue().getWarehouseName());

				Inbound inbound = InboundMapper.createInboundFromEmsInventory(emsInventoryEntry);

				inbound.setProduct(product);

				inbound.setWarehouseName(entry.getValue().getWarehouseName());

				if (mrnBatchMap.get(batch.getRefNo()) != null) {
					Set<Inbound> inbounds = new HashSet<>(mrnBatchMap.get(batch.getRefNo()).getInbounds());
					inbounds.add(inbound);
					mrnBatchMap.get(batch.getRefNo()).setInbounds(inbounds);
					inbound.setBatch(mrnBatchMap.get(batch.getRefNo()));
				} else {
					batch.setInbounds(Collections.singleton(inbound));
					inbound.setBatch(batch);
					mrnBatchMap.put(batch.getRefNo(), batch);
				}

				log.info("Batch created is: " + batch.getRefNo());
			}

		}

		saveBatchToDatabase(mrnBatchMap.values());
		response.setMessage("File imported successfully");
		response.setStatus(true);
		response.setCode(HttpStatus.OK.value());
		return response;
	}

	private void saveBatchToDatabase(Iterable<Batch> batches) {
		for (Batch batch : batches) {
			try {

				Batch inwardBatch = batchrepository.findByRefNo(batch.getRefNo());
				if (inwardBatch != null) {
					log.info("Batch already exists: " + batch.getRefNo());
					if(CollectionUtils.isEmpty(inwardBatch.getInbounds())) {
						Set<Inbound> inwardInbounds = new HashSet<>();
						for(Inbound inbound : batch.getInbounds()) {
							inbound.setBatch(inwardBatch);
							inwardInbounds.add(inbound);
						}
						log.info("Saving inbounds: " + batch.getRefNo());
						inboundRepo.saveAll(inwardInbounds);
					}else {
						Set<Inbound>inwardInbounds = new HashSet<>(inwardBatch.getInbounds());
						for(Inbound inbound: batch.getInbounds()){
							inbound.setBatch(inwardBatch);
							inwardInbounds.add(inbound);
						}
						log.info("Saving inbounds: " + batch.getRefNo());
						inboundRepo.saveAll(inwardInbounds);
					}
				} else {
					log.info("Saving Batch to Database: " + batch.getRefNo());

					CreateBatchRequest request = new CreateBatchRequest();

					List<ProductInput> inputs = new ArrayList<>();

					for (Inbound inbound : batch.getInbounds()) {
						ProductInput input = InboundMapper.createInputFromEntity(inbound);
						input.setInventrisableQuantity(inbound.getInventorisableQuantity());
						inputs.add(input);
					}
					request.setBatchType(BatchType.INBOUND);
					request.setRefNo(batch.getRefNo());
					request.setPurchaseDate(DateUtil.getCurrentDateTime());
					request.setInboundedBy(batch.getInboundedBy());
					request.setSupplierId(batch.getSupplierId());
					request.setSupplierName(batch.getSupplierName());
					request.setWarehouseId(batch.getWarehouseId());
					request.setWarehouseName(batch.getWarehouseName());

					request.setProducts(inputs);

					batchService.createBatch(request);

				}
			} catch (Exception e) {
				log.error("Error in saving batch with refNo: " + batch.getRefNo(), e);
			}
		}
	}

	private Map<String, ProductInventoryImportCSVContent> getProductInventoryImportContent(String filePath) throws IOException {
		Map<String, ProductInventoryImportCSVContent> inventoryProductMsnMap = new HashMap<String, ProductInventoryImportCSVContent>();

		if(!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + Constants.CSV_FILE_PATH);
		}


		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {
				ProductInventoryImportCSVContent content = mapper.convertValue(record.toMap(),
						ProductInventoryImportCSVContent.class);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getMrnId() + "-" + content.getPoId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getMrnId() + "-" + content.getPoId(), content);
				} else {
					String tempQuantity = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getMrnId() + "-" + content.getPoId()).getQuantity();

					if (Double.parseDouble(tempQuantity) > 0) {
						content.setQuantity(String
								.valueOf(Double.parseDouble(content.getQuantity()) + Double.parseDouble(tempQuantity)));

						inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getMrnId() + "-" + content.getPoId(), content);

					}
				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}

		return inventoryProductMsnMap;
	}

	private String getGroupingByKey(EMSInventory p) {
		return p.getProductMpn() + "-" + p.getWarehouseId() + "-" + p.getMrnId() + "-" + p.getPoId();
	}




	public BatchRefResponse migrateCSV(int numberOfThreads, String filename) throws IOException {
		log.info("migrating CSV started with filename :" + filename);
		BatchRefResponse response = new BatchRefResponse();

		Map<String, Batch> mrnBatchMap = new HashMap<>();

		Map<String, Product> productMsnMap = new HashMap<>();

		List<EMSInventory> emsInventory = emsInventoryServiceImpl.getProductMrnInventoryCSV();

		List<InboundPoItemIdQuntityDTO> listInboundPoItemIdQuntityDTO = inboundRepo.getInboundQuantityByPoItemId();

		Map<Integer, Double> mapPoItemIdQuanity = new HashMap<>();
		for (InboundPoItemIdQuntityDTO inboundPoItemIdQuntityDTO : listInboundPoItemIdQuntityDTO) {
			mapPoItemIdQuanity.put(inboundPoItemIdQuntityDTO.getPoItemId(),
					inboundPoItemIdQuntityDTO.getTotalQuantity());
		}

		Map<String, List<EMSInventory>> emsInventoryMap = emsInventory.stream().collect(Collectors.groupingBy(
				p -> getGroupingByKeyCSV(p), Collectors.mapping((EMSInventory p) -> p, Collectors.toList())));

		Map<String, ProductInventoryImportCSVContentCSV> productInventoryImportContentMap = getProductInventoryImportContentCSV(
				Constants.UPLOADED_FOLDER + filename);

		for (Map.Entry<String, ProductInventoryImportCSVContentCSV> entry : productInventoryImportContentMap
				.entrySet()) {

			List<EMSInventoryDTO> emsInventoryDTOs = new ArrayList<>();

			String productMsn = entry.getKey();
			Double sheetQuantity = NumberUtil.round4(Double.parseDouble(entry.getValue().getQuantity()));
			// Integer poId = Integer.parseInt(entry.getValue().getPoId());
			// Integer mrnId = Integer.parseInt(entry.getValue().getMrnId());

			List<EMSInventory> emsInventories = emsInventoryMap.get(productMsn);

			if (sheetQuantity <= 0 || CollectionUtils.isEmpty(emsInventories)) {
				continue;
			}

			emsInventories = emsInventories.stream().collect(Collectors.toList());

			emsInventories.sort(Comparator.comparing(EMSInventory::getMrnDate).reversed());

			for (EMSInventory emsInventoryRecord : emsInventories) {

				EMSInventoryDTO inventoryDTO = new EMSInventoryDTO(emsInventoryRecord);

				Double partQuantity = Math.min(sheetQuantity, emsInventoryRecord.getArrivedQuantity());

				if (mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId()) != null) {
					log.info("Inbound already existed of poItemId:  " + emsInventoryRecord.getPoItemId()
					+ "with quantity :" + mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId())
					+ "so inbounding remaining quantity");
					if ((emsInventoryRecord.getArrivedQuantity()
							- mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId())) > 0.0) {
						partQuantity = Math.min(sheetQuantity, (emsInventoryRecord.getArrivedQuantity()
								- mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId())));
						if(Double.compare(partQuantity, 0.0) == 0) {
							continue;
						}
					} else {
						continue;
					}
				}

				inventoryDTO.setArrivedQuantity(partQuantity);

				emsInventoryDTOs.add(inventoryDTO);

				sheetQuantity = sheetQuantity - partQuantity;

				if (sheetQuantity == 0) {
					break;
				}
			}

			for (EMSInventoryDTO emsInventoryEntry : emsInventoryDTOs) {

				Product product;
				if (productMsnMap.containsKey(emsInventoryEntry.getProductMpn())) {
					log.info("Product Already exists in cache");
					product = productMsnMap.get(emsInventoryEntry.getProductMpn());
				} else {
					log.info("Product doesn't exist in cache. Fetching from DB: " + emsInventoryEntry.getProductMpn());
					product = prodRepo.getUniqueByProductMsn(emsInventoryEntry.getProductMpn());
					if (product == null) {
						log.info("Product here is: " + product);
					}
					productMsnMap.put(emsInventoryEntry.getProductMpn(), product);
				}

				Batch batch = BatchMapper.createBatchFromEmsInventory(emsInventoryEntry);

				batch.setWarehouseName(entry.getValue().getWarehouseName());

				Inbound inbound = InboundMapper.createInboundFromEmsInventory(emsInventoryEntry);

				inbound.setProduct(product);

				inbound.setWarehouseName(entry.getValue().getWarehouseName());

				if (mrnBatchMap.get(batch.getRefNo()) != null) {
					Set<Inbound> inbounds = new HashSet<>(mrnBatchMap.get(batch.getRefNo()).getInbounds());
					inbounds.add(inbound);
					mrnBatchMap.get(batch.getRefNo()).setInbounds(inbounds);
					inbound.setBatch(mrnBatchMap.get(batch.getRefNo()));
				} else {
					batch.setInbounds(Collections.singleton(inbound));
					inbound.setBatch(batch);
					mrnBatchMap.put(batch.getRefNo(), batch);
				}

				log.info("Batch created is: " + batch.getRefNo());
			}

		}

		response.setRefNumbers(saveBatchToDatabaseCSV(mrnBatchMap.values()));
		response.setMessage("File imported successfully");
		response.setStatus(true);
		response.setCode(HttpStatus.OK.value());
		return response;
	}

	private List<String> saveBatchToDatabaseCSV(Iterable<Batch> batches) {
		List<String> itemRefList = new ArrayList<>();

		for (Batch batch : batches) {
			try {
				log.info("fetching batch started by ref no : " + batch.getRefNo());
				Batch inwardBatch = batchrepository.findByRefNo(batch.getRefNo());
				log.info("fetching batch ended by ref no : " + batch.getRefNo());
				if (inwardBatch != null) {
					log.info("Batch already exists: " + batch.getRefNo());
					if (CollectionUtils.isEmpty(inwardBatch.getInbounds())) {
						Set<Inbound> inwardInbounds = new HashSet<>();
						for (Inbound inbound : batch.getInbounds()) {
							inbound.setBatch(inwardBatch);
							inwardInbounds.add(inbound);
						}
						log.info("Saving inbounds: " + batch.getRefNo());
						inboundRepo.saveAll(inwardInbounds);

					} else {
						Set<Inbound> inwardInbounds = new HashSet<>(inwardBatch.getInbounds());
						for (Inbound inbound : batch.getInbounds()) {
							inbound.setBatch(inwardBatch);
							inwardInbounds.add(inbound);
						}
						log.info("Saving inbounds: " + batch.getRefNo());

						inboundRepo.saveAll(inwardInbounds);
					}
				} else {
					log.info("Saving Batch to Database: " + batch.getRefNo());

					CreateBatchRequest request = new CreateBatchRequest();

					List<ProductInput> inputs = new ArrayList<>();

					for (Inbound inbound : batch.getInbounds()) {
						ProductInput input = InboundMapper.createInputFromEntity(inbound);
						input.setInventrisableQuantity(inbound.getInventorisableQuantity());
						inputs.add(input);
					}
					request.setBatchType(BatchType.INBOUND);
					request.setRefNo(batch.getRefNo());
					request.setPurchaseDate(DateUtil.getCurrentDateTime());
					request.setInboundedBy(batch.getInboundedBy());
					request.setSupplierId(batch.getSupplierId());
					request.setSupplierName(batch.getSupplierName());
					request.setWarehouseId(batch.getWarehouseId());
					request.setWarehouseName(batch.getWarehouseName());

					request.setProducts(inputs);

					batchService.createBatch(request);

				}

				itemRefList.add(batch.getRefNo());
			} catch (Exception e) {
				log.error("Error in saving batch with refNo: " + batch.getRefNo(), e);
			}
		}
		return itemRefList;
	}

	private Map<String, ProductInventoryImportCSVContentCSV> getProductInventoryImportContentCSV(String filePath)
			throws IOException {
		log.info("getProductInventoryImportContentCSV () started  with filepath :" + filePath);
		Map<String, ProductInventoryImportCSVContentCSV> inventoryProductMsnMap = new HashMap<String, ProductInventoryImportCSVContentCSV>();

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {
				ProductInventoryImportCSVContentCSV content = mapper.convertValue(record.toMap(),
						ProductInventoryImportCSVContentCSV.class);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), content);
				} else {
					String tempQuantity = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId()).getQuantity();

					if (Double.parseDouble(tempQuantity) > 0) {
						content.setQuantity(String
								.valueOf(Double.parseDouble(content.getQuantity()) + Double.parseDouble(tempQuantity)));

						inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), content);

					}
				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}
		log.info("getProductInventoryImportContentCSV () ended  with filepath :" + filePath);
		return inventoryProductMsnMap;
	}

	private String getGroupingByKeyCSV(EMSInventory p) {
		return p.getProductMpn() + "-" + p.getWarehouseId();
	}




	public  void writeDataToCSV(String fileName, List<String[]> dataCSV) throws IOException 
	{
		log.info("writeDataToCSV() method started with filename :" + fileName);

		File file = new File(fileName);

		FileWriter outputfile = new FileWriter(file);

		CSVWriter writer = new CSVWriter(outputfile);

		try {

			writer.writeAll(dataCSV);
			log.info("writing to file :" + fileName + "completed");

		} finally {
			writer.close();

		}
		log.info("writeDataToCSV() method ended with filename :" + fileName);

	}

	public FileUploadResponse writeToTemporaryCSV(BatchRefRequest request) {
		log.info("writeToTemporaryCSV method started with request :" + request.toString());
		List<String[]> dataCSV = new ArrayList<String[]>();
		dataCSV.add(new String[] { "Warehouse Name", "warehouse_id", "MSN", "Product Description", "Quantity in stock",
				"UoM", "InboundId" });
		log.info("fetching Batch by ref numbers started");

		List<Batch> batchList = batchrepository.findByRefNoInAndBatchType(request.getRefNumbers(), BatchType.INBOUND);
		log.info("fetching Batch by ref numbers completed");

		for (Batch batch : batchList) {
			Set<Inbound> inbundList = new HashSet<>(batch.getInbounds());
			for (Inbound inboundEntity : inbundList) {
				if(Double.compare(inboundEntity.getQuantity(), 0.0) == 0) {
					continue;
				}
				if(!(inboundEntity.getType().equals(InboundType.NEW) && inboundEntity.getStatus().equals(InboundStatusType.STARTED))) {
					continue;
				}
				dataCSV.add(new String[] { inboundEntity.getWarehouseName(), inboundEntity.getWarehouseId().toString(),
						inboundEntity.getProduct().getProductMsn(), inboundEntity.getProductName(),
						inboundEntity.getQuantity().toString(), inboundEntity.getProduct().getUom(),
						inboundEntity.getId().toString() });
			}

		}
		String fileName = Constants.UPLOADED_FOLDER + "bbbb.csv";
		try {
			log.info("writing  started into bbbb.csv file");

			writeDataToCSV(fileName, dataCSV);
			log.info("writing  ended into bbbb.csv file");

		} catch (Exception e) {
			log.error("Exception occurred in writing  into bbbb.csv file", e);
		}
		String fileNameCC = Constants.UPLOADED_FOLDER + "cccc.csv";

		String fileNameDD = Constants.UPLOADED_FOLDER + "dddd.csv";

		FileUploadResponse fileUploadResponse = new FileUploadResponse("file written in csv", true, 200, fileName);
		String originalfileName = request.getOriginalFileName();
		Map<String, List<ProductInventoryImportCSVContentCSV>> productInventoryImportContentMap = null;
		Map<String, List<ProductInboundInventoryImportCSVContent>> productInventoryInboundImportContentMap = null;

		try {
			log.info("fetching productInventoryImportContentMap  from original file started ");
			productInventoryImportContentMap = getProductInventoryImportContentList(
					Constants.UPLOADED_FOLDER + originalfileName);
			log.info("fetching productInventoryImportContentMap  from original file ended ");

		} catch (IOException e) {
			log.error("Exception occurred in fetching productInventoryImportContentMap  from original file", e);
		}

		String mediumFileName = "bbbb.csv";
		try {
			log.info("fetching productInventoryImportContentMap  from bbbb.csv started ");
			productInventoryInboundImportContentMap = getProductInventoryInboundImportContentList(
					Constants.UPLOADED_FOLDER + mediumFileName);
			log.info("fetching productInventoryImportContentMap  from bbbb.csv file ended ");
		} catch (IOException e) {
			log.error("Exception occurred in fetching productInventoryImportContentMap  from bbbb.csv", e);
		}

		List<String[]> dataCSVCCC = new ArrayList<String[]>();
		dataCSVCCC.add(new String[] { "Warehouse Name", "warehouse_id", "zone", "bin", "MSN", "Product Description",
				"Quantity in stock", "UoM", "InboundId" });

		List<String[]> dataCSVDDD = new ArrayList<String[]>();
		dataCSVDDD.add(new String[] { "Warehouse Name", "warehouse_id", "zone", "bin", "MSN", "Product Description",
				"Quantity in stock", "UoM" });
		for (Map.Entry<String, List<ProductInventoryImportCSVContentCSV>> entryProductInventory : productInventoryImportContentMap
				.entrySet()) {
			List<ProductInventoryImportCSVContentCSV> productInventoryImportCSVContentList = entryProductInventory
					.getValue();

			List<ProductInboundInventoryImportCSVContent> productInboundInventoryImportCSVContentList = productInventoryInboundImportContentMap
					.get(entryProductInventory.getKey());
             if(productInboundInventoryImportCSVContentList == null) {
            	 continue;
             }
			for (ProductInboundInventoryImportCSVContent productInboundInventoryImportCSVContent : productInboundInventoryImportCSVContentList) {
				for (ProductInventoryImportCSVContentCSV productInventoryImportCSVContent : productInventoryImportCSVContentList) {
					if (NumberUtil.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity())) == 0.0) {
						continue;
					}
					Double partQuantity = Math.min(
							NumberUtil.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity())),
							NumberUtil
							.round4(Double.parseDouble(productInboundInventoryImportCSVContent.getQuantity())));
					Double productInventoryQuantity = NumberUtil
							.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity())) - partQuantity;
					Double productInboundInventoryQuantity = NumberUtil.round4(
							Double.parseDouble(productInboundInventoryImportCSVContent.getQuantity())) - partQuantity;
					productInventoryImportCSVContent.setQuantity(Double.toString(productInventoryQuantity));
					productInboundInventoryImportCSVContent
					.setQuantity(Double.toString(productInboundInventoryQuantity));

					dataCSVCCC.add(new String[] { productInboundInventoryImportCSVContent.getWarehouseName(),
							productInboundInventoryImportCSVContent.getWarehouseId(),
							productInventoryImportCSVContent.getZone(), productInventoryImportCSVContent.getBin(),
							productInboundInventoryImportCSVContent.getProductMsn(),
							productInboundInventoryImportCSVContent.getProductName(), Double.toString(partQuantity),
							productInventoryImportCSVContent.getUom(),
							productInboundInventoryImportCSVContent.getInboundId() });

					if (Double.compare(productInboundInventoryQuantity, 0.0) == 0) {
						break;
					}
				}
			}

		}

		for (Map.Entry<String, List<ProductInventoryImportCSVContentCSV>> entryProductInventory : productInventoryImportContentMap
				.entrySet()) {
			List<ProductInventoryImportCSVContentCSV> productInventoryImportCSVContentList = entryProductInventory
					.getValue();
			for (ProductInventoryImportCSVContentCSV productInventoryImportCSVContent : productInventoryImportCSVContentList) {
				if (NumberUtil.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity())) == 0.0) {
					continue;
				}
				dataCSVDDD.add(new String[] { productInventoryImportCSVContent.getWarehouseName(),
						productInventoryImportCSVContent.getWarehouseId(), productInventoryImportCSVContent.getZone(),
						productInventoryImportCSVContent.getBin(), productInventoryImportCSVContent.getProductMsn(),
						productInventoryImportCSVContent.getProductName(),
						productInventoryImportCSVContent.getQuantity(), productInventoryImportCSVContent.getUom() });

			}

		}

		try {
			log.info("wrting to cccc.csv and dddd.csv started");
			writeDataToCSV(fileNameCC, dataCSVCCC);
			writeDataToCSV(fileNameDD, dataCSVDDD);
			log.info("wrting to cccc.csv and dddd.csv ended");
		} catch (Exception e) {
			log.error("Exception Occurred in writing cccc.csv and dddd.csv file ", e);
		}
		List<ProductInboundInventoryStorageLocationImportCSVContent> productInboundInventoryStorageLocationImportCSVContentList = null;

		try {
			log.info("fetching productInboundInventoryStorageLocationImportCSVContentList from file cccc.csv started");
			productInboundInventoryStorageLocationImportCSVContentList = getProductInboundInventoryImportContentList(
					fileNameCC);
			log.info("fetching productInboundInventoryStorageLocationImportCSVContentList from file cccc.csv ended");
		} catch (IOException e) {
			log.error(
					"Exception occurred in fetching productInboundInventoryStorageLocationImportCSVContentList from file cccc.csv",
					e);
		}

		Map<String, List<ProductInboundInventoryStorageLocationImportCSVContent>> inboundLocationQuantityMap = productInboundInventoryStorageLocationImportCSVContentList
				.stream()
				.collect(Collectors.groupingBy(ProductInboundInventoryStorageLocationImportCSVContent::getInboundId,
						Collectors.mapping((ProductInboundInventoryStorageLocationImportCSVContent p) -> p,
								Collectors.toList())));

		for (Entry<String, List<ProductInboundInventoryStorageLocationImportCSVContent>> entryinboundLocationQuantityMap : inboundLocationQuantityMap
				.entrySet()) {
			Integer flag = 1;
			List<ProductInboundInventoryStorageLocationImportCSVContent> productInventoryImportCSVContentList = entryinboundLocationQuantityMap
					.getValue();
			CreateInboundStorageRequest createInboundStorageRequest = new CreateInboundStorageRequest();
			List<LocationToQuantity> items = new ArrayList<>();
			createInboundStorageRequest.setInboundId(Integer.parseInt(entryinboundLocationQuantityMap.getKey()));
			createInboundStorageRequest.setBinAssignedBy("AUTO_ASSIGNED");
			for (ProductInboundInventoryStorageLocationImportCSVContent productInventoryImportCSVContent : productInventoryImportCSVContentList) {
				LocationToQuantity locationQuantity = new LocationToQuantity();
				Integer warehouseId = Integer.parseInt(productInventoryImportCSVContent.getWarehouseId());
				String zone = productInventoryImportCSVContent.getZone();
				String bin = productInventoryImportCSVContent.getBin();
				StorageLocation storageLoacation = storageLocationRepository
						.getStorageLocationByWarehouseZoneAndBin(warehouseId, zone, bin);
				if (storageLoacation == null) {
					log.info("storage location not found for  zone : " + zone + "bin :" + bin + "warehouseId :"
							+ warehouseId + "for InboundId :" + entryinboundLocationQuantityMap.getKey());
					flag = 0;
					break;
				}
				locationQuantity.setStorageLocationId(storageLoacation.getId());
				locationQuantity.setQuantity(Double.parseDouble(productInventoryImportCSVContent.getQuantity()));
				items.add(locationQuantity);
				createInboundStorageRequest.setItems(items);
			}
			if (flag == 0) {
				continue;
			}
			log.info("assign bin service started with request :" + createInboundStorageRequest.toString());
			CreateInboundStorageResponse createInboundStorageResponse = inboundStorageService
					.create(createInboundStorageRequest);
			log.info("assign bin service ended with response :" + createInboundStorageResponse.toString());

		}

		return fileUploadResponse;
	} 



	private Map<String, List<ProductInventoryImportCSVContentCSV>> getProductInventoryImportContentList(String filePath)
			throws IOException {
		Map<String, List<ProductInventoryImportCSVContentCSV>> inventoryProductMsnMap = new HashMap<String, List<ProductInventoryImportCSVContentCSV>>();

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {

				ProductInventoryImportCSVContentCSV content = mapper.convertValue(record.toMap(),
						ProductInventoryImportCSVContentCSV.class);
				List<ProductInventoryImportCSVContentCSV> contentList = new ArrayList<>();
				contentList.add(content);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), contentList);
				} else {

					List<ProductInventoryImportCSVContentCSV> contentListTotal = new ArrayList<>();
					contentListTotal = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId());
					contentListTotal.add(content);
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(),
							contentListTotal);

				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}

		return inventoryProductMsnMap;
	}


	private Map<String, List<ProductInboundInventoryImportCSVContent>> getProductInventoryInboundImportContentList(
			String filePath) throws IOException {
		Map<String, List<ProductInboundInventoryImportCSVContent>> inventoryProductMsnMap = new HashMap<String, List<ProductInboundInventoryImportCSVContent>>();

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {

				ProductInboundInventoryImportCSVContent content = mapper.convertValue(record.toMap(),
						ProductInboundInventoryImportCSVContent.class);
				List<ProductInboundInventoryImportCSVContent> contentList = new ArrayList<>();
				contentList.add(content);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), contentList);
				} else {

					List<ProductInboundInventoryImportCSVContent> contentListTotal = new ArrayList<>();
					contentListTotal = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId());
					contentListTotal.add(content);
					contentListTotal.sort(Comparator.comparing(ProductInboundInventoryImportCSVContent::getQuantity));
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(),
							contentListTotal);

				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}

		return inventoryProductMsnMap;
	}


	private List<ProductInboundInventoryStorageLocationImportCSVContent> getProductInboundInventoryImportContentList(
			String filePath) throws IOException {

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		List<ProductInboundInventoryStorageLocationImportCSVContent> contentList = new ArrayList<>();

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {

				ProductInboundInventoryStorageLocationImportCSVContent content = mapper.convertValue(record.toMap(),
						ProductInboundInventoryStorageLocationImportCSVContent.class);
				contentList.add(content);

			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}

		return contentList;
	}

	public BatchRefResponse migrateCSVWithoutBin(int numberOfThreads, String filename) throws IOException {

		log.info("migrating CSV started with filename :" + filename);
		BatchRefResponse response = new BatchRefResponse();

		Map<String, Batch> mrnBatchMap = new HashMap<>();

		Map<String, Product> productMsnMap = new HashMap<>();

		List<EMSInventory> emsInventory = emsInventoryServiceImpl.getProductMrnInventory();

		List<InboundPoItemIdQuntityDTO> listInboundPoItemIdQuntityDTO = inboundRepo.getInboundQuantityByPoItemId();

		Map<Integer, Double> mapPoItemIdQuanity = new HashMap<>();
		for (InboundPoItemIdQuntityDTO inboundPoItemIdQuntityDTO : listInboundPoItemIdQuntityDTO) {
			mapPoItemIdQuanity.put(inboundPoItemIdQuntityDTO.getPoItemId(),
					inboundPoItemIdQuntityDTO.getTotalQuantity());
		}

		Map<String, List<EMSInventory>> emsInventoryMap = emsInventory.stream().collect(Collectors.groupingBy(
				p -> getGroupingByKeyCSV(p), Collectors.mapping((EMSInventory p) -> p, Collectors.toList())));

		Map<String, ProductInventoryImportCSVContentCSVWithoutBin> productInventoryImportContentMap = getProductInventoryImportContentCSVWithouBin(
				Constants.UPLOADED_FOLDER + filename);

		for (Map.Entry<String, ProductInventoryImportCSVContentCSVWithoutBin> entry : productInventoryImportContentMap
				.entrySet()) {

			List<EMSInventoryDTO> emsInventoryDTOs = new ArrayList<>();

			String productMsn = entry.getKey();
			Double sheetQuantity = NumberUtil.round4(Double.parseDouble(entry.getValue().getQuantity()));
			// Integer poId = Integer.parseInt(entry.getValue().getPoId());
			// Integer mrnId = Integer.parseInt(entry.getValue().getMrnId());

			List<EMSInventory> emsInventories = emsInventoryMap.get(productMsn);

			if (sheetQuantity <= 0 || CollectionUtils.isEmpty(emsInventories)) {
				continue;
			}

			emsInventories = emsInventories.stream().collect(Collectors.toList());

			emsInventories.sort(Comparator.comparing(EMSInventory::getMrnDate).reversed());

			for (EMSInventory emsInventoryRecord : emsInventories) {

				EMSInventoryDTO inventoryDTO = new EMSInventoryDTO(emsInventoryRecord);

				Double partQuantity = Math.min(sheetQuantity, emsInventoryRecord.getArrivedQuantity());

				if (mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId()) != null) {
					log.info("Inbound already existed of poItemId:  " + emsInventoryRecord.getPoItemId()
					+ "with quantity :" + mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId())
					+ "so inbounding remaining quantity");
					if ((emsInventoryRecord.getArrivedQuantity()
							- mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId())) > 0.0) {
						partQuantity = Math.min(sheetQuantity, (emsInventoryRecord.getArrivedQuantity()
								- mapPoItemIdQuanity.get(emsInventoryRecord.getPoItemId())));
						if(Double.compare(partQuantity, 0.0) == 0) {
							continue;
						}
					} else {
						continue;
					}
				}

				inventoryDTO.setArrivedQuantity(partQuantity);

				emsInventoryDTOs.add(inventoryDTO);

				sheetQuantity = sheetQuantity - partQuantity;

				if (sheetQuantity == 0) {
					break;
				}
			}

			for (EMSInventoryDTO emsInventoryEntry : emsInventoryDTOs) {

				Product product;
				if (productMsnMap.containsKey(emsInventoryEntry.getProductMpn())) {
					log.info("Product Already exists in cache");
					product = productMsnMap.get(emsInventoryEntry.getProductMpn());
				} else {
					log.info("Product doesn't exist in cache. Fetching from DB: " + emsInventoryEntry.getProductMpn());
					product = prodRepo.getUniqueByProductMsn(emsInventoryEntry.getProductMpn());
					if (product == null) {
						log.info("Product here is: " + product);
					}
					productMsnMap.put(emsInventoryEntry.getProductMpn(), product);
				}

				Batch batch = BatchMapper.createBatchFromEmsInventory(emsInventoryEntry);

				batch.setWarehouseName(entry.getValue().getWarehouseName());

				Inbound inbound = InboundMapper.createInboundFromEmsInventory(emsInventoryEntry);

				inbound.setProduct(product);

				inbound.setWarehouseName(entry.getValue().getWarehouseName());

				if (mrnBatchMap.get(batch.getRefNo()) != null) {
					Set<Inbound> inbounds = new HashSet<>(mrnBatchMap.get(batch.getRefNo()).getInbounds());
					inbounds.add(inbound);
					mrnBatchMap.get(batch.getRefNo()).setInbounds(inbounds);
					inbound.setBatch(mrnBatchMap.get(batch.getRefNo()));
				} else {
					batch.setInbounds(Collections.singleton(inbound));
					inbound.setBatch(batch);
					mrnBatchMap.put(batch.getRefNo(), batch);
				}

				log.info("Batch created is: " + batch.getRefNo());
			}

		}

		response.setRefNumbers(saveBatchToDatabaseCSV(mrnBatchMap.values()));
		response.setMessage("File imported successfully");
		response.setStatus(true);
		response.setCode(HttpStatus.OK.value());
		return response;
	
	}
	
	
	private Map<String, ProductInventoryImportCSVContentCSVWithoutBin> getProductInventoryImportContentCSVWithouBin(String filePath)
			throws IOException {
		log.info("getProductInventoryImportContentCSV () started  with filepath :" + filePath);
		Map<String, ProductInventoryImportCSVContentCSVWithoutBin> inventoryProductMsnMap = new HashMap<String, ProductInventoryImportCSVContentCSVWithoutBin>();

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {
				ProductInventoryImportCSVContentCSVWithoutBin content = mapper.convertValue(record.toMap(),
						ProductInventoryImportCSVContentCSVWithoutBin.class);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), content);
				} else {
					String tempQuantity = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId()).getQuantity();

					if (Double.parseDouble(tempQuantity) > 0) {
						content.setQuantity(String
								.valueOf(Double.parseDouble(content.getQuantity()) + Double.parseDouble(tempQuantity)));

						inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), content);

					}
				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}
		log.info("getProductInventoryImportContentCSV () ended  with filepath :" + filePath);
		return inventoryProductMsnMap;
	}

	public void writeToTemporaryCSVWithoutBin(@Valid BatchRefRequest request,HttpServletResponse response) {

		log.info("writeToTemporaryCSVWithoutBin method started with request :" + request.toString());
		List<String[]> dataCSV = new ArrayList<String[]>();
		dataCSV.add(new String[] { "Warehouse Name", "warehouse_id", "MSN", "Product Description", "Quantity in stock",
				"UoM", "InboundId" });
		log.info("fetching Batch by ref numbers started");

		List<Batch> batchList = batchrepository.findByRefNoInAndBatchType(request.getRefNumbers(), BatchType.INBOUND);
		log.info("fetching Batch by ref numbers completed");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dateSpecified = null;
		try {
			dateSpecified = sdf.parse(sdf.format(new Date()).toString());
		} catch (ParseException e1) {
			log.error("parsing error occuerred "+e1);
		}
		dateSpecified = DateUtils.addMinutes(dateSpecified, -10);
		
		for (Batch batch : batchList) {
			Set<Inbound> inbundList = new HashSet<>(batch.getInbounds());
			for (Inbound inboundEntity : inbundList) {	
				if(Double.compare(inboundEntity.getQuantity(), 0.0) == 0) {
					continue;
				}
				if(!(inboundEntity.getType().equals(InboundType.NEW) && inboundEntity.getStatus().equals(InboundStatusType.STARTED))) {
					continue;
				}
				
				if((dateSpecified != null) && DateUtil.convertStringToDate(inboundEntity.getCreated().toString(), "yyyy-MM-dd HH:mm", false).before(dateSpecified)) {
					continue;
				}
				dataCSV.add(new String[] { inboundEntity.getWarehouseName(), inboundEntity.getWarehouseId().toString(),
						inboundEntity.getProduct().getProductMsn(), inboundEntity.getProductName(),
						inboundEntity.getQuantity().toString(), inboundEntity.getProduct().getUom(),
						inboundEntity.getId().toString() });
			}

		}
		String fileName = Constants.UPLOADED_FOLDER + "bbbbWithoutBin.csv";
		try {
			log.info("writing  started into bbbbWithoutBin.csv file");

			writeDataToCSV(fileName, dataCSV);
			log.info("writing  ended into bbbbWithoutBin.csv file");

		} catch (Exception e) {
			log.error("Exception occurred in writing  into bbbbWithoutBin.csv file", e);
		}
		String fileNameCC = Constants.UPLOADED_FOLDER + "ccccWithoutBin.csv";

		String fileNameDD = Constants.UPLOADED_FOLDER + "ddddWithoutBin.csv";

		String originalfileName = request.getOriginalFileName();
		Map<String, List<ProductInventoryImportCSVContentCSVWithoutBin>> productInventoryImportContentMapWithoutBin = null;
		Map<String, List<ProductInboundInventoryImportCSVContentWithoutBin>> productInventoryInboundImportContentMapWithoutBin = null;

		try {
			log.info("fetching productInventoryImportContentMap  from original file started ");
			productInventoryImportContentMapWithoutBin = getProductInventoryImportContentListWithouBin(
					Constants.UPLOADED_FOLDER + originalfileName);
			log.info("fetching productInventoryImportContentMap  from original file ended ");

		} catch (IOException e) {
			log.error("Exception occurred in fetching productInventoryImportContentMap  from original file", e);
		}

		String mediumFileName = "bbbbWithoutBin.csv";
		try {
			log.info("fetching productInventoryImportContentMap  from bbbbWithoutBin.csv started ");
			productInventoryInboundImportContentMapWithoutBin = getProductInventoryInboundImportContentListWithoutBin(
					Constants.UPLOADED_FOLDER + mediumFileName);
			log.info("fetching productInventoryImportContentMap  from bbbbWithoutBin.csv file ended ");
		} catch (IOException e) {
			log.error("Exception occurred in fetching productInventoryImportContentMap  from bbbb.csv", e);
		}

		List<String[]> dataCSVCCC = new ArrayList<String[]>();
		dataCSVCCC.add(new String[] { "Warehouse Name", "warehouse_id", "MSN", "Product Description",
				"Quantity in stock", "UoM", "InboundId" });

		List<String[]> dataCSVDDD = new ArrayList<String[]>();
		dataCSVDDD.add(new String[] { "Warehouse Name", "warehouse_id", "MSN", "Product Description",
				"Quantity in stock", "UoM" });
		for (Entry<String, List<ProductInventoryImportCSVContentCSVWithoutBin>> entryProductInventory : productInventoryImportContentMapWithoutBin
				.entrySet()) {
			List<ProductInventoryImportCSVContentCSVWithoutBin> productInventoryImportCSVContentList = entryProductInventory
					.getValue();

			List<ProductInboundInventoryImportCSVContentWithoutBin> productInboundInventoryImportCSVContentList = productInventoryInboundImportContentMapWithoutBin
					.get(entryProductInventory.getKey());
             if(productInboundInventoryImportCSVContentList == null) {
            	 continue;
             }
			for (ProductInboundInventoryImportCSVContentWithoutBin productInboundInventoryImportCSVContent : productInboundInventoryImportCSVContentList) {
				for (ProductInventoryImportCSVContentCSVWithoutBin productInventoryImportCSVContent : productInventoryImportCSVContentList) {
					if (NumberUtil.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity())) == 0.0) {
						continue;
					}
					Double partQuantity = Math.min(
							NumberUtil.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity())),
							NumberUtil
							.round4(Double.parseDouble(productInboundInventoryImportCSVContent.getQuantity())));
					Double productInventoryQuantity = NumberUtil
							.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity()) - partQuantity) ;
					Double productInboundInventoryQuantity = NumberUtil.round4(
							Double.parseDouble(productInboundInventoryImportCSVContent.getQuantity())- partQuantity);
					productInventoryImportCSVContent.setQuantity(Double.toString(NumberUtil.round4(productInventoryQuantity)));
					productInboundInventoryImportCSVContent
					.setQuantity(Double.toString(productInboundInventoryQuantity));

					dataCSVCCC.add(new String[] { productInboundInventoryImportCSVContent.getWarehouseName(),
							productInboundInventoryImportCSVContent.getWarehouseId(),
							productInboundInventoryImportCSVContent.getProductMsn(),
							productInboundInventoryImportCSVContent.getProductName(), Double.toString(partQuantity),
							productInventoryImportCSVContent.getUom(),
							productInboundInventoryImportCSVContent.getInboundId() });

					if (Double.compare(productInboundInventoryQuantity, 0.0) == 0) {
						break;
					}
				}
			}

		}

		for (Map.Entry<String, List<ProductInventoryImportCSVContentCSVWithoutBin>> entryProductInventory : productInventoryImportContentMapWithoutBin
				.entrySet()) {
			List<ProductInventoryImportCSVContentCSVWithoutBin> productInventoryImportCSVContentList = entryProductInventory
					.getValue();
			for (ProductInventoryImportCSVContentCSVWithoutBin productInventoryImportCSVContent : productInventoryImportCSVContentList) {
				if (NumberUtil.round4(Double.parseDouble(productInventoryImportCSVContent.getQuantity())) == 0.0) {
					continue;
				}
				dataCSVDDD.add(new String[] { productInventoryImportCSVContent.getWarehouseName(),
						productInventoryImportCSVContent.getWarehouseId(), productInventoryImportCSVContent.getProductMsn(),
						productInventoryImportCSVContent.getProductName(),
						productInventoryImportCSVContent.getQuantity(), productInventoryImportCSVContent.getUom() });

			}

		}

		try {
			log.info("wrting to ccccWithoutBin.csv and ddddWithoutBin.csv started");
			writeDataToCSV(fileNameCC, dataCSVCCC);
			writeDataToCSV(fileNameDD, dataCSVDDD);
			log.info("wrting to ccccWithoutBin.csv and ddddWithoutBin.csv ended");
		} catch (Exception e) {
			log.error("Exception Occurred in writing cccc.csv and dddd.csv file ", e);
		}
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dddddd.csv");
		try {
			writeDDDDDataToCsv(response.getWriter(), dataCSVDDD);
		} catch (IOException e) {
			log.error("Error ocuured during dddd file exporting");
		}
		
	
	}


	private Map<String, List<ProductInventoryImportCSVContentCSVWithoutBin>> getProductInventoryImportContentListWithouBin(String filePath)
			throws IOException {
		Map<String, List<ProductInventoryImportCSVContentCSVWithoutBin>> inventoryProductMsnMap = new HashMap<String, List<ProductInventoryImportCSVContentCSVWithoutBin>>();

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {

				ProductInventoryImportCSVContentCSVWithoutBin content = mapper.convertValue(record.toMap(),
						ProductInventoryImportCSVContentCSVWithoutBin.class);
				List<ProductInventoryImportCSVContentCSVWithoutBin> contentList = new ArrayList<>();
				contentList.add(content);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), contentList);
				} else {

					List<ProductInventoryImportCSVContentCSVWithoutBin> contentListTotal = new ArrayList<>();
					contentListTotal = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId());
					contentListTotal.add(content);
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(),
							contentListTotal);

				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}

		return inventoryProductMsnMap;
	}
	
	
	private Map<String, List<ProductInboundInventoryImportCSVContentWithoutBin>> getProductInventoryInboundImportContentListWithoutBin(
			String filePath) throws IOException {
		Map<String, List<ProductInboundInventoryImportCSVContentWithoutBin>> inventoryProductMsnMap = new HashMap<String, List<ProductInboundInventoryImportCSVContentWithoutBin>>();

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {

				ProductInboundInventoryImportCSVContentWithoutBin content = mapper.convertValue(record.toMap(),
						ProductInboundInventoryImportCSVContentWithoutBin.class);
				List<ProductInboundInventoryImportCSVContentWithoutBin> contentList = new ArrayList<>();
				contentList.add(content);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(), contentList);
				} else {

					List<ProductInboundInventoryImportCSVContentWithoutBin> contentListTotal = new ArrayList<>();
					contentListTotal = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId());
					contentListTotal.add(content);
					contentListTotal.sort(Comparator.comparing(ProductInboundInventoryImportCSVContentWithoutBin::getQuantity));
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId(),
							contentListTotal);

				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}

		return inventoryProductMsnMap;
	}
	
	
	public  void writeDDDDDataToCsv(PrintWriter writer,List<String[]> dataCSVDDD) {
		try (
			     CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
	    ) {
	      for (String[] dddddDataRecord : dataCSVDDD) {
	        List<String> record = Arrays.asList(
	        		dddddDataRecord[0],
	        		dddddDataRecord[1],
	        		dddddDataRecord[2],
	        		dddddDataRecord[3],
	        		dddddDataRecord[4],
	        		dddddDataRecord[5]
	          );
	        
	        csvPrinter.printRecord(record);
	      }
	      csvPrinter.flush();
		    } catch (Exception e) {
		    	log.info("Error in creating CSV File", e);
		    }
	}
	
}
