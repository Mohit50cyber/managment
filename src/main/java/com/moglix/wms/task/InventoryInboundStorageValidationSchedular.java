package com.moglix.wms.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.moglix.wms.dto.EMSInventory;
import com.moglix.wms.dto.ProductInventoryImportCSVContentCSV;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.service.IProductInventoryService;

@Component
public class InventoryInboundStorageValidationSchedular {

	private Logger logger = LogManager.getLogger(InventoryInboundStorageValidationSchedular.class);

    @Autowired
    private IProductInventoryService productInventoryService;
    
    @Autowired
    private InboundStorageRepository inboundStorageRepository;

    //@Scheduled(fixedRate= 3600000)
    @Transactional
    public void validateInventoryInboundStorageQuantity() {
        logger.info("Cron for validation of inventory and inbound storage quantity match started");
        List<ProductInventory> productInventoryList = productInventoryService.getAll();
        
        
        
        List<InboundStorage> inboundStorageList = inboundStorageRepository.findByConfirmedTrue();
        
		        
        Map<String, Double> productWarehouseInboundStorageMapAvailableQ = inboundStorageList.stream() .collect(Collectors.groupingBy(ins->getGroupingByKey(ins), Collectors.summingDouble(ins->ins.getAvailableQuantity())));
        
        Map<String, Double> productWarehouseInboundStorageMapAllocatedQ = inboundStorageList.stream() .collect(Collectors.groupingBy(ins->getGroupingByKey(ins), Collectors.summingDouble(ins->ins.getAllocatedQuantity())));
        
        Map<String, Double> productWarehouseInboundStorageMapQuantity = inboundStorageList.stream() .collect(Collectors.groupingBy(ins->getGroupingByKey(ins), Collectors.summingDouble(ins->ins.getQuantity())));
      
        List<String> productIdListAvailableQMismatch = new ArrayList<>();
        List<String> productIdListAllocatedQMismatch = new ArrayList<>();
        List<String> productIdListCurrentQMismatch = new ArrayList<>();
        for(ProductInventory productinventory : productInventoryList) {
        	
        	if(!productWarehouseInboundStorageMapAvailableQ.get(getGroupingByWarehouseAndProductId(productinventory)).equals(productinventory.getAvailableQuantity()) ){
        		productIdListAvailableQMismatch.add(productinventory.getProduct().getId()+"-"+productinventory.getWarehouse().getId());
        		}
        	if(!productWarehouseInboundStorageMapAllocatedQ.get(getGroupingByWarehouseAndProductId(productinventory)).equals(productinventory.getAllocatedQuantity()) ){
        		productIdListAllocatedQMismatch.add(productinventory.getProduct().getId()+"-"+productinventory.getWarehouse().getId());
    		}
        	
        	if(!productWarehouseInboundStorageMapQuantity.get(getGroupingByWarehouseAndProductId(productinventory)).equals(productinventory.getCurrentQuantity()) ){
        		productIdListCurrentQMismatch.add(productinventory.getProduct().getId()+"-"+productinventory.getWarehouse().getId());
    		}
        }
        logger.info("poduct inventory Available Quantity mismatch  size :"+productIdListAvailableQMismatch.size());
        logger.info("poduct inventory Available Quantity mismatch in these ids:" + productIdListAvailableQMismatch);

        
        logger.info("poduct inventory Allocated Quantity mismatch  size :"+productIdListAllocatedQMismatch.size());
        logger.info("poduct inventory Allocated Quantity mismatch in these ids:" + productIdListAllocatedQMismatch);
      
        logger.info("poduct inventory current Quantity mismatch  size :"+productIdListCurrentQMismatch.size());
        logger.info("poduct inventory current  Quantity mismatch in these ids:" + productIdListCurrentQMismatch);
         
        logger.info("Cron for validation of inventory and inbound storage quantity match ended");

    }
    
    private String getGroupingByKey(InboundStorage ins) {
		return ins.getProduct().getId() + "-" + ins.getStorageLocation().getWarehouse().getId();
	}
    
    private String getGroupingByWarehouseAndProductId(ProductInventory p) {
    	return p.getProduct().getId()+"-"+p.getWarehouse().getId();
    }
}
