package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.api.response.InventoryInboundStorageUpdationResponse;
import com.moglix.wms.api.response.InventoryInboundStorageValidationResponse;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.service.IInventoryInboundStorageValidationService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.util.NumberUtil;

@Service("inventoryInboundValidationService")
public class InventoryInboundStorageValidationServiceImpl implements IInventoryInboundStorageValidationService {

	private Logger logger = LogManager.getLogger(InventoryInboundStorageValidationServiceImpl.class);

    @Autowired
    private IProductInventoryService productInventoryService;
    
    @Autowired
    private InboundStorageRepository inboundStorageRepository;

	@Override
	public InventoryInboundStorageValidationResponse getInventoryInboundQuantityValidation() {

		  logger.info("Cron for validation of inventory and inbound storage quantity match started");
	        List<ProductInventory> productInventoryList = productInventoryService.getAll();
	        
	        
	        
	        List<InboundStorage> inboundStorageList = inboundStorageRepository.findConfirmedGoodBins();
	        
			        
	        Map<String, Double> productWarehouseInboundStorageMapAvailableQ = inboundStorageList.stream().collect(Collectors.groupingBy(ins->getGroupingByKey(ins), Collectors.summingDouble(ins->ins.getAvailableQuantity())));
	        
	        Map<String, Double> productWarehouseInboundStorageMapAllocatedQ = inboundStorageList.stream() .collect(Collectors.groupingBy(ins->getGroupingByKey(ins), Collectors.summingDouble(ins->ins.getAllocatedQuantity())));
	        
	        Map<String, Double> productWarehouseInboundStorageMapQuantity = inboundStorageList.stream() .collect(Collectors.groupingBy(ins->getGroupingByKey(ins), Collectors.summingDouble(ins->ins.getQuantity())));
	      
	        List<String> productIdListAvailableQMismatch = new ArrayList<>();
	        List<String> productIdListAllocatedQMismatch = new ArrayList<>();
	        List<String> productIdListCurrentQMismatch = new ArrayList<>();
		for (ProductInventory productinventory : productInventoryList) {
			try {
				if (NumberUtil.round4(productWarehouseInboundStorageMapAvailableQ
						.get(getGroupingByWarehouseAndProductId(productinventory))) != (productinventory
								.getAvailableQuantity().doubleValue())) {
					productIdListAvailableQMismatch
							.add(productinventory.getProduct().getId() + "-" + productinventory.getWarehouse().getId());
				}
				if (NumberUtil
						.round4(productWarehouseInboundStorageMapAllocatedQ.get(getGroupingByWarehouseAndProductId(
								productinventory))) != (productinventory.getAllocatedQuantity()).doubleValue()) {
					productIdListAllocatedQMismatch
							.add(productinventory.getProduct().getId() + "-" + productinventory.getWarehouse().getId());
				}

				if (NumberUtil.round4(productWarehouseInboundStorageMapQuantity.get(getGroupingByWarehouseAndProductId(
						productinventory))) != (productinventory.getCurrentQuantity()).doubleValue()) {
					productIdListCurrentQMismatch
							.add(productinventory.getProduct().getId() + "-" + productinventory.getWarehouse().getId());
				}
			} catch (Exception e) {
				logger.error(
						"Error occured while updating quantities for productInventory: " + productinventory.getId(), e);
			}
		}
	        logger.info("poduct inventory Available Quantity mismatch  size :"+productIdListAvailableQMismatch.size());
	        logger.info("poduct inventory Available Quantity mismatch in these ids:" + productIdListAvailableQMismatch);

	        
	        logger.info("poduct inventory Allocated Quantity mismatch  size :"+productIdListAllocatedQMismatch.size());
	        logger.info("poduct inventory Allocated Quantity mismatch in these ids:" + productIdListAllocatedQMismatch);
	      
	        logger.info("poduct inventory current Quantity mismatch  size :"+productIdListCurrentQMismatch.size());
	        logger.info("poduct inventory current  Quantity mismatch in these ids:" + productIdListCurrentQMismatch);
	         
	        logger.info("Cron for validation of inventory and inbound storage quantity match ended");

	        return new InventoryInboundStorageValidationResponse("Inventory Inbound Storage validation completed", true, 200);
	    }
	
	

	private String getGroupingByKey(InboundStorage ins) {
		return ins.getProduct().getId() + "-" + ins.getStorageLocation().getWarehouse().getId();
	}
    
    private String getGroupingByWarehouseAndProductId(ProductInventory p) {
    	return p.getProduct().getId()+"-"+p.getWarehouse().getId();
    }



	@Override
	@Transactional
	public InventoryInboundStorageUpdationResponse updateInventoryInboundQuantity() {
		logger.info("Cron for updation of inventory and inbound storage quantity match started");
		List<ProductInventory> productInventoryList = productInventoryService.getAll();

		List<InboundStorage> inboundStorageList = inboundStorageRepository.findConfirmedGoodBins();

		Map<String, Double> productWarehouseInboundStorageMapAvailableQ = inboundStorageList.stream().collect(Collectors
				.groupingBy(ins -> getGroupingByKey(ins), Collectors.summingDouble(ins -> ins.getAvailableQuantity())));

		Map<String, Double> productWarehouseInboundStorageMapAllocatedQ = inboundStorageList.stream().collect(Collectors
				.groupingBy(ins -> getGroupingByKey(ins), Collectors.summingDouble(ins -> ins.getAllocatedQuantity())));

		Map<String, Double> productWarehouseInboundStorageMapQuantity = inboundStorageList.stream().collect(Collectors
				.groupingBy(ins -> getGroupingByKey(ins), Collectors.summingDouble(ins -> ins.getQuantity())));

		for (ProductInventory productinventory : productInventoryList) {
			try {
				logger.info("Updating inventory for ProductInventoryId: " + productinventory.getId());
				if (NumberUtil.round4(productWarehouseInboundStorageMapAvailableQ.get(getGroupingByWarehouseAndProductId(productinventory))) !=(productinventory.getAvailableQuantity().doubleValue())) {
					logger.trace("Inventory Available Quantity mismatch found for productInventory: " + productinventory.getId());
					logger.debug("Current ProductInventory Available Quantity: " + productinventory.getAvailableQuantity());
					
					logger.debug("Setting ProductInventory Available Quantity to: " + NumberUtil.round4(productWarehouseInboundStorageMapAvailableQ
							.get(getGroupingByWarehouseAndProductId(productinventory))));
					productinventory.setAvailableQuantity(NumberUtil.round4(productWarehouseInboundStorageMapAvailableQ
							.get(getGroupingByWarehouseAndProductId(productinventory))));
				}
				if (NumberUtil.round4(productWarehouseInboundStorageMapAllocatedQ.get(getGroupingByWarehouseAndProductId(productinventory))) !=(productinventory.getAllocatedQuantity()).doubleValue()) {
					logger.trace("Inventory Allocated Quantity mismatch found for productInventory: " + productinventory.getId());
					logger.debug(
							"Current ProductInventory Allocated Quantity: " + productinventory.getAllocatedQuantity());

					logger.debug("Setting ProductInventory Allocated Quantity to: "
							+ NumberUtil.round4(productWarehouseInboundStorageMapAllocatedQ
									.get(getGroupingByWarehouseAndProductId(productinventory))));
					productinventory.setAllocatedQuantity(NumberUtil.round4(productWarehouseInboundStorageMapAllocatedQ
							.get(getGroupingByWarehouseAndProductId(productinventory))));
				}

				if (NumberUtil.round4(productWarehouseInboundStorageMapQuantity.get(getGroupingByWarehouseAndProductId(productinventory))) != (productinventory.getCurrentQuantity()).doubleValue() ) {
					logger.trace("Inventory Current Quantity mismatch found for productInventory: " + productinventory.getId());
					logger.debug(
							"Current ProductInventory Current Quantity: " + productinventory.getCurrentQuantity());

					logger.debug("Setting ProductInventory Current Quantity to: "
							+ NumberUtil.round4(productWarehouseInboundStorageMapQuantity
									.get(getGroupingByWarehouseAndProductId(productinventory))));
					productinventory.setCurrentQuantity(NumberUtil.round4(productWarehouseInboundStorageMapQuantity
							.get(getGroupingByWarehouseAndProductId(productinventory))));
				}
				
				logger.trace("Updating Product Inventory with ID: " + productinventory.getId());
				productInventoryService.upsert(productinventory);
			}catch(Exception e) {
				logger.error("Error occured while updating quantities for productInventory: " + productinventory.getId(), e);
			}
		}

		logger.info("Cron for Updation of inventory and inbound storage quantity match ended");
		return new InventoryInboundStorageUpdationResponse("Inventory Inbound Storage updation completed", true, 200);
	}
	}

