package com.moglix.wms.service.impl;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.api.request.InventoryUpdateRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.constants.BlockedProductInventoryStatus;
import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.constants.PublishSystemType;
import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.entities.BlockedProductInventory;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.ProductInventoryHistory;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.entities.SaleOrderAllocationHistory;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.kafka.producer.KafkaEmsSalesOpsUpdateProducer;
import com.moglix.wms.repository.BlockedProductInventoryRepository;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.ProductInventoryHistoryRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.SaleOrderAllocationRepository;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.ISaleOrderService;
import com.moglix.wms.service.IWarehouseService;
import com.moglix.wms.util.NumberUtil;

/**
 * @author pankaj on 6/5/19
 */
@Service("inventoryService")
public class InventoryServiceImpl implements IInventoryService {

	Logger logger = LogManager.getLogger(InventoryServiceImpl.class);

	@Autowired
	private IProductService productService;

	@Autowired
	private IProductInventoryService productInventoryService;

	@Autowired
	private IWarehouseService warehouseService;

	@Autowired
	private IInboundStorageService inboundStorageService;

	@Autowired
	private ISaleOrderService saleOrderService;

	@Autowired
	private SaleOrderAllocationRepository saleOrderAllocationRepository;
	
	@Autowired
	private ProductInventoryHistoryRepository productInventoryHistoryRepo;
	
//	@Autowired
//	private ProductInventoryConfigRepository productInventoryConfigRepository;
	
	@Autowired
	private InboundStorageRepository inboundStorageRepo;
	
	@Autowired
	private BlockedProductInventoryRepository blockedProductInventoryRepo;
	
	@Autowired
	private ProductsRepository prodRepo;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	PlatformTransactionManager transactionManager;
	
//	@Autowired
//	private MailUtil mailUtil;
	
//	private final int numberOfRetries = 3;
	
	@Autowired
	KafkaEmsSalesOpsUpdateProducer kafkaPublisherInventoryUpdate;
	
	
	@Override
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public BaseResponse addInventory(Integer warehouseId, Integer productId, Double purchasePrice, Double qty) {
		String uuid = UUID.randomUUID().toString();
		logger.info("Request Id: " + uuid);
		logger.info("Adding inventory service started for request_id: " + uuid);
		BaseResponse response = new BaseResponse();
		
		logger.trace("Getting product for productId: " + productId + " : " + uuid);
		Product product = productService.getById(productId);
		if (product == null) {
			response.setMessage("Product does not exist");
			logger.info("Product does not exist");
			return response;
		}
		
		logger.trace("Product found for productId: " + productId + " : " + uuid);

		logger.trace("Getting product inventory for productId: " + productId + " and warehouse " + warehouseId + " : " + uuid);
		ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(warehouseId,
				productId);
		if (productInventory == null) { // create new product_inventory
			logger.trace("Product Inventory not found. Creating new for productId: " + productId + " and warehouse " + warehouseId + " : " + uuid);
			Warehouse warehouse = warehouseService.getById(warehouseId);

			productInventory = new ProductInventory();
			productInventory.setProduct(product);
			productInventory.setWarehouse(warehouse);
			logger.debug("Allocated Quantity: " + 0d + " : " + uuid);
			productInventory.setAllocatedQuantity(0d);
			
			logger.debug("Available Quantity: " + NumberUtil.round4(qty) + " : " + uuid);
			productInventory.setAvailableQuantity(NumberUtil.round4(qty));
			
			logger.debug("Current Quantity: " + NumberUtil.round4(qty) + " : " + uuid);
			productInventory.setCurrentQuantity(NumberUtil.round4(qty));
			
			logger.debug("Total Quantity: " + NumberUtil.round4(qty) + " : " + uuid);
			productInventory.setTotalQuantity(NumberUtil.round4(qty));
			
			productInventory.setAveragePrice(purchasePrice != null ? NumberUtil.round4(purchasePrice) : 0d);
			
			logger.info("Inserting data in product Inventory for productId: "  + productId + " and warehouse " + warehouseId + " : " + uuid);
			productInventoryService.upsert(productInventory);
		} else { // update product_inventory
			logger.trace("Product Inventory already exists for productId: "  + productId + " and warehouse " + warehouseId + " : " + uuid);
			
			Double availableQty = productInventory.getAvailableQuantity();
			logger.debug("Current available Quantity: " + availableQty + " : " + uuid);
			
			Double currentQty = productInventory.getCurrentQuantity();
			logger.debug("Current current Quantity: " + currentQty + " : " + uuid);
			
			Double totalQty = productInventory.getTotalQuantity();
			logger.debug("Current total Quantity: " + totalQty + " : " + uuid);
			
			if (productInventory.getAveragePrice() == null) {
				productInventory.setAveragePrice(0.0d);
			}
			double averagePrice = (productInventory.getAveragePrice() + (purchasePrice != null ? purchasePrice : 0))
					/ 2;

			logger.debug("New Available Quantity: " + NumberUtil.round4(qty + availableQty) + " : " + uuid);
			productInventory.setAvailableQuantity(NumberUtil.round4(qty + availableQty));
			
			logger.debug("New Current Quantity: " + NumberUtil.round4(qty + currentQty) + " : " + uuid);
			productInventory.setCurrentQuantity(NumberUtil.round4(qty + currentQty));
			
			logger.debug("New Total Quantity: " + NumberUtil.round4(qty + totalQty) + " : " + uuid);
			productInventory.setTotalQuantity(NumberUtil.round4(qty + totalQty));
			
			productInventory.setAverageAge(productInventory.getAverageAge() / 2);
			productInventory.setAveragePrice(NumberUtil.round4(averagePrice));
			
			logger.info("Updating data in product Inventory for productId: "  + productId + " and warehouse " + warehouseId + " : " + uuid);
			productInventoryService.upsert(productInventory);
		}


		Double availableQty = product.getAvailableQuantity();
		logger.debug("Current Available Quantity in product: " + availableQty + " : " + uuid);

		Double currentQty = product.getCurrentQuantity();
		logger.debug("Current Current Quantity in product: " + currentQty + " : " + uuid);

		Double totalQty = product.getTotalQuantity();
		logger.debug("Current Total Quantity in product: " + totalQty + " : " + uuid);

		
		product.setAvailableQuantity(NumberUtil.round4(qty + availableQty));
		logger.debug("New Available Quantity in product: " + NumberUtil.round4(qty + availableQty) + " : " + uuid);

		product.setCurrentQuantity(NumberUtil.round4(qty + currentQty));
		logger.debug("New Current Quantity in product: " + NumberUtil.round4(qty + currentQty) + " : " + uuid);

		product.setTotalQuantity(NumberUtil.round4(qty + totalQty));
		logger.debug("New Total Quantity in product: " + NumberUtil.round4(qty + totalQty) + " : " + uuid);

		logger.trace("Updating product details for productId: " + productId + " : " + uuid);
		productService.add(product); // update product inventory

		response.setMessage("Inventory Added");
		response.setStatus(true);

		logger.info("Adding inventory service ended" + " : " + uuid);
		return response;
	}
	
	public boolean checkAllInventoryAvailable(Integer warehouseId, Integer productId, Double qty) {
		logger.info("Checking " + qty + " allocated quantity for productId : " + productId + " and warehouseId: " + warehouseId );
		Product product = productService.getById(productId);
		ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(warehouseId,
				productId);
		if(productInventory.getAllocatedQuantity()<qty || productInventory.getCurrentQuantity()<qty
				|| product.getAllocatedQuantity()<qty || product.getCurrentQuantity()<qty) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public BaseResponse deductAllocatedInventory(Integer warehouseId, Integer productId, Double qty) {
		String uuid = UUID.randomUUID().toString();
		
		logger.info("deducting " + qty + " allocated quantity for productId : " + productId + " and warehouseId: " + warehouseId + " : " + uuid);
		
		BaseResponse response = new BaseResponse();
		Product product = productService.getById(productId);
		if (product == null) {
			response.setMessage("Product does not exist");
			logger.info("Product does not exist");
			return response;
		}

		ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(warehouseId,
				productId);
		if (productInventory == null) { // create new product_inventory
			response.setMessage("Product Inventory does not exist");
			logger.info("Product Inventory does not exist");
			return response;
		}
		
		// update product_inventory
		Double allocatedQty = productInventory.getAllocatedQuantity();
		logger.debug("Current Product Inventory Allocated Qty: " + allocatedQty + " : " + uuid);
		
		Double currentQty = productInventory.getCurrentQuantity();
		logger.debug("Current Product Inventory Current Qty: " + currentQty + " : " + uuid);

		productInventory.setAllocatedQuantity(NumberUtil.round4(allocatedQty - qty));
		logger.debug("Updated Product Inventory Allocated Qty: " + NumberUtil.round4(allocatedQty - qty) + " : " + uuid);

		productInventory.setCurrentQuantity(NumberUtil.round4(currentQty - qty));
		logger.debug("Updated Product Inventory Current Qty: " + NumberUtil.round4(currentQty - qty) + " : " + uuid);

		logger.trace("Updating productInventory for productId: " + productId + " : " + uuid);
		productInventoryService.upsert(productInventory); //updating product inventory

		allocatedQty = product.getAllocatedQuantity();
		logger.debug("Current Product Allocated Quantity: " + allocatedQty + " : " + uuid);
		
		currentQty = product.getCurrentQuantity();
		logger.debug("Current Product Current Quantity: " + currentQty + " : " + uuid);
		
		logger.debug("Updating product allocated quantity: " + NumberUtil.round4(allocatedQty - qty) + " : " + uuid);
		product.setAllocatedQuantity(NumberUtil.round4(allocatedQty - qty));
		
		logger.debug("Updating product current quantity: " + NumberUtil.round4(currentQty - qty) + " : " + uuid);
		product.setCurrentQuantity(NumberUtil.round4(currentQty - qty));

		logger.trace("Updating product for productId: " + productId + " : " + uuid);
		productService.upsert(product); // update product inventory
		
		response.setMessage("Inventory Deducted");
		response.setStatus(true);

		logger.info("Deduct inventory service ended" + " : " + uuid);
		return response;
	}

	@Override
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public BaseResponse deductAvailableInventory(Integer warehouseId, Integer productId, Double qty) {
	
		String uuid = UUID.randomUUID().toString();
		
		logger.info("Deducting " + qty + " from available quantity for productId : " + productId + " and warehouseId: " + warehouseId + " : " + uuid);
		
		BaseResponse response = new BaseResponse();
		
		Product product = productService.getById(productId);
		if (product == null) {
			response.setMessage("Product does not exist");
			logger.info("Product does not exist");
			return response;
		}

		ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(warehouseId, productId);
		if (productInventory == null) { // create new product_inventory
			response.setMessage("Product Inventory does not exist");
			logger.info("Product Inventory does not exist");
			return response;
		}

		// Update inventory in product_inventory
		Double availableQuantity = productInventory.getAvailableQuantity();
		logger.info("Current Product Inventory Available Qty: " + availableQuantity + " : " + uuid);

		Double currentQty = productInventory.getCurrentQuantity();
		logger.info("Current Product Inventory Current Qty: " + currentQty + " : " + uuid);
		
		if(NumberUtil.round4(availableQuantity - qty) < 0) {
			productInventory.setAvailableQuantity(0.0);	
			logger.error("Available quantity should not be negative in Product Inventory :: ProductMsn ::[" +  product.getProductMsn() +"] :: WarehouseId :: ["+ warehouseId +"]");
		}
		else {
			productInventory.setAvailableQuantity(NumberUtil.round4(availableQuantity - qty));
		}
		
		logger.info("Updating Product Inventory Available Qty :: [" + productInventory.getAvailableQuantity() + "] : " + uuid);
		
		if(NumberUtil.round4(currentQty - qty) < 0) {
			productInventory.setCurrentQuantity(0.0);	
			logger.error("Current quantity should not be negative in Product Inventory :: ProductMsn ::[" +  product.getProductMsn() +"] :: WarehouseId :: ["+ warehouseId +"]");
		}
		else {
			productInventory.setCurrentQuantity(NumberUtil.round4(currentQty - qty));
		}
		
		logger.info("Updating Product Inventory Current Qty :: [" + productInventory.getCurrentQuantity() + "] : " + uuid);

		logger.trace("Updating productInventory for productId :: [" + productId + "] : " + uuid);
		productInventoryService.upsert(productInventory);
		
		// Update inventory in product
		availableQuantity = product.getAvailableQuantity();
		logger.info("Current Product Available Quantity: " + availableQuantity + " : " + uuid);

		currentQty = product.getCurrentQuantity();
		logger.info("Current Product Current Quantity: " + currentQty + " : " + uuid);
		
		if(NumberUtil.round4(availableQuantity - qty) < 0) {
			product.setAvailableQuantity(0.0);	
			logger.error("Available quantity should not be negative in Product :: ProductMsn ::[" +  product.getProductMsn() +"] :: WarehouseId :: ["+ warehouseId +"]");
		}
		else {
			product.setAvailableQuantity(NumberUtil.round4(availableQuantity - qty));
		}
		
		logger.info("Updating Product Available Qty :: [" + product.getAvailableQuantity() + "] : " + uuid);
		
		if(NumberUtil.round4(currentQty - qty) < 0) {
			product.setCurrentQuantity(0.0);	
			logger.error("Current quantity should not be negative in Product Inventory :: ProductMsn ::[" +  product.getProductMsn() +"] :: WarehouseId :: ["+ warehouseId +"]");
		}
		else {
			product.setCurrentQuantity(NumberUtil.round4(currentQty - qty));
		}
		
		logger.info("Updating Product Inventory Current Qty :: [" + product.getCurrentQuantity() + "] : " + uuid);

		logger.trace("Updating product for productId :: [" + productId + "] : " + uuid);
		productService.upsert(product); 

		response.setMessage("Inventory Deducted");
		response.setStatus(true);

		logger.info("Deduct inventory service ended" + " : " + uuid);
		return response;
	}
	
	@Override
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public boolean allocateInventoryForSaleOrder(Integer saleOrderId) throws InterruptedException {
		
		logger.info("Allocation Service Started for saleOrder: " + saleOrderId);
		
		if (saleOrderId == null) {
			logger.info("SaleOrder id is null.");
		}
		else {
			SaleOrder saleOrder = saleOrderService.getById(saleOrderId);

			if (saleOrder == null) {
				logger.info("Invalid SaleOrder!!! :: [" + saleOrderId +"]");
			} 
			else{
				//Double prevAllocateQuantity = saleOrder.getAllocatedQuantity();
				try {
					allocateInventory(saleOrder);
				} catch (Exception e) {
					logger.error("Error coming in allocation process for Saleorder :: [" + saleOrderId +"]");
					e.printStackTrace();
				}
				//updateEMSandSalesOps(saleOrder, prevAllocateQuantity, true);
				boolean isInventory = true;
				if (saleOrder.getAllocatedQuantity() == 0) {
					isInventory = false;
				}
				kafkaPublisherInventoryUpdate.sendRequest(new InventoryUpdateRequest(saleOrder.getItemRef(), saleOrder.getAllocatedQuantity(), isInventory, PublishSystemType.WMS));
			}
		}
		logger.info("Allocation Service Ended");
		return true;
	}

	@Override
	public boolean allocateInventoryForProductId(Integer productId) {

		/*logger.trace("Allocating inventory to open Orders for productID: " + productId);
		
		List<SaleOrder> saleOrders = saleOrderService.findOpenSaleOrderForProduct(productId);
		logger.trace("Found " + saleOrders.size() + " orders for productId: " + productId);
		if (!CollectionUtils.isEmpty(saleOrders)) {
			for (SaleOrder so : saleOrders) {
				Double prevAllocateQuantity = so.getAllocatedQuantity();
				logger.debug("Previous allocated quantity for saleOrder: " + so.getEmsOrderItemId() + " is: " + prevAllocateQuantity);
				DefaultTransactionDefinition def = new DefaultTransactionDefinition();
				def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
				TransactionStatus transactionStatus = transactionManager.getTransaction(def);
				try {
					allocateInventoryAndNotifySystems(so,prevAllocateQuantity,false);
					transactionManager.commit(transactionStatus);
				}catch(Exception e) {
					logger.error("Error occured while allocating quantities to saleOrder: " + so.getEmsOrderItemId() + ". Transactions Rolled Back", e);			
					try {
						transactionManager.rollback(transactionStatus);
					} catch (UnexpectedRollbackException e1) {
						logger.warn("Exception in rollback. Transaction Silently rolled back", e1);
					} 
				}
			}
		} else {
			logger.info("No Open Order found for this productID: [" + productId + "]");
		}
	
		return true;*/
		return false;
	}

//	private void allocateInventoryAndNotifySystems(SaleOrder saleOrder, Double prevAllocateQuantity, boolean isInventory) throws Exception {
//
//		String uuid = UUID.randomUUID().toString();
//
//		logger.info("Allocating inventory for sale order id: " + saleOrder.getEmsOrderItemId() + "and warehouseId" + saleOrder.getWarehouse().getId() + " : " + uuid);
//		ProductInventory productInventory = productInventoryService
//				.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
//		
//		logger.info("Getting Blocked quantity for product msn: " + saleOrder.getProduct().getProductMsn()
//				+ " and warehouse: " + saleOrder.getWarehouse().getId());
//		
//		List<BlockedProductInventory> blockedProductInventoryList = blockedProductInventoryRepo.findByWarehouseIdAndProductMsn(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getProductMsn());
//		
//		ProductInventoryConfig inventoryConfig = productInventoryConfigRepository.findByProductMsnAndWarehouseId(saleOrder.getProduct().getProductMsn(), saleOrder.getWarehouse().getId());
//		
//		double blockedQuantity = 0.0d;
//		Double expiredQty = 0.0d;
//
//		if(blockedProductInventoryList != null && blockedProductInventoryList.size()>0) {
//			logger.info("Blockedproductinventorylist size found : "+blockedProductInventoryList.size());
//			blockedQuantity = blockedProductInventoryRepo.findtotalblockedquantity(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getProductMsn());
//		}else {
//			logger.info("Blockedproductinventory found to be null or 0 ");
//		}
//		
//		if(saleOrder.getProduct().getExpiryDateManagementEnabled() != null && saleOrder.getProduct().getExpiryDateManagementEnabled()) {
//			expiredQty = inboundStorageRepo.getTotalExpiredInventoryByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
//			expiredQty = expiredQty != null ? expiredQty: 0.0d;
//		}
//		
//		if (!checkIfInventoryAvailable(productInventory, blockedQuantity, expiredQty, saleOrder,inventoryConfig)) {
//			logger.info("Inventory not available for sale order id: " + saleOrder.getId() + " : " + uuid);
//		} else if (saleOrder.getAllocatedQuantity()
//				.equals(saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity())) {
//			logger.info("Ordered quantity has been already allocated for sale order id: " + saleOrder.getId() + " : " + uuid);
//		} else if(saleOrder.getOrderedQuantity() <= saleOrder.getPackedQuantity()){
//			logger.info("Invalid Quantity. Ordered Quantity less than packed quantity for emsOrderItemId: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
//		}else {
//			logger.trace("Product Inventory found with id: " + productInventory.getId() + " : " + uuid);
//			
//			logger.debug("saleOrder orderedQuantity: " + saleOrder.getOrderedQuantity() + " saleOrder Packed Quantity: "
//					+ saleOrder.getPackedQuantity() + " saleOrder pre-allocated quantity: "
//					+ saleOrder.getAllocatedQuantity() + " for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
//			Double allocatedQty = 0.0d;
//			
//			if(saleOrder.getBulkInvoiceId() == null) {
//				if(inventoryConfig != null
//						&& !inventoryConfig.getPlantProductInventoryConfigMappings()
//						.stream().map(e -> e.getPlant().getId()).collect(Collectors.toList())
//						.contains(saleOrder.getPlant().getId())) {
//					allocatedQty = Math.min(productInventory.getAvailableQuantity() - expiredQty - blockedQuantity - inventoryConfig.getMaximumQuantity(), (saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity()- saleOrder.getAllocatedQuantity()));
//				}else {
//					allocatedQty = Math.min(productInventory.getAvailableQuantity() - expiredQty - blockedQuantity, (saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity()- saleOrder.getAllocatedQuantity()));
//				}
//			}else {
//				allocatedQty = Math.min(blockedQuantity - expiredQty, (saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity()- saleOrder.getAllocatedQuantity()));
//			}
//
//			List<InboundStorage> inboundStorages;
//			
//			if(saleOrder.getProduct().getExpiryDateManagementEnabled() != null && saleOrder.getProduct().getExpiryDateManagementEnabled()) {
//				inboundStorages = inboundStorageRepo.findByProductAndWarehouseForAvailableBasedOnExpiry(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
//			}else {
//				inboundStorages = inboundStorageRepo.findByProductAndWarehouseForAvailable(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
//			}
//			
//			logger.debug("Quantity to be allocated to saleOrder " + saleOrder.getEmsOrderItemId() + ": " + allocatedQty + " : " + uuid);
//			
//			logger.trace("Found " + inboundStorages.size() + "Inbound Storages" + " : " + uuid);
//			SaleOrderAllocation allocation;
//			SaleOrderAllocationHistory allocationHistory;
//			logger.trace("Initialised currentAllocation with value: " + allocatedQty + " : " + uuid);
//			Double currentAllocation = allocatedQty;
//			Double inboundStorageAvailableQty;
//			for (InboundStorage inboundStorage : inboundStorages) {
//				logger.trace("Inside inbound storages Loop" + " : " + uuid);
//				
//				inboundStorageAvailableQty = inboundStorage.getAvailableQuantity();
//				logger.debug("Quantity available in inboundStorage: " + inboundStorage.getId() + " : "+ inboundStorageAvailableQty + " : " + uuid);
//				
//				allocation = new SaleOrderAllocation();
//				allocation.setInboundStorage(inboundStorage);
//				allocation.setSaleOrder(saleOrder);
//				logger.debug("setting Available and allocated quantity for sale order allocation to: " + (inboundStorageAvailableQty >= currentAllocation ? currentAllocation
//						: inboundStorageAvailableQty) + " : " + uuid);
//				allocation.setAllocatedQuantity(inboundStorageAvailableQty >= currentAllocation ? currentAllocation
//						: inboundStorageAvailableQty);
//				allocation.setAvailableQuantity(allocation.getAllocatedQuantity());
//				
//				saleOrderService.upsertAllocation(allocation);
//
//				allocationHistory = new SaleOrderAllocationHistory();
//				allocationHistory.setAction("Allocated");
//				allocationHistory.setQuantity(allocation.getAllocatedQuantity());
//				allocationHistory.setSaleOrder(saleOrder);
//				saleOrderService.upsertAllocationHistory(allocationHistory);
//
//				logger.debug("Setting new storage available quantity for storageID: " + inboundStorage.getId() + " to : " + 
//						NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);
//				inboundStorage.setAvailableQuantity(
//						NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()));
//				
//				logger.debug("Setting new storage allocated quantity for storageID: " + inboundStorage.getId() + " to : " + 
//						NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()) + " : " + uuid);
//				inboundStorage.setAllocatedQuantity(
//						NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()));
//				
//				logger.trace("Updating inbound storage: " + inboundStorage.getId() + " : " + uuid);
//				inboundStorageService.upsert(inboundStorage);
//
//				currentAllocation = NumberUtil.round4(currentAllocation - allocation.getAllocatedQuantity());
//				logger.debug("CurrentAllocation value at end of loop: " + currentAllocation + " : " + uuid);
//				
//				if (currentAllocation == 0) {
//					logger.trace("Allocation Completed for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
//					break;
//				}
//			}
//
//			logger.debug("Setting saleOrder allocated quantity for saleOrder: " + saleOrder.getEmsOrderItemId() + " : "
//					+ saleOrder.getAllocatedQuantity() + allocatedQty + " : " + uuid);
//			saleOrder.setAllocatedQuantity(saleOrder.getAllocatedQuantity() + allocatedQty);
//			
//			logger.trace("Updating Sale Order Info for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
//			saleOrderService.upsert(saleOrder);
//
//
//			logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Allocated Quantity: " + NumberUtil.round4(productInventory.getAllocatedQuantity() + allocatedQty) + " : " + uuid);
//			productInventory
//					.setAllocatedQuantity(NumberUtil.round4(productInventory.getAllocatedQuantity() + allocatedQty));
//			
//			logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Available Quantity: " + NumberUtil.round4(productInventory.getAvailableQuantity() - allocatedQty) + " : " + uuid);
//			productInventory
//					.setAvailableQuantity(NumberUtil.round4(productInventory.getAvailableQuantity() - allocatedQty));
//			
//			logger.trace("Updating product inventory with productinventory id: "  + productInventory.getId() +  " : " + uuid);
//			productInventoryService.upsert(productInventory);
//			
//			if (inventoryConfig != null && productInventory
//					.getAvailableQuantity() <= inventoryConfig.getMinimumQuantity()) {
//				// Handle available quantity < configured minimum quantity
//				
//				try {
//					String mailContent = Constants.getInventoryEmailContent(
//							productInventory.getProduct().getProductMsn(),
//							productInventory.getProduct().getProductName(), productInventory.getWarehouse().getName());
//					mailUtil.sendMail(mailContent, Constants.INVENTORY_MAIL_SUBJECT,
//							productInventory.getWarehouse().getId());
//				} catch (Exception e) {
//					logger.error("Error Occured in sending mail for VMI inventory for product: "
//							+ productInventory.getProduct().getProductMsn() + " and warehouse: "
//							+ productInventory.getWarehouse().getName(), e);
//				}
//			}
//
//			
//			Product product = productInventory.getProduct();
//			
//			logger.debug("Product Id: " + product.getId()+ " Updated Allocated Quantity: " + NumberUtil.round4(product.getAllocatedQuantity() + allocatedQty) + " : " + uuid);
//			product.setAllocatedQuantity(NumberUtil.round4(product.getAllocatedQuantity() + allocatedQty));
//			
//			logger.debug("Product Id: " + product.getId()+ " Updated Available Quantity: " + NumberUtil.round4(product.getAvailableQuantity() - allocatedQty) + " : " + uuid);
//			product.setAvailableQuantity(NumberUtil.round4(product.getAvailableQuantity() - allocatedQty));
//			
//			logger.trace("Updating Product Entry for product: " + product.getId() + " : " + uuid);
//			productService.upsert(product);
//
//			BlockedProductInventory blockedProductInventory = blockedProductInventoryRepo.findByWarehouseIdAndProductMsnAndUniqueID(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getProductMsn(),saleOrder.getUniqueblockid());	
//			
//			if (blockedProductInventory != null && !StringUtils.isEmpty(saleOrder.getBulkInvoiceId())) {
//				logger.debug("Setting new Blocked Inventory for inventoryId: " + blockedProductInventory.getId()
//						+ " Available Quantity: "
//						+ NumberUtil.round4(blockedProductInventory.getBlockedQuantity() - allocatedQty) + " : "
//						+ uuid);
//				blockedProductInventory.setBlockedQuantity(
//						NumberUtil.round4(blockedProductInventory.getBlockedQuantity() - allocatedQty));
//				blockedProductInventoryRepo.save(blockedProductInventory);
//			}
//			
//			logger.info(
//					"Inventory allocated to sale order id: " + saleOrder.getEmsOrderItemId() + ", allocatedQty: " + allocatedQty + " : " + uuid);
//		}
//		
//		logger.info("Notifying EMS and salesOps for SaleOrder: " + saleOrder.getEmsOrderItemId());
//		
//
//		logger.info("Calling EMS API to update Packable quantity for orderId: " + saleOrder.getEmsOrderId() + "with allocatedQuantity:" + saleOrder.getAllocatedQuantity());
//		RestTemplate restTemplate = new RestTemplate();
//		EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(saleOrder.getEmsOrderItemId(),
//				saleOrder.getAllocatedQuantity(),"WMS");
//		
//		ResponseEntity<BaseResponse> emsResponse = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API,
//				emsRequest, BaseResponse.class);
//		if (emsResponse.getBody().getStatus()) {
//			// Call sales ops API
//			
//			if(saleOrder.getAllocatedQuantity() == 0) {
//				isInventory = false;
//			}
//			
//			SalesOpsDemandRequest salesOpsRequest = new SalesOpsDemandRequest(saleOrder.getItemRef(),
//					saleOrder.getAllocatedQuantity(), isInventory);
//
//			logger.info("Calling Sales Ops API to update demands quantity with request: " + salesOpsRequest);
//
//			ResponseEntity<BaseResponse> salesOpsResponse = null;
//			HttpHeaders headers = new HttpHeaders();
//
//			headers.add("Authorization",
//					Constants.SALES_OPS_AUTH_TOKEN);
//			try {
//				salesOpsResponse = restTemplate.exchange(Constants.SALES_OPS_DEMAND_API, HttpMethod.POST,
//						new HttpEntity<SalesOpsDemandRequest>(salesOpsRequest, headers), BaseResponse.class);
//
//			} catch (Exception e) {
//				logger.error("Error occured in updating sales Ops API:", e);
//			}
//
//			if (salesOpsResponse == null || !salesOpsResponse.getBody().getStatus()) {
//				logger.warn("Unusual response from sales Ops API." + salesOpsResponse);
//				EMSPackableQuantityRequest emsRollbackRequest = new EMSPackableQuantityRequest(saleOrder.getEmsOrderItemId(),
//						prevAllocateQuantity,"WMS");
//				restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRollbackRequest, BaseResponse.class);
//				throw new WMSException("Cannot update through sales ops API. Rolling back stuff");
//			}
//			} else {
//				throw new WMSException("Cannot update through EMS API. Rolling back stuff");
//			}
//		
//	}
		
//	private void updateEMSandSalesOps(SaleOrder so, Double prevAllocateQuantity, Boolean isInventory) throws InterruptedException {
//		HttpEntity<BaseResponse> emsResponse;
//		
//		int retryCount = 0;
//		boolean isSuccess = false;
//		do {
//			logger.info("Sleeping for " + retryCount + " Seconds");
//			Thread.sleep(retryCount * 1000);
//			
//			logger.info("Calling EMS API to update Packable quantity for orderId: " + so.getEmsOrderId() + "with allocatedQuantity:" + so.getAllocatedQuantity());
//			RestTemplate restTemplate = new RestTemplate();
//			EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(so.getEmsOrderItemId(),
//					so.getAllocatedQuantity(), "WMS");
//			
//			emsResponse = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API,
//					emsRequest, BaseResponse.class);
//			isSuccess  = emsResponse.getBody().getStatus();
//
//			if (isSuccess) {
//				// Call sales ops API
//				//logger.info("Sleeping for 3 second before calling Sales Ops API");
//				//Thread.sleep(3000);
//				
//				if(so.getAllocatedQuantity() == 0) {
//					isInventory = false;
//				}
//				
//				SalesOpsDemandRequest salesOpsRequest = new SalesOpsDemandRequest(so.getItemRef(),
//						so.getAllocatedQuantity(), isInventory);
//
//				logger.info("Calling Sales Ops API to update demands quantity with request: " + salesOpsRequest);
//
//				ResponseEntity<BaseResponse> salesOpsResponse = null;
//				HttpHeaders headers = new HttpHeaders();
//
//				headers.add("Authorization",
//						Constants.SALES_OPS_AUTH_TOKEN);
//				try {
//					salesOpsResponse = restTemplate.exchange(Constants.SALES_OPS_DEMAND_API, HttpMethod.POST,
//							new HttpEntity<SalesOpsDemandRequest>(salesOpsRequest, headers), BaseResponse.class);
//
//				} catch (Exception e) {
//					logger.error("Error occured in updating sales Ops API:", e);
//				}
//
//				if (salesOpsResponse == null || !salesOpsResponse.getBody().getStatus()) {
//					logger.warn("Unusual response from sales Ops API." + salesOpsResponse);
//					EMSPackableQuantityRequest emsRollbackRequest = new EMSPackableQuantityRequest(so.getEmsOrderItemId(),
//							prevAllocateQuantity , "WMS");
//					restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRollbackRequest, BaseResponse.class);
//					throw new WMSException("Cannot update through sales ops API. Rolling back stuff");
//				}
//			}else {
//				logger.info("Request failed tp update packable quantity in EMS. Will Retry after: " + (retryCount + 1) + " seconds");
//			}
//			retryCount++;
//		}while(!isSuccess && retryCount < numberOfRetries);
//		
//		if(!isSuccess) {
//			throw new WMSException("Cannot update through EMS API. Rolling back stuff");
//		}
//	}

	@Transactional
	@Override
	public void allocateInventory(SaleOrder saleOrder) {
		
		String uuid = UUID.randomUUID().toString();

		BlockedProductInventory blockedProductInventory = blockedProductInventoryRepo.findByWarehouseIdAndProductMsnAndUniqueID(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getProductMsn(),saleOrder.getUniqueblockid());	

		if(blockedProductInventory == null) {
			logger.info("Blocked Inventory not found for productMSN :: ["+ saleOrder.getProduct().getProductMsn() + "] against BulkInvoiceId :: [" + saleOrder.getBulkInvoiceId() + "] in warehouse :: [" + saleOrder.getWarehouse().getId() + "].");
		}
		else {
			
			logger.info("Allocating inventory for sale order id :: [" + saleOrder.getId() + "] and warehouseId :: [" + saleOrder.getWarehouse().getId() + "]");
			
			ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
	
			Double blockedQuantity = 0.0d;
			blockedQuantity = blockedProductInventory.getBlockedQuantity();
			logger.info("Blocked inventory :: " + blockedQuantity);
			
			if(!(productInventory.getAvailableQuantity()>= saleOrder.getOrderedQuantity())){
				logger.info("Inventory you are trying to allocate for productMSN :: ["+ saleOrder.getProduct().getProductMsn() + "]  is greater then the inventory actual present in warehouse :: [" + saleOrder.getWarehouse().getId() + "].");
			}
			else if (saleOrder.getOrderedQuantity() < saleOrder.getPackedQuantity()) {
				logger.info("Invalid Quantity. Ordered Quantity less than packed quantity for emsOrderItemId: " + saleOrder.getEmsOrderItemId());
			}
			
			else if (saleOrder.getOrderedQuantity() > blockedQuantity) {
				logger.info("Ordered Quantity is more than blocked quantity for emsOrderItemId: " + saleOrder.getEmsOrderItemId());
			}
						
			else {
				logger.debug("saleOrder orderedQuantity: " + saleOrder.getOrderedQuantity() + " saleOrder Packed Quantity: " + saleOrder.getPackedQuantity() 
						+ " saleOrder pre-allocated quantity: " + saleOrder.getAllocatedQuantity() + " for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
				
				Double allocatedQty = 0.0d;
				
				allocatedQty = saleOrder.getOrderedQuantity();
				
				logger.debug("Quantity to be allocated to saleOrder :: [" + saleOrder.getEmsOrderItemId() + "] : [" + allocatedQty + " qty] : " + uuid);
				
				List<InboundStorage> inboundStorages;
				
				inboundStorages = inboundStorageRepo.findByProductAndWarehouseForAvailable(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
				logger.trace("Found " + inboundStorages.size() + "Inbound Storages" + " : " + uuid);
				
				//TODO: Declare this locally where it is getting initialized
				SaleOrderAllocation allocation;
				SaleOrderAllocationHistory allocationHistory;
				Double currentAllocation = allocatedQty;
				logger.trace("Initialised currentAllocation with value: " + allocatedQty + " : " + uuid);
				
				Double inboundStorageAvailableQty;
				for (InboundStorage inboundStorage : inboundStorages) {
					logger.trace("Inside inbound storages Loop" + " : " + uuid);
					
					inboundStorageAvailableQty = inboundStorage.getAvailableQuantity();
					logger.debug("Quantity available in inboundStorage: " + inboundStorage.getId() + " : "+ inboundStorageAvailableQty + " : " + uuid);
					
					allocation = new SaleOrderAllocation();
					allocation.setInboundStorage(inboundStorage);
					allocation.setSaleOrder(saleOrder);
					
					logger.debug("setting Available and allocated quantity to: " + (inboundStorageAvailableQty >= currentAllocation ? currentAllocation : inboundStorageAvailableQty) + " : " + uuid);
					allocation.setAllocatedQuantity(inboundStorageAvailableQty >= currentAllocation ? currentAllocation : inboundStorageAvailableQty);
					allocation.setAvailableQuantity(allocation.getAllocatedQuantity());
					saleOrderService.upsertAllocation(allocation);
	
					allocationHistory = new SaleOrderAllocationHistory();
					allocationHistory.setAction("Allocated");
					allocationHistory.setQuantity(allocation.getAllocatedQuantity());
					allocationHistory.setSaleOrder(saleOrder);
					saleOrderService.upsertAllocationHistory(allocationHistory);
	
					logger.debug("Setting new storage available quantity: " + NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);
					if(NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) < 0) {
						inboundStorage.setAvailableQuantity(0.0);
						logger.error("Available quantity should not be negative in Inbound Storage :: ProductMsn ::[" +  inboundStorage.getProduct().getProductMsn() +"] :: WarehouseId :: ["+ saleOrder.getWarehouse().getId() +"]");
					}
					else {
						inboundStorage.setAvailableQuantity(NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()));
					}
					
					logger.debug("Setting new storage allocated quantity: " + NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()) + " : " + uuid);
					inboundStorage.setAllocatedQuantity(NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()));
	
					logger.trace("Updating inbound storage: " + inboundStorage.getId() + " : " + uuid);
					inboundStorageService.upsert(inboundStorage);
	
					currentAllocation = NumberUtil.round4(currentAllocation - allocation.getAllocatedQuantity());
					if (currentAllocation == 0) {
						logger.trace("Allocation Completed for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
						break;
					}
				}
	
				logger.debug("Setting saleOrder allocated quantity for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + allocatedQty + " : " + uuid);
				saleOrder.setAllocatedQuantity(allocatedQty);
				
				logger.trace("Updating Sale Order Info for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
				saleOrderService.upsert(saleOrder);
				
				logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Allocated Quantity: " + NumberUtil.round4(productInventory.getAllocatedQuantity() + allocatedQty) + " : " + uuid);
				productInventory.setAllocatedQuantity(NumberUtil.round4(productInventory.getAllocatedQuantity() + allocatedQty));
				
				logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Available Quantity: " + NumberUtil.round4(productInventory.getAvailableQuantity() - allocatedQty) + " : " + uuid);
				
				if(NumberUtil.round4(productInventory.getAvailableQuantity() - allocatedQty) < 0) {
					productInventory.setAvailableQuantity(0.0);
					logger.error("Available quantity should not be negative in ProductInventory :: ProductMsn ::[" +  productInventory.getProduct().getProductMsn() +"] :: WarehouseId :: ["+ productInventory.getWarehouse().getId() +"]");
				}
				else {
					productInventory.setAvailableQuantity(NumberUtil.round4(productInventory.getAvailableQuantity() - allocatedQty));
				}
				
				logger.trace("Updating product inventory with inventoryId: " + productInventory.getId() + " : " + uuid);
				productInventoryService.upsert(productInventory);
	
				Product product = productInventory.getProduct();
				
				logger.debug("Product Id: " + product.getId() + " Updated Allocated Quantity: " + NumberUtil.round4(product.getAllocatedQuantity() + allocatedQty) + " : " + uuid);
				product.setAllocatedQuantity(NumberUtil.round4(product.getAllocatedQuantity() + allocatedQty));
				
				logger.debug("Product Id: " + product.getId() + " Updated Available Quantity: " + NumberUtil.round4(product.getAvailableQuantity() - allocatedQty) + " : " + uuid);
				
				if(NumberUtil.round4(product.getAvailableQuantity() - allocatedQty) < 0) {
					product.setAvailableQuantity(0.0);
					logger.error("Available quantity should not be negative in Product Table :: ProductMsn ::[" +  product.getProductMsn() +"] :: WarehouseId :: ["+ saleOrder.getWarehouse().getId() +"]");
				}
				else {
					product.setAvailableQuantity(NumberUtil.round4(product.getAvailableQuantity() - allocatedQty));
				}
				
				logger.trace("Updating Product Entry for product: " + product.getId() + " : " + uuid);
				productService.upsert(product);
	
				Double remainingBlockedQty = NumberUtil.round4(blockedProductInventory.getBlockedQuantity() - allocatedQty);
				
				if(remainingBlockedQty == 0.0d) {
					
					blockedProductInventory.setStatus(BlockedProductInventoryStatus.PROCESSED);
					logger.info("Mark Blocked inventory status to Processed ");
				}
				
				blockedProductInventory.setBlockedQuantity(remainingBlockedQty);
				blockedProductInventoryRepo.save(blockedProductInventory);
	
				logger.info("Inventory allocated to sale order id: " + saleOrder.getId() + ", allocatedQty: " + allocatedQty);
			}
		}
	}
	
//	private boolean checkIfInventoryAvailable(ProductInventory productInventory, double blockedQuantity, double expiredQty, SaleOrder saleOrder, ProductInventoryConfig inventoryConfig) {
//		
//		if (productInventory == null 
//				|| (saleOrder.getBulkInvoiceId() != null && blockedQuantity <= 0)
//				|| (saleOrder.getBulkInvoiceId() == null && productInventory.getAvailableQuantity() - blockedQuantity - expiredQty <= 0)
//				|| (saleOrder.getBulkInvoiceId() == null && inventoryConfig != null
//						&& !inventoryConfig.getPlantProductInventoryConfigMappings().stream().map(e -> e.getPlant().getId()).collect(Collectors.toList()).contains(saleOrder.getPlant().getId())
//						&& productInventory.getAvailableQuantity() - blockedQuantity - expiredQty < inventoryConfig.getMaximumQuantity())) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
	@Override
	@Transactional
	public void transferAllocation(SaleOrder saleOrder, Double allocatedQuantity) {
		Double allocatedQty = allocatedQuantity;
		if (saleOrder == null) {
			return;
		}
		List<InboundStorage> inboundStorages = inboundStorageService
				.findAvailableByProduct(saleOrder.getProduct().getId());
		SaleOrderAllocation allocation;
		SaleOrderAllocationHistory allocationHistory;
		Double currentAllocation = allocatedQty;
		Double inboundStorageAvailableQty;
		for (InboundStorage inboundStorage : inboundStorages) {
			inboundStorageAvailableQty = inboundStorage.getAvailableQuantity();

			allocation = new SaleOrderAllocation();
			allocation.setInboundStorage(inboundStorage);
			allocation.setSaleOrder(saleOrder);
			allocation.setAllocatedQuantity(
					inboundStorageAvailableQty >= currentAllocation ? currentAllocation : inboundStorageAvailableQty);
			allocation.setAvailableQuantity(allocation.getAllocatedQuantity());
			saleOrderService.upsertAllocation(allocation);

			allocationHistory = new SaleOrderAllocationHistory();
			allocationHistory.setAction("Allocated");
			allocationHistory.setQuantity(allocation.getAllocatedQuantity());
			allocationHistory.setSaleOrder(saleOrder);
			saleOrderService.upsertAllocationHistory(allocationHistory);

			inboundStorage.setAvailableQuantity(
					NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()));
			inboundStorage.setAllocatedQuantity(
					NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()));
			inboundStorageService.upsert(inboundStorage);

			currentAllocation = NumberUtil.round4(currentAllocation - allocation.getAllocatedQuantity());
			if (currentAllocation == 0) {
				break;
			}
		}
	}

	@Override
	@Transactional
	public void deAllocateInventory(SaleOrder saleOrder) {
		logger.info("Inventory de-allocation service started");
		ProductInventory productInventory = productInventoryService
				.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
		if (productInventory == null || productInventory.getAllocatedQuantity() <= 0) {
			logger.info("Inventory not allocated for sale order id: " + saleOrder.getId());
		} else {
			productInventory.setAllocatedQuantity(
					NumberUtil.round4(productInventory.getAllocatedQuantity() - saleOrder.getAllocatedQuantity()));
			productInventory.setAvailableQuantity(
					NumberUtil.round4(productInventory.getAvailableQuantity() + saleOrder.getAllocatedQuantity()));
			productInventoryService.upsert(productInventory);

			Product product = productInventory.getProduct();
			product.setAllocatedQuantity(
					NumberUtil.round4(product.getAllocatedQuantity() - saleOrder.getAllocatedQuantity()));
			product.setAvailableQuantity(
					NumberUtil.round4(product.getAvailableQuantity() + saleOrder.getAllocatedQuantity()));
			productService.upsert(product);
			
			List<SaleOrderAllocation> allocations = saleOrderAllocationRepository.getSaleOrderAllocationBySaleOrderIdAndStatus(saleOrder.getId(), SaleOrderAllocationStatus.ALLOCATED);
			InboundStorage inboundStorage;
			for (SaleOrderAllocation allocation : allocations) {
				inboundStorage = allocation.getInboundStorage();
				inboundStorage.setAllocatedQuantity(
						NumberUtil.round4(inboundStorage.getAllocatedQuantity() - allocation.getAvailableQuantity()));
				inboundStorage.setAvailableQuantity(
						NumberUtil.round4(inboundStorage.getAvailableQuantity() + allocation.getAvailableQuantity()));
				inboundStorageService.upsert(inboundStorage);

				// todo discuss for available/allocated qty in order_allocation
				allocation.setStatus(SaleOrderAllocationStatus.CANCELLED);
				saleOrderService.upsertAllocation(allocation);
			}

			SaleOrderAllocationHistory allocationHistory = new SaleOrderAllocationHistory();
			allocationHistory.setAction("Cancelled");
			allocationHistory.setQuantity(0d);
			allocationHistory.setSaleOrder(saleOrder);
			saleOrderService.upsertAllocationHistory(allocationHistory);
		}
		logger.info("Inventory de-allocation service ended");
	}

	@Transactional
	@Override
	public boolean saveInventoryHistory(Warehouse warehouse, String productMsn, InventoryTransactionType transactionType,
			InventoryMovementType movementType, String transactionRef, Double prevQuantity, Double currQuantity) {
		boolean flag = false;
		Product product = prodRepo.getUniqueByProductMsn(productMsn);
		ProductInventoryHistory history = new ProductInventoryHistory();
		history.setCurrentQuantity(currQuantity);
		history.setPrevQuantity(prevQuantity);
		history.setProduct(product);
		history.setProductMsn(productMsn);
		history.setTransactionRef(transactionRef);
		history.setTransactionType(transactionType);
		history.setMovementType(movementType);
		history.setWarehouse(warehouse);
		productInventoryHistoryRepo.save(history);
		flag = true;
		return flag;
	}
 
	@Override
	@Transactional
	public BaseResponse addAvailableInventory(Integer warehouseId, Integer productId, Double qty) {
		
		logger.info("add available inventory service started");
		BaseResponse response = new BaseResponse();
		Product product = productService.getById(productId);
		if (product == null) {
			response.setMessage("Product does not exist");
			logger.info("Product does not exist");
			return response;
		}

		ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(warehouseId,
				productId);
		if (productInventory == null) { // create new product_inventory
			response.setMessage("Product Inventory does not exist");
			logger.info("Product Inventory does not exist");
			return response;
		}

		// update product_inventory
		Double availableQuantity = productInventory.getAvailableQuantity();
		logger.debug("AvailableQuantity :: " + availableQuantity);
		Double currentQty = productInventory.getCurrentQuantity();
		logger.debug("CurrentQuantity :: " + currentQty);
		
		productInventory.setAvailableQuantity(NumberUtil.round4(availableQuantity + qty));
		productInventory.setCurrentQuantity(NumberUtil.round4(currentQty + qty));
		productInventoryService.upsert(productInventory);

		availableQuantity = product.getAvailableQuantity();
		currentQty = product.getCurrentQuantity();

		product.setAvailableQuantity(NumberUtil.round4(availableQuantity + qty));
		product.setCurrentQuantity(NumberUtil.round4(currentQty + qty));
		productService.upsert(product); // update product inventory

		response.setMessage("Inventory added");
		response.setStatus(true);

		logger.info("add inventory service ended");
		
		return response;
	}
	
	@Override
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public BaseResponse deductAvailableInventoryWarehouseWise(Integer warehouseId, ProductInventory productInventory,Product product) {
		
		logger.info("deduct available inventory service started");
		BaseResponse response = new BaseResponse();
		if (product == null) {
			response.setMessage("Product does not exist");
			logger.info("Product does not exist");
			return response;
		}

		if (productInventory == null) { // create new product_inventory
			response.setMessage("Product Inventory does not exist");
			logger.info("Product Inventory does not exist");
			return response;
		}

		// update product_inventory
		Double availableQuantity = productInventory.getAvailableQuantity();
		Double currentQty = productInventory.getCurrentQuantity();
		Double setCurrentQtyProdInventory = NumberUtil.round4(currentQty - availableQuantity);

		productInventory.setAvailableQuantity(0.0d);
		if(setCurrentQtyProdInventory < 0.0) {
			setCurrentQtyProdInventory =0.0;
		}
		productInventory.setCurrentQuantity(setCurrentQtyProdInventory);
		productInventoryService.upsert(productInventory);

		Double setProductAvailableQuantity = NumberUtil.round4(product.getAvailableQuantity() - availableQuantity );
		Double setProductCurrentQty = NumberUtil.round4(product.getCurrentQuantity() - availableQuantity);
		
		if(setProductAvailableQuantity < 0.0) {
			setProductAvailableQuantity =0.0;
		}
		if(setProductCurrentQty < 0.0) {
			setProductCurrentQty = 0.0;
		}

		product.setAvailableQuantity(setProductAvailableQuantity);
		product.setCurrentQuantity(setProductCurrentQty);
		productService.upsert(product); // update product inventory

		response.setMessage("Inventory Deducted");
		response.setStatus(true);

		logger.info("Deduct inventory service ended");

		return response;
	}
}