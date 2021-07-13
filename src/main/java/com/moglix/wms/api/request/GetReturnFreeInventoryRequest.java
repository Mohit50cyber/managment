package com.moglix.wms.api.request;

public class GetReturnFreeInventoryRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6361131917436270564L;

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
		return "GetReturnFreeInventoryRequest [emsReturnId=" + emsReturnId + ", supplierId=" + supplierId + "]";
	}
}
