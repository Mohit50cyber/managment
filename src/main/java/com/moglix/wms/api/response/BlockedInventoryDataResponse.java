package com.moglix.wms.api.response;

import java.util.List;

import com.moglix.wms.dto.BlockedInventoryDTO;

public class BlockedInventoryDataResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1487206790817781515L;
	
	public BlockedInventoryDataResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	List<BlockedInventoryDTO> inventoryData;

	public List<BlockedInventoryDTO> getInventoryData() {
		return inventoryData;
	}

	public void setInventoryData(List<BlockedInventoryDTO> inventoryData) {
		this.inventoryData = inventoryData;
	}

}
