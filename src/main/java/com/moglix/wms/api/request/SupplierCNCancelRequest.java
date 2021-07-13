package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

import com.moglix.wms.constants.BatchType;

public class SupplierCNCancelRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4977514893971733424L;

	@NotNull
	private String refNo;

	private BatchType batchType = BatchType.SUPPLIER_RETURN_CN;

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public BatchType getBatchType() {
		return batchType;
	}

	public void setBatchType(BatchType batchType) {
		this.batchType = batchType;
	}

	
	
}
