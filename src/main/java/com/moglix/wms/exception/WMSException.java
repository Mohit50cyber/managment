package com.moglix.wms.exception;

import java.io.Serializable;

public class WMSException extends RuntimeException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7402506364408133690L;

	public WMSException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public WMSException(String message) {
		this(message, null);
	}
}
