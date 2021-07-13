package com.moglix.wms.api.response;

import java.util.Set;

import com.moglix.wms.dto.SupplierCreditNoteDetailDTO;

public class GetProductByReturnPacketAndSupplierResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3886001396234607209L;
	
	public GetProductByReturnPacketAndSupplierResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
	
	private Set<SupplierCreditNoteDetailDTO> creditNoteDetails;

	public Set<SupplierCreditNoteDetailDTO> getCreditNoteDetails() {
		return creditNoteDetails;
	}

	public void setCreditNoteDetails(Set<SupplierCreditNoteDetailDTO> creditNoteDetails) {
		this.creditNoteDetails = creditNoteDetails;
	}

}
