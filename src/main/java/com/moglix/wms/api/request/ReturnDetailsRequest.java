package com.moglix.wms.api.request;

public class ReturnDetailsRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4542945449702183459L;

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
		return "ReturnDetailsRequest [emsReturnId=" + emsReturnId + ", supplierId=" + supplierId + "]";
	}
}
