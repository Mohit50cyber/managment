package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.api.request.CreateProductRequest;
import com.moglix.wms.api.response.CreateProductResponse;
import com.moglix.wms.api.response.GetProductByBarcodeResponse;
import com.moglix.wms.api.response.GetProductByPacketAndSupplierResponse;
import com.moglix.wms.api.response.GetProductByPacketResponse;
import com.moglix.wms.api.response.GetProductByReturnPacketAndSupplierResponse;
import com.moglix.wms.api.response.GetProductInventoryHistoryResponse;
import com.moglix.wms.api.response.GetProuductExpiryAndLotResponse;
import com.moglix.wms.dto.ProductDTO;
import com.moglix.wms.dto.ProductInventoryHistoryDTO;
import com.moglix.wms.dto.ProductPacketResponseDTO;
import com.moglix.wms.dto.SupplierCreditNoteDetailDTO;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundItem;
import com.moglix.wms.entities.Packet;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventoryHistory;
import com.moglix.wms.entities.ReturnPacket;
import com.moglix.wms.entities.ReturnPacketItem;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.mapper.InboundMapper;
import com.moglix.wms.repository.InboundItemsRepository;
import com.moglix.wms.repository.ProductInventoryHistoryRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.IPacketService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.util.PaginationUtil;

/**
 * @author pankaj on 29/4/19
 */
@Service("productService")
public class ProductServiceImpl implements IProductService {

	@Autowired
	private InboundItemsRepository inboundItemsRepo;

	@Autowired
	@Qualifier("packetServiceImpl")
	private IPacketService packetService;

	@Autowired
	@Qualifier("batchService")
	private IBatchService batchServiceImpl;

	private Logger logger = LogManager.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductsRepository repository;
	
	@Autowired
	private ProductInventoryHistoryRepository productInventoryHistoryRepository;

	@Override
	public Product upsert(Product product) {
		repository.save(product);
		return product;
	}

	@Override
	public Product add(Product product) {
		repository.save(product);
		return product;
	}

	@Override
	public Product getById(Integer id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Iterable<Product> findAllByIdIn(Set<Integer> ids) {
		return repository.findAllById(ids);
	}

	@Override
	public Product getByProductMsnAndUom(String productMsn, String uom) {
		return repository.findByProductMsnAndUom(productMsn, uom).orElse(null);
	}

	@Override
	public GetProductByBarcodeResponse getProductByBarcode(String barcode) {

		GetProductByBarcodeResponse response = new GetProductByBarcodeResponse("Product Details Found", true,
				HttpStatus.OK.value());
		InboundItem item = inboundItemsRepo.findByBarcode(barcode).orElse(null);

		if (item == null) {
			return new GetProductByBarcodeResponse("Product Not found for this barcode", true, HttpStatus.OK.value());
		}

		ProductDTO product = InboundMapper.createProductDTOFromInbound(item.getInbound());
		product.setSerialNumber(item.getSerialNumber());
		response.setProduct(product);
		return response;
	}

	@Transactional
	@Override
	public CreateProductResponse create(CreateProductRequest request) {
		logger.info("Create product service started");
		CreateProductResponse response = new CreateProductResponse();
		Product product = repository.getUniqueByProductMsn(request.getProductMsn());
		if (product != null) {
			logger.info("Product already exist with msn: " + request.getProductMsn() + ". Updating Product");
			product.setUom(request.getUom());
			product.setProductMsn(request.getProductMsn());
			product.setProductName(request.getProductName());
			if(StringUtils.isNotBlank(request.getProductBrand())) {
				product.setProductBrand(request.getProductBrand());
			}
			product.setExpiryDateManagementEnabled(request.isExpiryDateManagementEnabled());
			product.setLotManagementEnabled(request.isLotManagementEnabled());
			if(request.isExpiryDateManagementEnabled() && request.getShelfLife() != null) {
				product.setShelfLife(request.getShelfLife());
			}
			upsert(product);
			response.setStatus(true);
			response.setMessage("Product updated with MSN: " + request.getProductMsn());
		} else {
			product = new Product();
			product.setProductMsn(request.getProductMsn());
			product.setProductName(request.getProductName());
			product.setUom(request.getUom());
			if(StringUtils.isNotBlank(request.getProductBrand())) {
				product.setProductBrand(request.getProductBrand());
			}
			product.setSerializedProduct(request.isSerializedProduct());
			product.setExpiryDateManagementEnabled(request.isExpiryDateManagementEnabled());
			product.setLotManagementEnabled(request.isLotManagementEnabled());
			if(request.isExpiryDateManagementEnabled() && request.getShelfLife() != null) {
				product.setShelfLife(request.getShelfLife());
			}
			upsert(product);
			response.setStatus(true);
			response.setMessage("Product[MSN:" + request.getProductMsn() + " created successfully]");
		}
		logger.info("Create product service ended");
		return response;
	}

	@Override
	public GetProductByPacketResponse getProductsByEMSPacketId(Integer emsPacketId, Integer emsReturnId) {
		Packet packet = packetService.findByEmsPacketId(emsPacketId).orElse(null);
		if (packet == null) {
			return new GetProductByPacketResponse("No Packets found for EMS Packet ID: " + emsPacketId, true,
					HttpStatus.OK.value());
		}
		GetProductByPacketResponse response = new GetProductByPacketResponse(
				"Successfully found products for EMS Packet ID: " + emsPacketId, true, HttpStatus.OK.value());

		List<SaleOrderAllocation> saleOrderAllocations = packet.getPacketItems().stream()
				.map(e -> e.getSaleOrderAllocation()).collect(Collectors.toList());

		Map<String, List<Inbound>> productMsnInboundMap = saleOrderAllocations.stream().collect(Collectors
				.toMap(e -> e.getSaleOrder().getProduct().getProductMsn(), e -> new ArrayList<>(Arrays.asList(e.getInboundStorage().getInbound())), (e1,e2) -> {e1.addAll(e2); return e1;}));
		
		Map<Integer, Double> items = new HashMap<>();
		for(ReturnPacket returnPackets: packet.getReturnPackets()) {
			if(Integer.compare(returnPackets.getEmsReturnId(), emsReturnId) == 0) {
				items.put(returnPackets.getEmsReturnId(), returnPackets.getTotalQuantity());
			}
		}
		
		List<ReturnPacketItem> returnPacketItems = packet.getReturnPackets().stream()
				.filter(e -> Integer.compare(emsReturnId, e.getEmsReturnId()) == 0)
				.flatMap(e -> e.getReturnPacketItems().stream()).collect(Collectors.toList());
		
		List<ProductPacketResponseDTO> products;

		for (ReturnPacketItem packetItem : returnPacketItems) {
			for(Inbound inbound : productMsnInboundMap.get(packetItem.getProductMsn())){
				ProductPacketResponseDTO productResponseDTO = InboundMapper
						.createProductPacketFromEntity(inbound,saleOrderAllocations, emsPacketId);
				//productResponseDTO.setReturnedQuantity(items.get(packetItem.getReturnPacket().getEmsReturnId()));
				productResponseDTO.setReturnedQuantity(packetItem.getQuantity());
				products = response.getProducts();
				if (products.contains(productResponseDTO)) {
					products.get(products.indexOf(productResponseDTO)).getSupplierDetails()
							.addAll(productResponseDTO.getSupplierDetails());
				} else {
					products.add(productResponseDTO);
				}
			}
			
		}
		return response;
	}

	@Override
	public GetProductByPacketAndSupplierResponse getProductsByEMSPacketIdAndSupplierId(Integer emsPacketId,
			Integer supplierId) {
		Packet packet = packetService.findByEmsPacketId(emsPacketId).orElse(null);
		if (packet == null) {
			return new GetProductByPacketAndSupplierResponse("No Packets found for EMS Packet ID: " + emsPacketId, true,
					HttpStatus.OK.value());
		}
		GetProductByPacketAndSupplierResponse response = new GetProductByPacketAndSupplierResponse(
				"Successfully found products for EMS Packet ID: " + emsPacketId, true, HttpStatus.OK.value());

		Set<SupplierCreditNoteDetailDTO> items = packet.getPacketItems().stream()
				.filter(e -> Integer.compare(e.getInboundStorage().getInbound().getSupplierId(), supplierId) == 0)
				.collect(Collectors.groupingBy(e -> InboundMapper
						.createSupplierCreditNoteDetailDTOFromInbound(e.getInboundStorage().getInbound())))
				.keySet();

		response.setCreditNoteDetails(items);

		return response;
	}

	@Override
	public GetProductByReturnPacketAndSupplierResponse getProductsByEMSReturnIdAndSupplierId(Integer emsReturnId,
			Integer supplierId, Integer supplierPoId) {
		Batch batch = batchServiceImpl.findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(emsReturnId, supplierId).orElse(null);
		if (batch == null) {
			return new GetProductByReturnPacketAndSupplierResponse("No Packets found for EMS Packet ID: " + emsReturnId,
					true, HttpStatus.OK.value());
		}
		GetProductByReturnPacketAndSupplierResponse response = new GetProductByReturnPacketAndSupplierResponse(
				"Successfully found products for EMS Packet ID: " + emsReturnId, true, HttpStatus.OK.value());

		Set<SupplierCreditNoteDetailDTO> items = batch.getInbounds().stream().filter(e -> Integer.compare(supplierPoId, e.getSupplierPoId()) == 0)
				.collect(Collectors.groupingBy(e -> InboundMapper.createSupplierCreditNoteDetailDTOFromInbound(e)))
				.keySet();

		response.setCreditNoteDetails(items);

		return response;
	}
	
	@Override
	@Transactional
	public GetProductInventoryHistoryResponse getProductInventoryHistory(String productMsn, Integer warehouseId, Pageable page) {
		
		Page<ProductInventoryHistory> inventoryHistory = productInventoryHistoryRepository.findByProductMsnAndWarehouseId(productMsn, warehouseId, page);
		
		if (inventoryHistory.getContent().isEmpty()) {
			logger.info("No inventory history found for productMsn: " +  productMsn + " in warehouseId: " + warehouseId);
			return new GetProductInventoryHistoryResponse("No inventory history found for productMsn: " +  productMsn + " in warehouseId: " + warehouseId + ". Consider changing the selected warehouse", true,
					HttpStatus.OK.value());
		} else {
			GetProductInventoryHistoryResponse response = (GetProductInventoryHistoryResponse) PaginationUtil.setPaginationParams(inventoryHistory,
					new GetProductInventoryHistoryResponse("Successfully retreived history for product: " + productMsn + " in warehouseId: " + warehouseId, true, HttpStatus.OK.value()));
			for (ProductInventoryHistory history : inventoryHistory.getContent()) {
				response.getProductInventoryhistory().add(new ProductInventoryHistoryDTO(history));
			}
			return response;
		}
	}

	@Override
	@Transactional
	public GetProuductExpiryAndLotResponse getExpiryAndLotDetails(String productMsn) {

		Product product = repository.getUniqueByProductMsn(productMsn);

		if(product != null) {
			ProductDTO detail = new ProductDTO(product);
			GetProuductExpiryAndLotResponse response = new GetProuductExpiryAndLotResponse("Found Details for productMsn: " + productMsn, true, HttpStatus.OK.value());
			response.setProductDetail(detail);
			
			return response;
		}else {
			return new GetProuductExpiryAndLotResponse("Found No Details for productMsn: " + productMsn, true, HttpStatus.OK.value());
		}
	}
}
