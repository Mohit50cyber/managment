package com.moglix.wms.dto;

import java.io.Serializable;

import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.entities.ReturnPacket;
import com.moglix.wms.util.DateUtil;

public class ReturnPacketDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1573870191896084226L;
	
	private Integer id;
	
	private Integer emsPacketId;
	
	private PacketStatus status;
		
	private Integer warehouseId;
	
	private String warehouseName;
	
	private String invoiceNumber;
	
	private String created;
	
	private String customerName;
	
	private Integer emsReturnId;
	
	public ReturnPacketDTO(ReturnPacket packet) {
		this.id = packet.getId();
		this.emsPacketId = packet.getEmsPacketId();
		this.status = packet.getStatus();
		this.warehouseId = packet.getWarehouse().getId();
		this.warehouseName = packet.getWarehouse().getName();
		this.created = DateUtil.convertDateToString(packet.getCreated(), null);	
		this.customerName = packet.getCustomerName();
		this.invoiceNumber = packet.getInvoiceNumber();
		this.emsReturnId = packet.getEmsReturnId();
				
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}

	public PacketStatus getStatus() {
		return status;
	}

	public void setStatus(PacketStatus status) {
		this.status = status;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}
}
