package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.validator.CheckValidWarehouse;

public class FakeReturnBatchRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 342538679676518726L;
	
	@NotNull
	private Integer emsPacketId;
	
	@NotNull
	private Integer emsReturnId;
	
	@NotNull
	private String invoiceNumber;
	
	@NotNull
	private String customerName;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@NotNull
	@Size(min = 1)
	private List<FakeReturnBatchMapping> mapping;

	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public List<FakeReturnBatchMapping> getMapping() {
		return mapping;
	}

	public void setMapping(List<FakeReturnBatchMapping> mapping) {
		this.mapping = mapping;
	}
}
