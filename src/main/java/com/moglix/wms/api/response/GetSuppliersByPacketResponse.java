package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ProductPacketResponseDTO;

public class GetSuppliersByPacketResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1684220593953312927L;

	public GetSuppliersByPacketResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
	
	List<ProductPacketResponseDTO.SupplierInfo>suppliers = new ArrayList<ProductPacketResponseDTO.SupplierInfo>();

	public List<ProductPacketResponseDTO.SupplierInfo> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<ProductPacketResponseDTO.SupplierInfo> suppliers) {
		this.suppliers = suppliers;
	}
	
	

}
