package com.moglix.wms.util;

import org.springframework.http.HttpStatus;

import com.moglix.wms.api.response.BaseResponse;

public class GenericUtils {
	
	private GenericUtils() {
		
	}
	
	public static BaseResponse getResponseMessage(String message, boolean status, HttpStatus code) {
		return new BaseResponse(message, status, code.value());
	}
}