package com.moglix.wms.api.request;

public class GetProductByPacketAndSupplierRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4190338208235247303L;
	
	private Integer emsPacketId;
	
	private Integer supplierId;

	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
}
