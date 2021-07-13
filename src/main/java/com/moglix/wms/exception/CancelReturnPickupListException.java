package com.moglix.wms.exception;

import java.io.Serializable;

public class CancelReturnPickupListException extends RuntimeException implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3611953347106629636L;
	
	public CancelReturnPickupListException(String message, Throwable e) {
		super(message, e);
	}

	public CancelReturnPickupListException(String message) {
		this(message, null);
	}
}
