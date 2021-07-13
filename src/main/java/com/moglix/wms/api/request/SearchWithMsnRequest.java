package com.moglix.wms.api.request;

import java.util.List;

public class SearchWithMsnRequest extends BaseRequest {

	/**
	*
	*/
	private static final long serialVersionUID = -1498250814394668288L;

	private Integer storageLocationId;
	private Integer warehouseId;
	private List<String> productMsnList;

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public List<String> getProductMsnList() {
		return productMsnList;
	}

	public void setProductMsnList(List<String> productMsnList) {
		this.productMsnList = productMsnList;
	}

	public Integer getStorageLocationId() {
		return storageLocationId;
	}

	public void setStorageLocationId(Integer storageLocationId) {
		this.storageLocationId = storageLocationId;
	}

	@Override
	public String toString() {
		return "SearchWithMsnRequest [storageLocationId=" + storageLocationId + ", warehouseId=" + warehouseId
				+ ", productMsnList=" + productMsnList + "]";
	}

}
