package com.moglix.wms.api.request;

/**
 * @author pankaj on 15/5/19
 */
public class SearchProductInventoryRequest extends BaseRequest {
    private static final long serialVersionUID = 2101109423573157701L;

    private Integer warehouseId;

    private String productMsn;
    
    private Integer zoneId;
    
    private Integer binId;
    
    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getProductMsn() {
        return productMsn;
    }

    public void setProductMsn(String productMsn) {
        this.productMsn = productMsn;
    }
    
    public Integer getZoneId() {
		return zoneId;
	}

	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}

	public Integer getBinId() {
		return binId;
	}

	public void setBinId(Integer binId) {
		this.binId = binId;
	}

	@Override
	public String toString() {
		return "SearchProductInventoryRequest [warehouseId=" + warehouseId + ", productMsn=" + productMsn + ", zoneId="
				+ zoneId + ", binId=" + binId + "]";
	}
}
