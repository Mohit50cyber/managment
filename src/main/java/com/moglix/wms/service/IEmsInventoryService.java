package com.moglix.wms.service;

import java.util.List;

import com.moglix.wms.dto.EMSInventory;
import com.moglix.wms.dto.EMSReturnInventory;

public interface IEmsInventoryService {
	public List<EMSInventory> getProductMrnInventory();
	public List<EMSInventory> getProductMrnInventoryCSV();

	List<EMSReturnInventory> getProductReturnInventory();
}
