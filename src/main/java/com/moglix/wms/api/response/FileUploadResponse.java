package com.moglix.wms.api.response;

public class FileUploadResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7778900999989012386L;
	
	private String filename;
	public FileUploadResponse(String message, boolean status, int code, String filename) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
		this.filename = filename;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
