package com.moglix.wms.api.response;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class BaseResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3009276540611029428L;
	private String message;
	private boolean status;
	@Override
	public String toString() {
		return "BaseResponse [message=" + message + ", status=" + status + ", code=" + code + "]";
	}
	private int code = HttpStatus.OK.value();

	public BaseResponse() {
	}

	public BaseResponse(String message, boolean status, int code) {
		this.message = message;
		this.status = status;
		this.code = code;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}