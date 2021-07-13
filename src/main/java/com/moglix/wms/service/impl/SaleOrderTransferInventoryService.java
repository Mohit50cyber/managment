package com.moglix.wms.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.constants.SaleOrderAllocationStatus;
import com.moglix.wms.entities.BlockedProductInventory;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.ProductInventoryConfig;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.entities.SaleOrderAllocationHistory;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.repository.BlockedProductInventoryRepository;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.ProductInventoryConfigRepository;
import com.moglix.wms.repository.SaleOrderAllocationRepository;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.ISaleOrderService;
import com.moglix.wms.util.MailUtil;
import com.moglix.wms.util.NumberUtil;

@Service("saleOrderTransferInventoryService")
public class SaleOrderTransferInventoryService implements IInventoryService {
	
    private Logger logger = LogManager.getLogger(SaleOrderTransferInventoryService.class);

    @Autowired
    private IProductInventoryService productInventoryService;
    
    @Autowired
   	private IProductService productService;

    @Autowired
    private SaleOrderAllocationRepository saleOrderAllocationRepository;
    
    @Autowired
    private IInboundStorageService inboundStorageService;
    
    @Autowired
    private ISaleOrderService saleOrderService;
    
    @Autowired
	private BlockedProductInventoryRepository blockedProductInventoryRepo;
	
	@Autowired
	private InboundStorageRepository inboundStorageRepo;
	
	@Autowired
	private ProductInventoryConfigRepository productInventoryConfigRepository;
	
	@Autowired
	private MailUtil mailUtil;
	
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
	@Transactional
	public void deAllocateInventory(SaleOrder saleOrder) {

		String uuid = UUID.randomUUID().toString();
		
		logger.info("Inventory de-allocation service started for sale order: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
		
		ProductInventory productInventory = productInventoryService
				.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
		if (productInventory == null || productInventory.getAllocatedQuantity() <= 0) {
			logger.info("Inventory not allocated for sale order id: " + saleOrder.getId() + " : " + uuid);
		} else {
			List<SaleOrderAllocation> allocations = saleOrderAllocationRepository.getSaleOrderAllocationBySaleOrderIdAndStatus(saleOrder.getId(), SaleOrderAllocationStatus.ALLOCATED);

			logger.info("Got " + allocations.size() + " allocation for sale order " + saleOrder.getEmsOrderItemId() + " : " + uuid);

			if(!allocations.isEmpty()) {
				logger.debug("Setting ProductInventory Allocated Quantity: " + (NumberUtil.round4(productInventory.getAllocatedQuantity() - saleOrder.getAllocatedQuantity())) + " : " + uuid);
				productInventory.setAllocatedQuantity(
						NumberUtil.round4(productInventory.getAllocatedQuantity() - saleOrder.getAllocatedQuantity()));
				
				logger.debug("Setting ProductInventory Available Quantity: " + (
						NumberUtil.round4(productInventory.getAvailableQuantity() + saleOrder.getAllocatedQuantity())) + " : " + uuid);
				productInventory.setAvailableQuantity(
						NumberUtil.round4(productInventory.getAvailableQuantity() + saleOrder.getAllocatedQuantity()));
				
				logger.trace("Updating product inventory with productinventory id: "  + productInventory.getId() +  " : " + uuid);
				productInventoryService.upsert(productInventory);

				Product product = productInventory.getProduct();
				
				logger.debug("Setting Product Allocated Quantity: " + (
						NumberUtil.round4(product.getAllocatedQuantity() - saleOrder.getAllocatedQuantity())) + " : " + uuid);
				product.setAllocatedQuantity(
						NumberUtil.round4(product.getAllocatedQuantity() - saleOrder.getAllocatedQuantity()));
				
				logger.debug("Setting Product Available Quantity: " + (
						NumberUtil.round4(product.getAvailableQuantity() + saleOrder.getAllocatedQuantity())) + " : " + uuid);
				product.setAvailableQuantity(
						NumberUtil.round4(product.getAvailableQuantity() + saleOrder.getAllocatedQuantity()));
				
				logger.trace("Updating Product Entry for product: " + product.getId() + " : " + uuid);
				productService.upsert(product);

				InboundStorage inboundStorage;
				for (SaleOrderAllocation allocation : allocations) {
					if(allocation.getAvailableQuantity() > 0) {
						inboundStorage = allocation.getInboundStorage();
						
						logger.info("Setting inbound storage available : " + inboundStorage.getId() + "----" + (
								NumberUtil.round4(inboundStorage.getAllocatedQuantity() - allocation.getAvailableQuantity())) + " : " + uuid);
						
						inboundStorage.setAllocatedQuantity(
								NumberUtil.round4(inboundStorage.getAllocatedQuantity() - allocation.getAvailableQuantity()));
						
						logger.info("Setting inbound storage available : " + inboundStorage.getId() + "----" + (NumberUtil
								.round4(inboundStorage.getAvailableQuantity() + allocation.getAvailableQuantity())) + " : " + uuid);
						
						inboundStorage.setAvailableQuantity(
								NumberUtil.round4(inboundStorage.getAvailableQuantity() + allocation.getAvailableQuantity()));
						inboundStorageService.upsert(inboundStorage);

						allocation.setStatus(SaleOrderAllocationStatus.TRANSFERRED);
						allocation.setAvailableQuantity(0.0d);
						saleOrderService.upsertAllocation(allocation);
						
						SaleOrderAllocationHistory allocationHistory = new SaleOrderAllocationHistory();
						allocationHistory.setAction("Transferred");
						allocationHistory.setQuantity(0d);
						allocationHistory.setSaleOrder(saleOrder);
						saleOrderService.upsertAllocationHistory(allocationHistory);
					}else {
						logger.warn("Invalid or Packed sale order allocation for saleOrder: " + saleOrder.getEmsOrderItemId());
					}
				}
				logger.info("Setting saleOrder: " + saleOrder.getEmsOrderItemId() + " allocated quantity to 0" + " : " + uuid);
				saleOrder.setAllocatedQuantity(0.0d);
			}else {
				logger.warn("Product Inventory Found but no allocations for saleOrder: " +saleOrder.getEmsOrderItemId());
			}
		}
		logger.info("Inventory de-allocation service ended" + " : " + uuid);
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
	@Transactional
	public void allocateInventory(SaleOrder saleOrder) {
		String uuid = UUID.randomUUID().toString();

		logger.info("Allocating inventory for sale order id: " + saleOrder.getId() + "and warehouseId" + saleOrder.getWarehouse().getId());
		ProductInventory productInventory = productInventoryService
				.getByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
		
		logger.info("Getting Blocked quantity for product msn: " + saleOrder.getProduct().getProductMsn()
				+ " and warehouse: " + saleOrder.getWarehouse().getId());
		
		BlockedProductInventory blockedProductInventory = blockedProductInventoryRepo.findByWarehouseIdAndProductMsnAndUniqueID(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getProductMsn(),saleOrder.getBulkInvoiceId());	
		List<BlockedProductInventory> blockedProductInventoryList = blockedProductInventoryRepo.findByWarehouseIdAndProductMsn(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getProductMsn());
		
		ProductInventoryConfig inventoryConfig = productInventoryConfigRepository.findByProductMsnAndWarehouseId(saleOrder.getProduct().getProductMsn(), saleOrder.getWarehouse().getId());
		
		//TODO: double initialisation check
		double blockedQuantity = 0.0d;
		Double expiredQty = 0.0d;
		if(blockedProductInventoryList != null && blockedProductInventoryList.size()>0) {
			blockedQuantity = blockedProductInventoryRepo.findtotalblockedquantity(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getProductMsn());
		}

		if(saleOrder.getProduct().getExpiryDateManagementEnabled() != null && saleOrder.getProduct().getExpiryDateManagementEnabled()) {
			expiredQty = inboundStorageRepo.getTotalExpiredInventoryByWarehouseIdAndProductId(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
			expiredQty = expiredQty != null ? expiredQty: 0.0d;
		}
		//Extract This method in a separate method
		if (productInventory == null || (saleOrder.getBulkInvoiceId() != null && blockedQuantity <= 0)
				|| (saleOrder.getBulkInvoiceId() == null
						&& productInventory.getAvailableQuantity() - expiredQty - blockedQuantity <= 0)) {
			logger.info("Inventory not available for sale order id: " + saleOrder.getId());
		}else if (saleOrder.getAllocatedQuantity()
				.equals(saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity())) {
			logger.info("Ordered quantity has been already allocated for sale order id: " + saleOrder.getId());
		} else if(saleOrder.getOrderedQuantity() < saleOrder.getPackedQuantity() ){
			logger.info("Invalid Quantity. Ordered Quantity less than packed quantity for emsOrderItemId: " + saleOrder.getEmsOrderItemId());
		}else {
			logger.debug("saleOrder orderedQuantity: " + saleOrder.getOrderedQuantity() + " saleOrder Packed Quantity: "
					+ saleOrder.getPackedQuantity() + " saleOrder pre-allocated quantity: "
					+ saleOrder.getAllocatedQuantity() + " for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
			
			Double allocatedQty = 0.0d;
			
			if(saleOrder.getBulkInvoiceId() == null) {
				allocatedQty = Math.min(productInventory.getAvailableQuantity() - expiredQty -  blockedQuantity, (saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity()- saleOrder.getAllocatedQuantity()));
			}else {
				allocatedQty = Math.min(blockedQuantity - expiredQty, (saleOrder.getOrderedQuantity() - saleOrder.getPackedQuantity()- saleOrder.getAllocatedQuantity()));
			}
			
			List<InboundStorage> inboundStorages;
			
			if(saleOrder.getProduct().getExpiryDateManagementEnabled() != null && saleOrder.getProduct().getExpiryDateManagementEnabled()) {
				inboundStorages = inboundStorageRepo.findByProductAndWarehouseForAvailableBasedOnExpiry(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
			}else {
				inboundStorages = inboundStorageRepo.findByProductAndWarehouseForAvailable(saleOrder.getWarehouse().getId(), saleOrder.getProduct().getId());
			}
			
			logger.debug("Quantity to be allocated to saleOrder " + saleOrder.getEmsOrderItemId() + ": " + allocatedQty + " : " + uuid);
			
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
				
				logger.debug("setting Available and allocated quantity to: " + (inboundStorageAvailableQty >= currentAllocation ? currentAllocation
						: inboundStorageAvailableQty) + " : " + uuid);
				allocation.setAllocatedQuantity(inboundStorageAvailableQty >= currentAllocation ? currentAllocation
						: inboundStorageAvailableQty);
				allocation.setAvailableQuantity(allocation.getAllocatedQuantity());
				saleOrderService.upsertAllocation(allocation);

				allocationHistory = new SaleOrderAllocationHistory();
				allocationHistory.setAction("Allocated");
				allocationHistory.setQuantity(allocation.getAllocatedQuantity());
				allocationHistory.setSaleOrder(saleOrder);
				saleOrderService.upsertAllocationHistory(allocationHistory);

				
				logger.debug("Setting new storage available quantity: " + 
						NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);
				inboundStorage.setAvailableQuantity(
						NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()));
				
				
				logger.debug("Setting new storage allocated quantity: " + 
						NumberUtil.round4(inboundStorageAvailableQty - allocation.getAllocatedQuantity()) + " : " + uuid);
				inboundStorage.setAllocatedQuantity(
						NumberUtil.round4(inboundStorage.getAllocatedQuantity() + allocation.getAllocatedQuantity()));

				logger.trace("Updating inbound storage: " + inboundStorage.getId() + " : " + uuid);
				inboundStorageService.upsert(inboundStorage);

				currentAllocation = NumberUtil.round4(currentAllocation - allocation.getAllocatedQuantity());
				if (currentAllocation == 0) {
					logger.trace("Allocation Completed for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
					break;
				}
			}

			logger.debug("Setting saleOrder allocated quantity for saleOrder: " + saleOrder.getEmsOrderItemId() + " : "
					+ saleOrder.getAllocatedQuantity() + allocatedQty + " : " + uuid);
			saleOrder.setAllocatedQuantity(saleOrder.getAllocatedQuantity() + allocatedQty);
			
			logger.trace("Updating Sale Order Info for saleOrder: " + saleOrder.getEmsOrderItemId() + " : " + uuid);
			saleOrderService.upsert(saleOrder);

			
			logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Allocated Quantity: " + NumberUtil.round4(productInventory.getAllocatedQuantity() + allocatedQty) + " : " + uuid);
			productInventory
					.setAllocatedQuantity(NumberUtil.round4(productInventory.getAllocatedQuantity() + allocatedQty));
			
			logger.debug("Setting new product Inventory for inventoryId: " + productInventory.getId() + " Available Quantity: " + NumberUtil.round4(productInventory.getAvailableQuantity() - allocatedQty) + " : " + uuid);
			productInventory
					.setAvailableQuantity(NumberUtil.round4(productInventory.getAvailableQuantity() - allocatedQty));
			
			logger.trace("Updating product inventory with inventoryId: " + productInventory.getId() + " : " + uuid);
			productInventoryService.upsert(productInventory);
			
			if (inventoryConfig != null && productInventory
					.getAvailableQuantity() <= inventoryConfig.getMinimumQuantity()) {
				// Handle available quantity < configured minimum quantity
				try {
					String mailContent = Constants.getInventoryEmailContent(
							productInventory.getProduct().getProductMsn(),
							productInventory.getProduct().getProductName(), productInventory.getWarehouse().getName());
					mailUtil.sendMail(mailContent, Constants.INVENTORY_MAIL_SUBJECT,
							productInventory.getWarehouse().getId());
				} catch (Exception e) {
					logger.error("Error Occured in sending mail for VMI inventory for product: "
							+ productInventory.getProduct().getProductMsn() + " and warehouse: "
							+ productInventory.getWarehouse().getName(), e);
				}
			}

			Product product = productInventory.getProduct();
			
			logger.debug("Product Id: " + product.getId() + " Updated Allocated Quantity: " + NumberUtil.round4(product.getAllocatedQuantity() + allocatedQty) + " : " + uuid);
			product.setAllocatedQuantity(NumberUtil.round4(product.getAllocatedQuantity() + allocatedQty));
			
			logger.debug("Product Id: " + product.getId() + " Updated Available Quantity: " + NumberUtil.round4(product.getAvailableQuantity() - allocatedQty) + " : " + uuid);
			product.setAvailableQuantity(NumberUtil.round4(product.getAvailableQuantity() - allocatedQty));
			
			logger.trace("Updating Product Entry for product: " + product.getId() + " : " + uuid);
			productService.upsert(product);

			if (blockedProductInventory != null && !StringUtils.isEmpty(saleOrder.getBulkInvoiceId())) {
				logger.debug("Setting new Blocked Inventory for inventoryId: " + blockedProductInventory.getId()
						+ " Available Quantity: "
						+ NumberUtil.round4(blockedProductInventory.getBlockedQuantity() - allocatedQty) + " : "
						+ uuid);
				blockedProductInventory.setBlockedQuantity(
						NumberUtil.round4(blockedProductInventory.getBlockedQuantity() - allocatedQty));
				blockedProductInventoryRepo.save(blockedProductInventory);
			}

			logger.info(
					"Inventory allocated to sale order id: " + saleOrder.getId() + ", allocatedQty: " + allocatedQty);
		}
	}

	@Override
	public BaseResponse deductAvailableInventoryWarehouseWise(Integer warehouseId, ProductInventory prodInventory,
			Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean allocateInventoryForSaleOrder(Integer saleOrderId) throws InterruptedException {
		return false;
	}

	@Override
	public boolean allocateInventoryForProductId(Integer productId) {
		return false;
	}

}
