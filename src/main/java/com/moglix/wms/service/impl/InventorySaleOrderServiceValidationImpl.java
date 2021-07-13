package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.constants.SaleOrderStatus;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.repository.SaleOrderRepository;
import com.moglix.wms.service.IInventorySaleOrderStorageValidationService;
import com.moglix.wms.service.IProductInventoryService;
import com.moglix.wms.util.NumberUtil;

@Service
public class InventorySaleOrderServiceValidationImpl implements IInventorySaleOrderStorageValidationService{

	Logger logger = LogManager.getLogger(InventorySaleOrderServiceValidationImpl.class);
	
	@Autowired
	private IProductInventoryService productInventoryService;

	@Autowired
	private SaleOrderRepository saleOrderRepository;
	
	@Override
	public BaseResponse getInventorySaleOrderQuantityValidation() {
		logger.info("Cron for validation of inventory and inbound storage quantity match started");
        List<ProductInventory> productInventoryList = productInventoryService.getAll();
        
        SaleOrderStatus statuses[] = {SaleOrderStatus.OPEN, SaleOrderStatus.FULFILLED};
        
        List<SaleOrder> saleOrders = saleOrderRepository.findByStatusIn(Arrays.asList(statuses));
        
        Map<String, Double> saleOrderAllocatedQuantityMap = saleOrders.stream().collect(Collectors.groupingBy(so->getGroupingByKey(so), Collectors.summingDouble(so-> so.getAllocatedQuantity())));
        
		List<String> inventorySaleOrderMismatchList = new ArrayList<>();
		for (ProductInventory productinventory : productInventoryList) {

			try {
				if (saleOrderAllocatedQuantityMap
						.get(getGroupingByWarehouseAndProductId(productinventory)) != null && NumberUtil.round4(saleOrderAllocatedQuantityMap
						.get(getGroupingByWarehouseAndProductId(productinventory))) != (productinventory
								.getAllocatedQuantity().doubleValue())) {
					inventorySaleOrderMismatchList
							.add(productinventory.getProduct().getId() + "-" + productinventory.getWarehouse().getId());
				}
			} catch (Exception e) {
				logger.error("Error Occurred while processing productInventory: " + productinventory.getId(), e);
			}
		}
		if(!inventorySaleOrderMismatchList.isEmpty()) {
			logger.info("Mismatch found in sale order and productinventory. Total productInventories out of sink: " + inventorySaleOrderMismatchList.size());
			logger.info("Mismatch found in these ids: " + inventorySaleOrderMismatchList);
		}else {
			logger.info("No Mismatch found in sale order and productInventory");
		}
		return new BaseResponse("Validation task completed successfully", true, HttpStatus.OK.value());
	}
	
	private String getGroupingByKey(SaleOrder so) {
		return so.getProduct().getId() + "-" + so.getWarehouse().getId();
	}
    
    private String getGroupingByWarehouseAndProductId(ProductInventory p) {
    	return p.getProduct().getId()+"-"+p.getWarehouse().getId();
    }
}
