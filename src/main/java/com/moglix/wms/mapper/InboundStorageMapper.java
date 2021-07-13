package com.moglix.wms.mapper;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.moglix.wms.dto.InventoryDataResult;

@Service
public class InboundStorageMapper {
	
	public static List<String> createInventoryDataFromStorage(InventoryDataResult storage) {
		List<String> record = new ArrayList<>();
		
		record.add(storage.getWarehouseName());
		record.add(storage.getProductMsn());
		record.add(storage.getProductName());
		record.add(storage.getInventoryType().toString());
		record.add(storage.getZone());
		record.add(storage.getBin());
		record.add(String.valueOf(storage.getSupplierPoId()));
		record.add(String.valueOf(storage.getSupplierPoItemId()));
		record.add(String.valueOf(storage.getMrnDate()));
		record.add(String.valueOf(storage.getAllocatedQuantity()));
		record.add(String.valueOf(storage.getAvailableQuantity()));
		record.add(String.valueOf(storage.getTotalQuantity()));
		record.add(String.valueOf(storage.getPurchasePrice()));
		return record;
	}
}
