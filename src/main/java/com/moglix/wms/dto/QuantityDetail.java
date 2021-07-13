package com.moglix.wms.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuantityDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8230719568725914336L;

	private String productMsn;

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	List<FreshAvailableQuantityDetail> details = new ArrayList<>();

	public List<FreshAvailableQuantityDetail> getDetails() {
		return details;
	}

	public void setDetails(List<FreshAvailableQuantityDetail> details) {
		this.details = details;
	}
}
