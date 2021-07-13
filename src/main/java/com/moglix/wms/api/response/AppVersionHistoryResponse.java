package com.moglix.wms.api.response;

import com.moglix.wms.api.request.StockTransferNoteSearchRequest;
import com.moglix.wms.entities.AppVersionHistory;

import lombok.Data;

@Data
public class AppVersionHistoryResponse extends BaseResponse {
	
	private static final long serialVersionUID = -1487206790817781515L;
	
	public AppVersionHistory appVersionHistory;
	
	public boolean status; 
}
