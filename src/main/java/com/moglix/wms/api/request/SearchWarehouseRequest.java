package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 30/4/19
 */
public class SearchWarehouseRequest extends BaseRequest {
    
	private static final long serialVersionUID = -3598136553646879899L;
   
	@NotNull
    private Integer isoNumber;
	
	public Integer getIsoNumber() {
		return isoNumber;
	}

	public void setIsoNumber(Integer isoNumber) {
		this.isoNumber = isoNumber;
	}

	@Override
	public String toString() {
		return "SearchWarehouseRequest [isoNumber=" + isoNumber + "]";
	}
}
