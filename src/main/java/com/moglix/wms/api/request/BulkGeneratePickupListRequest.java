package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.Size;

import com.moglix.wms.dto.InvoiceDetail;

public class BulkGeneratePickupListRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7320641591259631434L;
	
	@Size(min = 1)
	private List<InvoiceDetail> invoiceDetails;
	
	private String generatedBy = "bulkInvoicing";
	
	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public List<InvoiceDetail> getInvoiceDetails() {
		return invoiceDetails;
	}

	public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
	}

	@Override
	public String toString() {
		return "BulkGeneratePickupListRequest [invoiceDetails=" + invoiceDetails + ", generatedBy=" + generatedBy + "]";
	}
}
