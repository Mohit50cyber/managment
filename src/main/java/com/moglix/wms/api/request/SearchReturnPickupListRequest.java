package com.moglix.wms.api.request;

/**
 * @author pankaj on 20/5/19
 */
public class SearchReturnPickupListRequest extends BaseRequest {
    private static final long serialVersionUID = 5221039080485685342L;

    private String searchKey;
    private Integer warehouseId;

    public SearchReturnPickupListRequest() {
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	@Override
	public String toString() {
		return "SearchReturnPickupListRequest [searchKey=" + searchKey + ", warehouseId=" + warehouseId + "]";
	}
	
	
}
