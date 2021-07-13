package com.moglix.wms.api.response;

import java.util.List;

import com.moglix.wms.dto.MSNListDTO;

import lombok.Data;

@Data
public class MSNListResponse extends BaseResponse{

	private static final long serialVersionUID = 4103130902883016708L;

	public MSNListResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
	
	private List<MSNListDTO> msnListDto;
	
}
