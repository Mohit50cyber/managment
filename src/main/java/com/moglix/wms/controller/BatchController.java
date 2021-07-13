package com.moglix.wms.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moglix.wms.api.request.BatchRefRequest;
import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.api.request.DeleteBatchRequest;
import com.moglix.wms.api.request.RollbackBatchRequest;
import com.moglix.wms.api.request.SupplierCNCancelRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.BatchRefResponse;
import com.moglix.wms.api.response.CancelSupplierCNResponse;
import com.moglix.wms.api.response.CreateBatchResponse;
import com.moglix.wms.api.response.DeleteBatchResponse;
import com.moglix.wms.api.response.FileUploadResponse;
import com.moglix.wms.api.response.RollbackBatchResponse;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.task.BatchImportTask;
import com.moglix.wms.task.InventoryImportTask;

@RestController
@RequestMapping("/api/batches")
public class BatchController {
	
	Logger log = LogManager.getLogger(BatchController.class);
	Gson gson = new GsonBuilder().create();
	@Autowired
	@Qualifier("batchService")
	private IBatchService biService;
	
	@Autowired
	private BatchImportTask importTask;
	
	@Autowired
	private InventoryImportTask inventoryImportTask;

	@GetMapping("ping")
	public BaseResponse ping() {
		return new BaseResponse("WMS Controller working", true, HttpStatus.OK.value());
	}

	@PostMapping("/")
	public CreateBatchResponse create(@Valid @RequestBody CreateBatchRequest request) {
		log.info("Request received to create Batch :: " + new Gson().toJson(request.toString()));
		log.info("Request received to insert Batch with refId: " + request.getRefNo());
		return biService.createBatch(request);
	}

	@PostMapping("/delete")
	public RollbackBatchResponse delete(@Valid @RequestBody RollbackBatchRequest request) {
		log.info("Request received to remove Batch:" + request.toString());
		return biService.rollbackBatch(request);
	}

	@PostMapping("/deleteByMrn")
	public DeleteBatchResponse delete(@Valid @RequestBody DeleteBatchRequest request) {
		log.info("Request received to delete Batch: " + request.toString());
		return biService.deleteBatch(request);
	}
	
	@PostMapping("/supplierCN/cancel")
	public CancelSupplierCNResponse cancelsupplierCN(@Valid @RequestBody SupplierCNCancelRequest request) {
		log.info("Request received to delete Batch: " + request.toString());
		return biService.cancelsupplierCN(request);
	}
	
	@GetMapping("/startBatchImport/{numberOfThreads}")
	public BaseResponse startBatchImport(@PathVariable("numberOfThreads") int numberOfThreads) throws IOException {
		importTask.process(numberOfThreads); 
		return new BaseResponse("Import Task Started", true, 200);
	}
	
	@PostMapping("/checkDelete")
	public DeleteBatchResponse checkIfDeletable(@Valid @RequestBody DeleteBatchRequest request) {
		log.info("Request received to check if batch can be deleted. RefNo: " + request.getRefNo());
		return biService.checkIfBatchIsDeletable(request.getRefNo(), request.getBatchType());
	}
	
	@GetMapping("/startImportBatchTask/{numberOfThreads}/filename/{filename}")
	public BaseResponse startImportBatchTask(@PathVariable("numberOfThreads") int numberOfThreads, @PathVariable("filename") String filename) throws IOException {
		log.info("Request received to start import Batches");
		return inventoryImportTask.migrate(numberOfThreads,filename);
	}
	
		
	@PostMapping("/upload")
    public FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
		log.info("Received Request to upload file: " + file.getOriginalFilename() + " with size: " + file.getSize());
		return biService.fileUpload(file);
    }
	
	@GetMapping("/startImportBatchTaskCSV/{numberOfThreads}/filename/{filename}")
	public BatchRefResponse startImportBatchTaskCSV(@PathVariable("numberOfThreads") int numberOfThreads, @PathVariable("filename") String filename) throws IOException {
		log.info("Request received to start import Batches");
		return inventoryImportTask.migrateCSV(numberOfThreads,filename);
	}
	
	@PostMapping("/writeTemporaryCSV")
    public FileUploadResponse writeToTemporaryCSV(@Valid @RequestBody BatchRefRequest request){
		log.info("Request received to writeToTemporaryCSV"+ request.toString() );
		return inventoryImportTask.writeToTemporaryCSV(request);
    }
	
	@GetMapping("/startImportBatchTaskCSVWithoutBin/{numberOfThreads}/filename/{filename}")
	public BatchRefResponse startImportBatchTaskCSVWithoutBin(@PathVariable("numberOfThreads") int numberOfThreads, @PathVariable("filename") String filename) throws IOException {
		log.info("Request received to start import Batches");
		return inventoryImportTask.migrateCSVWithoutBin(numberOfThreads,filename);
	}
	
	@PostMapping("/writeTemporaryCSVWithoutBin")
    public void writeToTemporaryCSVWithoutBin(@Valid @RequestBody BatchRefRequest request,HttpServletResponse response){
		log.info("Request received to writeToTemporaryCSV"+ request.toString() );
		 inventoryImportTask.writeToTemporaryCSVWithoutBin(request,response);
    }
}
