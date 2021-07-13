package com.moglix.wms.api.request;

public class CheckDebitnoteCreationValidityRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5684832522536949113L;

	private Integer emsReturnId;
	
	private Integer supplierId;

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Override
	public String toString() {
		return "CheckDebitnoteCreationValidityRequest [emsReturnId=" + emsReturnId + ", supplierId=" + supplierId + "]";
	}
}
