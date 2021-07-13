package com.moglix.wms.api.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.api.request.ReturnBatchRequest.PacketQuantityMapping;

public class CreateDebitNoteRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -697553028830874342L;
	
	@NotNull
	private Integer emsPacketId;
	
	@NotBlank
	private String debitNoteNumber;
	
	private String supplierName;
	
	@NotNull
	private Integer supplierId;
	
	@NotNull
	@Size(min  = 1)
	private List<PacketQuantityMapping> packetQuantityMapping;
	
	public Integer getEmsPacketId() {
		return emsPacketId;
	}
	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}
	public String getDebitNoteNumber() {
		return debitNoteNumber;
	}
	public void setDebitNoteNumber(String debitNoteNumber) {
		this.debitNoteNumber = debitNoteNumber;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	public List<PacketQuantityMapping> getPacketQuantityMapping() {
		return packetQuantityMapping;
	}
	public void setPacketQuantityMapping(List<PacketQuantityMapping> packetQuantityMapping) {
		this.packetQuantityMapping = packetQuantityMapping;
	}
	
	
}
