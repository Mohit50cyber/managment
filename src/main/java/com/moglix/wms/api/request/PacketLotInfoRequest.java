package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PacketLotInfoRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1905183813455905825L;
	
	@NotNull
	@Size(min = 1)
	List<String>invoiceNumbers;

	public List<String> getInvoiceNumbers() {
		return invoiceNumbers;
	}

	public void setInvoiceNumbers(List<String> invoiceNumbers) {
		this.invoiceNumbers = invoiceNumbers;
	}

	@Override
	public String toString() {
		return "PacketLotInfoRequest [invoiceNumbers=" + invoiceNumbers + "]";
	}
}
