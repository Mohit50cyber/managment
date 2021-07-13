package com.moglix.wms.task;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.constants.InboundStorageType;
import com.moglix.wms.dto.BadInventory;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.ProductInventoryRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.service.IInventoryService;

@Component
public class ShelfLifeExpiryDateTask {

	private Logger logger = LogManager.getLogger(ShelfLifeExpiryDateTask.class);
	
	@Autowired
	private ProductsRepository prodRepo;
	
	@Autowired
	private InboundStorageRepository inboundStorageRepo;
	
	@Autowired
	@Qualifier("inventoryService")
	private IInventoryService inventoryService;
	
	@Scheduled(cron = "0 4 0 * * ?")
	@Transactional
	public void updateInventoryStateBasedOnShelfLife() {
		logger.info("Task to remove expired inventory Started");
		List<BadInventory>expiredInventory = prodRepo.findExpiredInventory();
		
		logger.info("Found: " + expiredInventory.size() + " expired inventories");
		
		for(BadInventory badInventory: expiredInventory) {
			logger.info("Process bad inventory with productId: " + badInventory.getProductId() + " and warehouseId: "
					+ badInventory.getWarehouseId() + " and expiryDate: " + badInventory.getExpiryDate()
					+ " and shelf life: " + badInventory.getShelfLife() + " and creation date: "
					+ badInventory.getCreated() + " and inbound storage id: " + badInventory.getId());
			
			InboundStorage inboundStorage = inboundStorageRepo.findById(badInventory.getId()).orElse(null);
			
			if(inboundStorage != null) {
				logger.info("Found inbound storage for id: " + badInventory.getId());
				inboundStorage.setConfirmed(false);
				inboundStorage.setType(InboundStorageType.EXPIRED);
				logger.info("Updated values of type to: " + InboundStorageType.EXPIRED + " and confirmed to: " + false);
				inboundStorageRepo.save(inboundStorage);
			}
			
			inventoryService.deductAvailableInventory(badInventory.getWarehouseId(), badInventory.getProductId(), badInventory.getAvailableQuantity());
		}
		
		logger.info("Task to remove expired inventory Ended");
	}

}
