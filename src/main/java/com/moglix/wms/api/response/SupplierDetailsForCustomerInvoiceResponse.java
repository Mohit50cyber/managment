package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ProductPacketResponseDTO;
import com.moglix.wms.dto.ProductPacketResponseDTOIMS;

public class SupplierDetailsForCustomerInvoiceResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -145793594605765481L;
	
	private List<ProductPacketResponseDTOIMS> productPacketResponses = new ArrayList<>();

	
	public SupplierDetailsForCustomerInvoiceResponse() {
		super();
	}


	public SupplierDetailsForCustomerInvoiceResponse(String message, boolean status, int code) {
		super(message, status, code);
	}


	public SupplierDetailsForCustomerInvoiceResponse(List<ProductPacketResponseDTOIMS> productPacketResponses) {
		super();
		this.productPacketResponses = productPacketResponses;
	}


	public List<ProductPacketResponseDTOIMS> getProductPacketResponses() {
		return productPacketResponses;
	}


	public void setProductPacketResponses(List<ProductPacketResponseDTOIMS> productPacketResponses) {
		this.productPacketResponses = productPacketResponses;
	}
	
	
	
	

}
