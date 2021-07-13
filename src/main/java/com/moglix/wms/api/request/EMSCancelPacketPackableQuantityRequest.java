package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class EMSCancelPacketPackableQuantityRequest extends BaseRequest {

	public EMSCancelPacketPackableQuantityRequest(Integer emsOrderItemId, Double packableQuanity, String source, String invoiceNumber) {
		super();
		this.emsOrderItemId = emsOrderItemId;
		this.packableQuanity = packableQuanity;
		this.source = source;
		this.invoiceNumber = invoiceNumber;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4013750513355717203L;
	
	@NotNull
	private Integer emsOrderItemId;
	
	@NotNull
	private Double packableQuanity;
	
	@NotNull
	private String source; 
	
	@NotNull
	private String invoiceNumber;

	public Integer getEmsOrderItemId() {
		return emsOrderItemId;
	}

	public void setEmsOrderItemId(Integer emsOrderItemId) {
		this.emsOrderItemId = emsOrderItemId;
	}

	public Double getPackableQuanity() {
		return packableQuanity;
	}

	public void setPackableQuanity(Double packableQuanity) {
		this.packableQuanity = packableQuanity;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
}
