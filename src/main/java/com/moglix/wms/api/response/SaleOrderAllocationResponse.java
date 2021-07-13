package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderAllocationResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3970205608227855313L;
	
	public SaleOrderAllocationResponse(String message, boolean status, int code) {
		super.setCode(code);
		super.setMessage(message);
		super.setStatus(status);
	}
	private List<Allocation> allocations = new ArrayList<>();

	public List<Allocation> getAllocations() {
		return allocations;
	}
	public void setAllocations(List<Allocation> allocations) {
		this.allocations = allocations;
	}
	@Override
	public String toString() {
		return "SaleOrderAllocationResponse [allocations=" + allocations + "]";
	}
}
