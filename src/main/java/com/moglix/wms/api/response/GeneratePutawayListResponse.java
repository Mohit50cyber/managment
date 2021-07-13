package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.PutawayListItemsDTO;

public class GeneratePutawayListResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6051315147849833860L;

	private List<PutawayListItemsDTO> putawayList = new ArrayList<>();
	
	private String generatedBy;
	
	private String assignedTo;

	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public List<PutawayListItemsDTO> getPutawayList() {
		return putawayList;
	}

	public void setPutawayList(List<PutawayListItemsDTO> putawayList) {
		this.putawayList = putawayList;
	}

	
}
