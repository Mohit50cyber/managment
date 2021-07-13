package com.moglix.wms.exception;

import java.io.Serializable;

public class WMSSecurtyException extends RuntimeException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2364489445848306912L;


	public WMSSecurtyException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public WMSSecurtyException(String message) {
		this(message, null);
	}
}
