package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.FreeInventoryData;

public class GetAvailableInventoryResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9065589669944600993L;
	
	public GetAvailableInventoryResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	List<FreeInventoryData> inventory = new ArrayList<>();

	public List<FreeInventoryData> getInventory() {
		return inventory;
	}

	public void setInventory(List<FreeInventoryData> inventory) {
		this.inventory = inventory;
	}
}
