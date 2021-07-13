package com.moglix.wms.api.response;

import com.moglix.wms.api.request.StockTransferNoteSearchRequest;
import com.moglix.wms.api.response.BaseResponse;

import lombok.Data;

@Data
public class FetchStorageResponse extends BaseResponse{
	
	private static final long serialVersionUID = -4969495960116602264L;

	public FetchStorageResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}	

	public FetchStorageResponse() {
		super();
	}

	String zone;
	
	Integer storageLocationId;

}
