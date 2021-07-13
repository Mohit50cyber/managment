package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;

public class GetInboundRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2198588803289228175L;

	@NotNull
	private Integer warehouseId;
	
	private List<InboundType> status;
	
	private String searchKey;
	
	private List<InboundStatusType> action;

	public List<InboundStatusType> getAction() {
		return action;
	}

	public void setAction(List<InboundStatusType> action) {
		this.action = action;
	}

	public List<InboundType> getStatus() {
		return status;
	}

	public void setStatus(List<InboundType> status) {
		this.status = status;
	}

	
	public String getSearchKey() {
		return searchKey;
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
