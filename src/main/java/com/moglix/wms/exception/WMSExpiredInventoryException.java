package com.moglix.wms.exception;

import java.io.Serializable;

public class WMSExpiredInventoryException extends RuntimeException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4717245287279539767L;

	public WMSExpiredInventoryException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public WMSExpiredInventoryException(String message) {
		this(message, null);
	}
}
