package com.moglix.wms.dto;

import java.io.Serializable;

/**
 * @author moglix
 *
 */
public class InvoiceDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1068928760290233189L;

	private String invoiceNumber;
	
	private Integer warehouseId;

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Override
	public String toString() {
		return "InvoiceDetail [invoiceNumber=" + invoiceNumber + ", warehouseId=" + warehouseId + "]";
	}
}
