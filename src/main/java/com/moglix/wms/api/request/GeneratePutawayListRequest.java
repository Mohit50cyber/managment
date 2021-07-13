package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class GeneratePutawayListRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8191583479082613579L;
	
	@NotBlank
	private String assignedTo;
	
	@NotBlank
	private String generatedBy;

	@NotNull
	@Size(min = 1)
	private List<Integer>inboundIds;

	public List<Integer> getInboundIds() {
		return inboundIds;
	}

	public void setInboundIds(List<Integer> inboundIds) {
		this.inboundIds = inboundIds;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

}
