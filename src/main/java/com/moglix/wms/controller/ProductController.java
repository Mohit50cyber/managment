package com.moglix.wms.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.request.CreateProductRequest;
import com.moglix.wms.api.request.GetProductByPacketAndSupplierRequest;
import com.moglix.wms.api.request.GetProductByReturnPacketAndSupplierRequest;
import com.moglix.wms.api.request.GetProductInventoryHistoryRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreateProductResponse;
import com.moglix.wms.api.response.GetProductByBarcodeResponse;
import com.moglix.wms.api.response.GetProductByPacketAndSupplierResponse;
import com.moglix.wms.api.response.GetProductByPacketResponse;
import com.moglix.wms.api.response.GetProductByReturnPacketAndSupplierResponse;
import com.moglix.wms.api.response.GetProductInventoryHistoryResponse;
import com.moglix.wms.api.response.GetProuductExpiryAndLotResponse;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.ISupplierReturnService;
import com.moglix.wms.task.ProductImportTask;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	
	Logger log = LogManager.getLogger(ProductController.class);

	@Autowired
	@Qualifier("productService")
	private IProductService productService;
	
	@Autowired
	@Qualifier("debitNoteService")
	private ISupplierReturnService supplierReturnService;
	
	@Autowired
	private ProductImportTask importTask;

	@PostMapping("/")
	public CreateProductResponse create(@Valid @RequestBody CreateProductRequest request) {
		log.info("Request received to create product: " + request.toString());
		return productService.create(request);
	}

	@GetMapping("/{barcode}")
    public GetProductByBarcodeResponse getProductByBarcode(@PathVariable("barcode") String barcode) {
        log.info("Request received for barcode : " + barcode);
        return productService.getProductByBarcode(barcode);
    }
	
	@GetMapping("/getexpiryAndLotDetails/{productMsn}")
    public GetProuductExpiryAndLotResponse getExpiryAndLotDetails(@PathVariable("productMsn") String productMsn) {
        log.info("Request received to fetch expiry and lot details for productMsn : " + productMsn);
        return productService.getExpiryAndLotDetails(productMsn);
    }
	
	@GetMapping("/packet/{emsPacketId}/returnPacket/{emsReturnId}")
	public GetProductByPacketResponse getProductsByPacketId(@PathVariable("emsPacketId") Integer emsPacketId, @PathVariable("emsReturnId") Integer emsReturnId) {
		return productService.getProductsByEMSPacketId(emsPacketId, emsReturnId);
	}
	
	@PostMapping("/getBySupplierAndPacket")
	public GetProductByPacketAndSupplierResponse getProductBySupplierAndEmsPacketId(@Valid @RequestBody GetProductByPacketAndSupplierRequest request) {
		log.info("Request received to get product details with emsPacketID: " + request.getEmsPacketId() + " and supplierId: " + request.getSupplierId());
		return productService.getProductsByEMSPacketIdAndSupplierId(request.getEmsPacketId(), request.getSupplierId());
	}
	
	@PostMapping("/getBySupplierAndReturnPacket")
	public GetProductByReturnPacketAndSupplierResponse getProductBySupplierAndEmsReturnId(@Valid @RequestBody GetProductByReturnPacketAndSupplierRequest request) {
		log.info("Request received to get product details with emsReturnId: " + request.getEmsReturnId() + " and supplierId: " + request.getSupplierId());
		return productService.getProductsByEMSReturnIdAndSupplierId(request.getEmsReturnId(), request.getSupplierId(), request.getSupplierPoId());
	}
	
	@GetMapping("/startProductImport/{numberOfThreads}")
	public BaseResponse startBatchImport(@PathVariable("numberOfThreads") int numberOfThreads) throws IOException {
		importTask.process(numberOfThreads); 
		return new BaseResponse("Product Import Task Started", true, 200);
	}
	
	@PostMapping("/inventoryHistory")
	public GetProductInventoryHistoryResponse getProductInventoryHistory(@Valid @RequestBody GetProductInventoryHistoryRequest request, Pageable page){
		log.info("Request received to get product inventory history with productMsn: " + request.getProductMsn() + " in warehouse id: " + request.getWarehouseId());
		return productService.getProductInventoryHistory(request.getProductMsn(),request.getWarehouseId(),page);
	}
}
