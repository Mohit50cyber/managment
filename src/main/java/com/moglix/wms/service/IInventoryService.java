package com.moglix.wms.service;

import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.Warehouse;

/**
 * @author pankaj on 6/5/19
 */
public interface IInventoryService {
    BaseResponse addInventory(Integer warehouseId, Integer productId,Double purchasePrice, Double qty);
    BaseResponse deductAllocatedInventory(Integer warehouseId, Integer productId, Double qty);
    void deAllocateInventory(SaleOrder saleOrder);
	BaseResponse deductAvailableInventory(Integer warehouseId, Integer productId, Double qty);
	BaseResponse addAvailableInventory(Integer warehouseId, Integer productId, Double qty);
	void transferAllocation(SaleOrder saleOrder, Double allocatedQuantity);
	boolean saveInventoryHistory(Warehouse warehouse, String productMsn, InventoryTransactionType transactionType,
			InventoryMovementType movementType, String transactionRef, Double prevQuantity, Double currQuantity);
	void allocateInventory(SaleOrder saleOrder);
	BaseResponse deductAvailableInventoryWarehouseWise(Integer warehouseId,  ProductInventory prodInventory,Product product);
	
	boolean allocateInventoryForSaleOrder(Integer saleOrderId) throws InterruptedException;
	boolean allocateInventoryForProductId(Integer productId);
}
