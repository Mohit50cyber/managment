package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ExpiredInventoryDTO;

public class ExpiredInventoryDataResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2014019337563874779L;

	public ExpiredInventoryDataResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	List<ExpiredInventoryDTO> expiredInventory = new ArrayList<>();

	public List<ExpiredInventoryDTO> getExpiredInventory() {
		return expiredInventory;
	}

	public void setExpiredInventory(List<ExpiredInventoryDTO> expiredInventory) {
		this.expiredInventory = expiredInventory;
	}
}
