 package com.moglix.wms.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.LockModeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.moglix.wms.api.request.EMSPackableQuantityRequest;
import com.moglix.wms.api.request.InventoryUpdateRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.constants.PublishSystemType;
import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.ProductInventoryConfig;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.entities.SaleOrderAllocationHistory;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMapping;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMappingItem;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.kafka.producer.KafkaEmsSalesOpsUpdateProducer;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.ProductInventoryConfigRepository;
import com.moglix.wms.repository.SaleOrderSupplierPurchaseOrderMappingItemRepository;
import com.moglix.wms.repository.SaleOrderSupplierPurchaseOrderMappingRepository;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.ISaleOrderService;
import com.moglix.wms.util.MailUtil;
import com.moglix.wms.util.NumberUtil;

/**
 * @author sparsh saxena on 9/3/21
 */
@Service("inventoryServiceV2")
public class InventoryServiceImplV2 implements IInventoryService {

	Logger logger = LogManager.getLogger(InventoryServiceImplV2.class);

	@Autowired
	private ISaleOrderService saleOrderService;

	@Autowired
	private IProductInventoryService productInventoryService;

	@Autowired
	private SaleOrderSupplierPurchaseOrderMappingRepository saleOrderSupplierPurchaseOrderMappingRepository;
	
	@Autowired
	private SaleOrderSupplierPurchaseOrderMappingItemRepository saleOrderSupplierPurchaseOrderMappingItemRepository;

	@Autowired
	private ProductInventoryConfigRepository productInventoryConfigRepository;

	@Autowired
	private InboundStorageRepository inboundStorageRepo;

	@Autowired
	PlatformTransactionManager transactionManager;

	@Autowired
	private IInboundStorageService inboundStorageService;

	@Autowired
	private IProductService productService;

	@Autowired
	private MailUtil mailUtil;

	//private final int numberOfRetries = 3;
	
	@Autowired
	KafkaEmsSalesOpsUpdateProducer kafkaPublisherInventoryUpdate;

	@Override
	public BaseResponse addInventory(Integer warehouseId, Integer productId, Double purchasePrice, Double qty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse deductAllocatedInventory(Integer warehouseId, Integer productId, Double qty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deAllocateInventory(SaleOrder saleOrder) {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseResponse deductAvailableInventory(Integer warehouseId, Integer productId, Double qty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse addAvailableInventory(Integer warehouseId, Integer productId, Double qty) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void transferAllocation(SaleOrder saleOrder, Double allocatedQuantity) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean saveInventoryHistory(Warehouse warehouse, String productMsn,
			InventoryTransactionType transactionType, InventoryMovementType movementType, String transactionRef,
			Double prevQuantity, Double currQuantity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BaseResponse deductAvailableInventoryWarehouseWise(Integer warehouseId, ProductInventory prodInventory,
			Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public boolean allocateInventoryForSaleOrder(Integer saleOrderId) throws InterruptedException {

		logger.info("Allocation Service Started for saleOrder :: " + saleOrderId);
		if (saleOrderId == null) {
			logger.info("SaleOrder id is null");
		} 
		else {
			
			SaleOrder saleOrder = saleOrderService.getById(saleOrderId);

			if (saleOrder == null) {
				logger.info("Invalid SaleOrder.!!! :: [" + saleOrderId +"]");
				return false;
			}
			else {
				
				// Double prevAllocateQuantity = saleOrder.getAllocatedQuantity();
				logger.info("Running old flow for allocation.");
				
				try {
					allocateInventory(saleOrder);
				} 
				catch (Exception e) {
					logger.error("Error coming in allocation process for Saleorder :: [" + saleOrderId +"]");
					e.printStackTrace();
				}
				// updateEMSandSalesOps(saleOrder, prevAllocateQuantity, true);
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

	@Transactional
	@Override
	public void allocateInventory(SaleOrder saleOrder) {

		String uuid = UUID.randomUUID().toString();

		logger.info("Allocating inventory for sale order id :: [" + saleOrder.getId() + "] and warehouseId :: [" + saleOrder.getWarehouse().getId() +"]");
		ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
		Double productInventoryQty = 0.0d;
		if(productInventory != null) {
			logger.info("Available inventory in warehouse :: [" + productInventory.getAvailableQuantity() +"]");
			productInventoryQty = productInventory.getAvailableQuantity();
		}else {
			logger.info("MSN [" + saleOrder.getProduct().getProductMsn() +"] is not exists in warehouse.");
			productInventoryQty = 0.0d;
		}
		ProductInventoryConfig inventoryConfig = productInventoryConfigRepository.findByProductMsnAndWarehouseId(saleOrder.getProduct().getProductMsn(), saleOrder.getWarehouse().getId());
		// TODO: double initialisation check

		Double expiredQty = 0.0d;
		
		Double actualAvailableQty = 0.0d;
		logger.info("Checking inbounded inventory for other orders.");
		
		Double totalInboundedQty = saleOrderSupplierPurchaseOrderMappingItemRepository.getInboundedInventoryForOtherOrders(saleOrder.getProduct().getProductMsn(), saleOrder.getWarehouse().getId());
		
		logger.info("Total inbounded inventory for other orders :: [" + totalInboundedQty +"]");
		
		actualAvailableQty = productInventoryQty - totalInboundedQty; // subtract already available quantity for other orders
		logger.info("Actual available inventory :: [" + actualAvailableQty +"]");
		
		if(actualAvailableQty < 0) {
			actualAvailableQty = 0.0d;
		}
		
		if (saleOrder.getProduct().getExpiryDateManagementEnabled() != null && saleOrder.getProduct().getExpiryDateManagementEnabled()) {
			expiredQty = inboundStorageRepo.getTotalExpiredInventoryByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
			expiredQty = expiredQty != null ? expiredQty : 0.0d;
		}

		if(!(actualAvailableQty>= saleOrder.getOrderedQuantity())){
			logger.info("Inventory you are trying to allocate for productMSN :: ["+ saleOrder.getProduct().getProductMsn() + "]  is greater then the inventory actual present in warehouse :: [" + saleOrder.getWarehouse().getId() + "].");
		}
		if (!checkIfInventoryAvailable(productInventory, actualAvailableQty, expiredQty, saleOrder, inventoryConfig)) {
			logger.info("Inventory not available for sale order id :: " + saleOrder.getId());
		}
		else if (saleOrder.getAllocatedQuantity().equals(saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity())) {
			logger.info("Ordered quantity has been already allocated for sale order id :: " + saleOrder.getId());
		} 
		else if (saleOrder.getOrderedQuantity() < saleOrder.getPackedQuantity()) {
			logger.info("Invalid Quantity. Ordered Quantity less than packed quantity for emsOrderItemId :: " + saleOrder.getEmsOrderItemId());
		}
		else {

			logger.debug("saleOrder orderedQuantity: " + saleOrder.getOrderedQuantity() + " saleOrder Packed Quantity: "
					+ saleOrder.getPackedQuantity() + " saleOrder pre-allocated quantity: "
					+ saleOrder.getAllocatedQuantity() + " for saleOrder: " + saleOrder.getEmsOrderItemId() + " : "
					+ uuid);

			Double allocatedQty = 0.0d;

			if (inventoryConfig != null && !inventoryConfig.getPlantProductInventoryConfigMappings().stream().map(e -> e.getPlant().getId()).collect(Collectors.toList()).contains(saleOrder.getPlant().getId())) {
				allocatedQty = Math.min(productInventory.getAvailableQuantity() - expiredQty - inventoryConfig.getMaximumQuantity(),(saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity()- saleOrder.getAllocatedQuantity()));
			} 
			else {
				allocatedQty = Math.min(productInventory.getAvailableQuantity() - expiredQty,(saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity() - saleOrder.getAllocatedQuantity()));
			}

			//logger.debug("Quantity to be allocated to saleOrder " + saleOrder.getEmsOrderItemId() + ": " + allocatedQty + " : " + uuid);
			logger.debug("Quantity to be allocated to saleOrder id :: [" + saleOrder.getId() + "], EmsOrderItemId :: [" + saleOrder.getEmsOrderItemId() + "] : " + allocatedQty + " : " + uuid);

			List<InboundStorage> inboundStorages;

			if (saleOrder.getProduct().getExpiryDateManagementEnabled() != null && saleOrder.getProduct().getExpiryDateManagementEnabled()) {
				inboundStorages = inboundStorageRepo.findByProductAndWarehouseForAvailableBasedOnExpiry(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
			} 
			else {
				inboundStorages = inboundStorageRepo.findByProductAndWarehouseForAvailable(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
			}

			logger.trace("Found " + inboundStorages.size() + "Inbound Storages" + " : " + uuid);

			// TODO: Declare this locally where it is getting initialized
			SaleOrderAllocation allocation;
			SaleOrderAllocationHistory allocationHistory;
			Double currentAllocation = allocatedQty;
			logger.trace("Initialised currentAllocation with value: " + allocatedQty + " : " + uuid);

			Double inboundStorageAvailableQty;
			for (InboundStorage inboundStorage : inboundStorages) {
				logger.trace("Inside inbound storages Loop" + " : " + uuid);

				inboundStorageAvailableQty = inboundStorage.getAvailableQuantity();
				logger.debug("Quantity available in inboundStorage: " + inboundStorage.getId() + " : " + inboundStorageAvailableQty + " : " + uuid);

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
				
				logger.debug("Setting new storage allocated quantity: " + NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);
				inboundStorage.setAllocatedQuantity(NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()));

				logger.trace("Updating inbound storage: " + inboundStorage.getId() + " : " + uuid);
				inboundStorageService.upsert(inboundStorage);

				currentAllocation = NumberUtil.round4(currentAllocation - allocation.getAllocatedQuantity());
				if (currentAllocation == 0) {
					logger.trace("Allocation Completed for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
					break;
				}
			}

			logger.debug("Setting saleOrder allocated quantity for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + saleOrder.getAllocatedQuantity() + allocatedQty + " : " + uuid);
			saleOrder.setAllocatedQuantity(saleOrder.getAllocatedQuantity() + allocatedQty);

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

			if (inventoryConfig != null && productInventory.getAvailableQuantity() <= inventoryConfig.getMinimumQuantity()) {
				// Handle available quantity < configured minimum quantity
				try {
					String mailContent = Constants.getInventoryEmailContent(
							productInventory.getProduct().getProductMsn(),
							productInventory.getProduct().getProductName(), productInventory.getWarehouse().getName());
					mailUtil.sendMail(mailContent, Constants.INVENTORY_MAIL_SUBJECT,
							productInventory.getWarehouse().getId());
				} catch (Exception e) {
					logger.error("Error Occured in sending mail for VMI inventory for product: " + productInventory.getProduct().getProductMsn() + " and warehouse: " + productInventory.getWarehouse().getName(), e);
				}
			}

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

			logger.info("Inventory allocated to sale order id: " + saleOrder.getId() + ", allocatedQty: " + allocatedQty);
		}
	}
	
	public boolean allocateInventoryByDemandGenerated(SaleOrder saleOrder, SaleOrderSupplierPurchaseOrderMappingItem saleOrderSupplierPurchaseOrderMappingItemObj) {// Integer batchId, Integer pickUpWarehouseId, Double allocatedQty) {

		logger.info("Inside Allocating inventory by Demand Generated Method.");
		String uuid = UUID.randomUUID().toString();
		logger.info("Allocating inventory for sale order id :: [" + saleOrder.getId() + "] and warehouseId :: [" + saleOrderSupplierPurchaseOrderMappingItemObj.getPickUpWarehouseId() + "]");

		ProductInventory productInventory = productInventoryService.getByWarehouseIdAndProductId(saleOrderSupplierPurchaseOrderMappingItemObj.getPickUpWarehouseId(), saleOrder.getProduct().getId());
		logger.info("Product Inventory available :: [" + productInventory.getAvailableQuantity() + "]");
		
		if (!(saleOrder.getWarehouse().getId().equals(saleOrderSupplierPurchaseOrderMappingItemObj.getPickUpWarehouseId()))) {
			logger.info("SaleOrder warehouse and PickupWarehouse are not same for sale order id :: " + saleOrder.getId());
			return false;
		} 
		else if (saleOrder.getAllocatedQuantity().equals(saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity())) {
			logger.info("Ordered quantity has been already allocated for sale order id :: " + saleOrder.getId());
			return false;
		} 
		else if (saleOrder.getOrderedQuantity() < saleOrder.getPackedQuantity()) {
			logger.info("Invalid Quantity. Ordered Quantity less than packed quantity for emsOrderItemId :: " + saleOrder.getEmsOrderItemId());
			return false;
		} 
		else {

			logger.debug("SaleOrder orderedQuantity :: [" + saleOrder.getOrderedQuantity() + "] SaleOrder Packed Quantity :: [" + saleOrder.getPackedQuantity() + 
					"] SaleOrder pre-allocated quantity :: [" + saleOrder.getAllocatedQuantity() + "] for saleOrder :: ["+ saleOrder.getId() +"] and EmsOrder :: ["+ saleOrder.getEmsOrderItemId() + "] : " + uuid);

			Double allocatedQty = saleOrderSupplierPurchaseOrderMappingItemObj.getQuantity();
			logger.debug("Quantity to be allocated to saleOrder :: [" + saleOrder.getId() + "], EmsOrderID :: ["+ saleOrder.getEmsOrderItemId() + "] :: " + allocatedQty + " : " + uuid);

			
			logger.info("Inbound id for saleorder [ " + saleOrder.getId() + "]" + " :: Inbound id :: [" + saleOrderSupplierPurchaseOrderMappingItemObj.getInboundId()+"]"); 
			
			List<InboundStorage> inboundStorages = inboundStorageRepo.findByProductAndWarehouseAndInboundForAvailable(saleOrderSupplierPurchaseOrderMappingItemObj.getPickUpWarehouseId(), saleOrder.getProduct().getId(), saleOrderSupplierPurchaseOrderMappingItemObj.getInboundId());

			logger.trace("Found " + inboundStorages.size() + "Inbound Storages" + " : " + uuid);
			logger.info("Found " + inboundStorages.size() + "Inbound Storages" + " : for saleOrder :: " + saleOrder.getId());
			
			if(inboundStorages!= null && !inboundStorages.isEmpty()) {
				
				// TODO: Declare this locally where it is getting initialized
				SaleOrderAllocation allocation;
				SaleOrderAllocationHistory allocationHistory;
				Double currentAllocation = allocatedQty;
				logger.trace("Initialised currentAllocation with value: " + allocatedQty + " : " + uuid);
	
				Double inboundStorageAvailableQty;
				
				for (InboundStorage inboundStorage : inboundStorages) {
					
					logger.trace("Inside inbound storages Loop" + " : " + uuid);
					logger.info("InboundStorage id for saleorder [ " + saleOrder.getId() + "]" + " :: InboundStorage id :: " + inboundStorage.getId());
	
					inboundStorageAvailableQty = inboundStorage.getAvailableQuantity();
					logger.debug("Quantity available in inboundStorage: " + inboundStorage.getId() + " : [" + inboundStorageAvailableQty + "] : " + uuid);
	
					allocation = new SaleOrderAllocation();
					allocation.setInboundStorage(inboundStorage);
					allocation.setSaleOrder(saleOrder);
					
					// check inbounded quantity for this saleorder
					
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
	
					logger.debug("Setting new storage allocated quantity: " + NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);
					inboundStorage.setAllocatedQuantity(NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()));
	
					logger.trace("Updating inbound storage: " + inboundStorage.getId() + " : " + uuid);
					inboundStorageService.upsert(inboundStorage);
	
					currentAllocation = NumberUtil.round4(currentAllocation - allocation.getAllocatedQuantity());
					if (currentAllocation == 0) {
						logger.trace("Allocation Completed for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
						break;
					}
				}
	
				logger.debug("Setting saleOrder allocated quantity for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + saleOrder.getAllocatedQuantity() + allocatedQty + " : " + uuid);
				saleOrder.setAllocatedQuantity(saleOrder.getAllocatedQuantity() + allocatedQty);
	
				logger.trace("Updating Sale Order Info for saleOrder: " + saleOrder.getId() + " : " + uuid);
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
	
				logger.info("Inventory allocated to sale order id: " + saleOrder.getId() + ", allocatedQty: " + allocatedQty);
			}else {
				logger.trace("Inbound storages not found for saleorder :: " + saleOrder.getId());
				return false;
			}
		}
		return true;
	}

	private boolean checkIfInventoryAvailable(ProductInventory productInventory, double actualAvailableQty, double expiredQty, SaleOrder saleOrder, ProductInventoryConfig inventoryConfig) {

		if (productInventory == null
				|| (saleOrder.getBulkInvoiceId() == null && actualAvailableQty - expiredQty <= 0)
				|| (saleOrder.getBulkInvoiceId() == null && inventoryConfig != null
						&& !inventoryConfig.getPlantProductInventoryConfigMappings().stream().map(e -> e.getPlant().getId()).collect(Collectors.toList()).contains(saleOrder.getPlant().getId())
						&& actualAvailableQty - expiredQty < inventoryConfig.getMaximumQuantity())) {
			return false;
		} else {
			return true;
		}
	}

//	private void updateEMSandSalesOps(SaleOrder so, Double prevAllocateQuantity, Boolean isInventory) throws InterruptedException {
//
//		HttpEntity<BaseResponse> emsResponse;
//
//		int retryCount = 0;
//		boolean isSuccess = false;
//		do {
//			logger.info("Sleeping for " + retryCount + " Seconds");
//			Thread.sleep(retryCount * 1000);
//
//			logger.info("Calling EMS API to update Packable quantity for orderId: " + so.getEmsOrderId() + "with allocatedQuantity:" + so.getAllocatedQuantity());
//
//			RestTemplate restTemplate = new RestTemplate();
//			EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(so.getEmsOrderItemId(), so.getAllocatedQuantity(), "WMS");
//
//			emsResponse = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRequest, BaseResponse.class);
//			isSuccess = emsResponse.getBody().getStatus();
//
//			if (isSuccess) {
//				// Call sales ops API
//				// logger.info("Sleeping for 3 second before calling Sales Ops API");
//				// Thread.sleep(3000);
//
//				if (so.getAllocatedQuantity() == 0) {
//					isInventory = false;
//				}
//
//				SalesOpsDemandRequest salesOpsRequest = new SalesOpsDemandRequest(so.getItemRef(), so.getAllocatedQuantity(), isInventory);
//
//				logger.info("Calling Sales Ops API to update demands quantity with request: " + salesOpsRequest);
//
//				ResponseEntity<BaseResponse> salesOpsResponse = null;
//				HttpHeaders headers = new HttpHeaders();
//
//				headers.add("Authorization", Constants.SALES_OPS_AUTH_TOKEN);
//				try {
//					salesOpsResponse = restTemplate.exchange(Constants.SALES_OPS_DEMAND_API, HttpMethod.POST, new HttpEntity<SalesOpsDemandRequest>(salesOpsRequest, headers), BaseResponse.class);
//				}
//				catch (Exception e) {
//					logger.error("Error occured in updating sales Ops API:", e);
//				}
//
//				if (salesOpsResponse == null || !salesOpsResponse.getBody().getStatus()) {
//					logger.warn("Unusual response from sales Ops API." + salesOpsResponse);
//					EMSPackableQuantityRequest emsRollbackRequest = new EMSPackableQuantityRequest(so.getEmsOrderItemId(), prevAllocateQuantity, "WMS");
//					restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRollbackRequest, BaseResponse.class);
//					throw new WMSException("Cannot update through sales ops API. Rolling back stuff");
//				}
//			} else {
//				logger.info("Request failed tp update packable quantity in EMS. Will Retry after: " + (retryCount + 1) + " seconds");
//			}
//			retryCount++;
//		} while (!isSuccess && retryCount < numberOfRetries);
//
//		if (!isSuccess) {
//			throw new WMSException("Cannot update through EMS API. Rolling back stuff");
//		}
//	}


	@Override
	public boolean allocateInventoryForProductId(Integer productId) {

		logger.trace("Allocating inventory to open Orders for productID: " + productId);

		List<SaleOrder> saleOrders = saleOrderService.findOpenSaleOrderForProduct(productId); // ignore STN Order changes present in service layer.
		logger.trace("Found " + saleOrders.size() + " orders for productId: " + productId);
		
		if (!CollectionUtils.isEmpty(saleOrders)) {
		
			for (SaleOrder so : saleOrders) {
				
				Double prevAllocateQuantity = so.getAllocatedQuantity();
				logger.debug("Previous allocated quantity for saleOrder: [" +so.getId() + "], EmsOrderItemId :: ["+ so.getEmsOrderItemId() + "], Item ref :: [" + so.getItemRef() + "] is: " + prevAllocateQuantity);
				
				DefaultTransactionDefinition def = new DefaultTransactionDefinition();
				def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
				TransactionStatus transactionStatus = transactionManager.getTransaction(def);
				
				try {
					List<SaleOrderSupplierPurchaseOrderMapping> saleOrderSupplierPurchaseOrderMappingList = saleOrderSupplierPurchaseOrderMappingRepository.findByItemRef(so.getItemRef());
					logger.info("SaleOrderSupplierPurchaseOrderMappingList size :: " + saleOrderSupplierPurchaseOrderMappingList.size());
					
					if(saleOrderSupplierPurchaseOrderMappingList != null && !saleOrderSupplierPurchaseOrderMappingList.isEmpty()) {
						logger.info("Mapping found for SaleOrder ItemRef :: [" + so.getItemRef() +"]");
						
						for (SaleOrderSupplierPurchaseOrderMapping obj : saleOrderSupplierPurchaseOrderMappingList) {
						
							allocateInventoryAndNotifySystems(so, obj, prevAllocateQuantity, false);
						}
					}
					else {
						logger.info("Mapping not found for SaleOrder ItemRef :: [" + so.getItemRef() +"]");
					}
				} 
				catch (Exception e) {
					logger.error("Error occured while allocating quantities to saleOrder: [" +so.getId() + "], EmsOrderItemId :: [" + so.getEmsOrderItemId() + "]. Transactions Rolled Back", e);
					
					try {
						transactionManager.rollback(transactionStatus);
						
					 } catch (UnexpectedRollbackException e1) {
						  logger.warn("Exception in rollback. Transaction Silently rolled back", e1); 
						  //throw new WMSException("Allocation not done. Rolling back stuff.");
						  continue;
					 }
				}
				transactionManager.commit(transactionStatus);
			}
		} 
		else {
			logger.info("No Open Order found for this productID: [" + productId + "]");
		}

		return true;
	}

	private void allocateInventoryAndNotifySystems(SaleOrder saleOrder, SaleOrderSupplierPurchaseOrderMapping mappingObj, Double prevAllocateQuantity, boolean isInventory) throws Exception {

		List<SaleOrderSupplierPurchaseOrderMappingItem> saleOrderSupplierPurchaseOrderMappingItemList = saleOrderSupplierPurchaseOrderMappingItemRepository.findByItemRefAndSaleOrderSupplierPurchaseOrderMappingIdAndStatus(saleOrder.getItemRef(), mappingObj.getId(), SaleOrderSupplierPurchaseOrderMappingStatus.BIN_ASSIGNED);
		
		logger.info("SaleOrderSupplierPurchaseOrderMappingItemList size :: " + saleOrderSupplierPurchaseOrderMappingItemList.size());

		if (!saleOrderSupplierPurchaseOrderMappingItemList.isEmpty()) {

			logger.info("Checking inventory is inbounded for saleorder :: [" + saleOrder.getId() + "] Purchase order id :: [" + mappingObj.getSupplierPoId());

			for(SaleOrderSupplierPurchaseOrderMappingItem saleOrderSupplierPurchaseOrderMappingItemObj : saleOrderSupplierPurchaseOrderMappingItemList) {

				logger.info("Inventory is inbounded for saleorder :: [" + saleOrderSupplierPurchaseOrderMappingItemObj.getSaleOrderId() + "] Purchase order id :: [" + saleOrderSupplierPurchaseOrderMappingItemObj.getSupplierPoId() + "] Item Ref :: [" + saleOrderSupplierPurchaseOrderMappingItemObj.getItemRef() + "]");
				
				boolean result = false;
				result = allocateInventoryByDemandGenerated(saleOrder, saleOrderSupplierPurchaseOrderMappingItemObj);
				
				if(result == true) {
					
					saleOrderSupplierPurchaseOrderMappingItemObj.setStatus(SaleOrderSupplierPurchaseOrderMappingStatus.ALLOCATED);
					saleOrderSupplierPurchaseOrderMappingItemRepository.save(saleOrderSupplierPurchaseOrderMappingItemObj);
					
					// kafkaPublisherInventoryUpdate.sendRequest(new InventoryUpdateRequest(saleOrder.getItemRef(), saleOrder.getAllocatedQuantity(), true, PublishSystemType.WMS));
															
					logger.info("Calling EMS API to update Packable quantity for orderId: " + saleOrder.getEmsOrderId() + "with allocatedQuantity:" + saleOrder.getAllocatedQuantity());
					RestTemplate restTemplate = new RestTemplate();
					EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(saleOrder.getEmsOrderItemId(), saleOrder.getAllocatedQuantity(), "WMS");
	
					ResponseEntity<BaseResponse> emsResponse = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRequest, BaseResponse.class);
						
					if (emsResponse.getBody().getStatus()) {
						logger.info(String.format("Packable quantity for EMS OrderId :: %s is updated in EMS." , saleOrder.getEmsOrderId()));
					}
					else {
						throw new WMSException(String.format("Packable quantity for EMS OrderId :: %s is not updated in EMS. Rolling back stuff!!!" , saleOrder.getEmsOrderId()));
					}
					
					// Update EMS and SalesOps old method
					
//					logger.info("Notifying EMS and salesOps for SaleOrder: " + saleOrder.getEmsOrderItemId());
//	
//					logger.info("Calling EMS API to update Packable quantity for orderId: " + saleOrder.getEmsOrderId()+ "with allocatedQuantity:" + saleOrder.getAllocatedQuantity());
//					RestTemplate restTemplate = new RestTemplate();
//					EMSPackableQuantityRequest emsRequest = new EMSPackableQuantityRequest(saleOrder.getEmsOrderItemId(), saleOrder.getAllocatedQuantity(), "WMS");
//	
//					ResponseEntity<BaseResponse> emsResponse = restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRequest, BaseResponse.class);
//					
//					if (emsResponse.getBody().getStatus()) {
//						// Call sales ops API
//	
//						if (saleOrder.getAllocatedQuantity() == 0) {
//							isInventory = false;
//						}
//	
//						SalesOpsDemandRequest salesOpsRequest = new SalesOpsDemandRequest(saleOrder.getItemRef(), saleOrder.getAllocatedQuantity(), isInventory);
//	
//						logger.info("Calling Sales Ops API to update demands quantity with request: " + salesOpsRequest);
//	
//						ResponseEntity<BaseResponse> salesOpsResponse = null;
//						HttpHeaders headers = new HttpHeaders();
//	
//						headers.add("Authorization", Constants.SALES_OPS_AUTH_TOKEN);
//						try {
//							salesOpsResponse = restTemplate.exchange(Constants.SALES_OPS_DEMAND_API, HttpMethod.POST, new HttpEntity<SalesOpsDemandRequest>(salesOpsRequest, headers), BaseResponse.class);
//						} catch (Exception e) {
//							logger.error("Error occured in updating sales Ops API:", e);
//						}
//	
//						if (salesOpsResponse == null || !salesOpsResponse.getBody().getStatus()) {
//							logger.warn("Unusual response from sales Ops API." + salesOpsResponse);
//							EMSPackableQuantityRequest emsRollbackRequest = new EMSPackableQuantityRequest(saleOrder.getEmsOrderItemId(), prevAllocateQuantity, "WMS");
//							restTemplate.postForEntity(Constants.EMS_PACKABLE_QUANTITY_API, emsRollbackRequest, BaseResponse.class);
//							throw new WMSException("Cannot update through sales ops API. Rolling back stuff");
//						}
//					} 
//					else {
//						throw new WMSException("Cannot update through EMS API. Rolling back stuff");
//					}
				}
			}
		}
	}
}
