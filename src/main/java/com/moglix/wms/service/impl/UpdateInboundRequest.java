package com.moglix.wms.service.impl;

import javax.validation.constraints.NotNull;

import com.moglix.wms.api.request.BaseRequest;

public class UpdateInboundRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 359179541205553216L;
	
	@NotNull
	private Integer inboundId;
		
	public Integer getInboundId() {
		return inboundId;
	}

	public void setInboundId(Integer inboundId) {
		this.inboundId = inboundId;
	}

	private Double purchasePrice;
	
	private Double tax;

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@Override
	public String toString() {
		return "UpdateInboundRequest [purchasePrice=" + purchasePrice + ", tax=" + tax + "]";
	}
}

