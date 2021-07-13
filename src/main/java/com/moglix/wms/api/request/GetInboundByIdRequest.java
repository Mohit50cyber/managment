package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class GetInboundByIdRequest extends BaseRequest{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3366858257032953076L;
	
	@NotNull
	Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	} 
	
}
