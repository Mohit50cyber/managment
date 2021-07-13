package com.moglix.wms.api.response;

import java.util.List;

import com.moglix.wms.dto.DnDataDTO;
import com.moglix.wms.dto.InventoryDataDTO;

public class InventoryDataResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5670028243981426022L;
	
	public InventoryDataResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	List<InventoryDataDTO> inventoryData;
	
	List<DnDataDTO> dnData;

	public List<InventoryDataDTO> getInventoryData() {
		return inventoryData;
	}

	public void setInventoryData(List<InventoryDataDTO> inventoryData) {
		this.inventoryData = inventoryData;
	}

	public List<DnDataDTO> getDnData() {
		return dnData;
	}

	public void setDnData(List<DnDataDTO> dnData) {
		this.dnData = dnData;
	}
	
	
}
