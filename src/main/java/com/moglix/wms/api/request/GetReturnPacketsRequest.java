package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

import com.moglix.wms.constants.PacketStatus;

public class GetReturnPacketsRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4415241370853423313L;

	private Integer warehouseId;
	
	private String searchKey;
	
	@NotNull
	private PacketStatus status;

	public String getSearchKey() {
		return searchKey;
	}

	public PacketStatus getStatus() {
		return status;
	}

	public void setStatus(PacketStatus status) {
		this.status = status;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
}
