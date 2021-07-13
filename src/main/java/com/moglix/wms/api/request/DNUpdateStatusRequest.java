package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class DNUpdateStatusRequest {

	
	@NotNull
	private Integer returnNoteId;
	
	@NotNull
	private String debitNoteNumber;
	
	@NotNull
	private String status;

	public Integer getReturnNoteId() {
		return returnNoteId;
	}

	public void setReturnNoteId(Integer returnNoteId) {
		this.returnNoteId = returnNoteId;
	}

	public String getDebitNoteNumber() {
		return debitNoteNumber;
	}

	public void setDebitNoteNumber(String debitNoteNumber) {
		this.debitNoteNumber = debitNoteNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DNUpdateStatusRequest [returnNoteId=" + returnNoteId + ", debitNoteNumber=" + debitNoteNumber
				+ ", status=" + status + "]";
	}

	

	
	
	
	
}
