package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class GetProductByReturnPacketAndSupplierRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1170149427657540513L;
	
	@NotNull
	private Integer emsReturnId;
	
	@NotNull
	private Integer supplierId;
	
	@NotNull
	private Integer supplierPoId;

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

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}

}
