package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class EMSPackableQuantityRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4623399981991600577L;

 
	
	public EMSPackableQuantityRequest(@NotNull Integer emsOrderItemId, @NotNull Double packableQuanity,
			@NotNull String source) {
		super();
		this.emsOrderItemId = emsOrderItemId;
		this.packableQuanity = packableQuanity;
		this.source = source;
	}

	@NotNull
	private Integer emsOrderItemId;
	
	@NotNull
	private Double packableQuanity;
	
	@NotNull
	private String source;

	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

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
}
