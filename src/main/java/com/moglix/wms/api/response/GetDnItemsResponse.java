package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.DnDetailItemDTO;
import com.moglix.wms.entities.ReturnPickupList;


public class GetDnItemsResponse extends BaseResponse{
	
	private static final long serialVersionUID = 2101109423573157701L;
	
	public GetDnItemsResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}	

	public GetDnItemsResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	private List<DnDetailItemDTO> dnItemDetails;
	
	private Double totalQuantity;
	
	private String productMSN;
	
	private String productDesc;
	

	public List<DnDetailItemDTO> getDnItemDetails() {
		return dnItemDetails;
	}

	public void setDnItemDetails(List<DnDetailItemDTO> dnItemDetails) {
		this.dnItemDetails = dnItemDetails;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public String getProductMSN() {
		return productMSN;
	}

	public void setProductMSN(String productMSN) {
		this.productMSN = productMSN;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	

	
}
