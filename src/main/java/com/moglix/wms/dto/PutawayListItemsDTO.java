package com.moglix.wms.dto;

import java.io.Serializable;

public class PutawayListItemsDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8194256127109700076L;
	private Integer id;
	private String supplierName;
	private Integer supplierId;
	private String productMsn;
	private String productName;
	private Double quantity;	
	private String uom;
	private String zoneId;
	private String rackId;
	private String binId;
	private String location;
	private String lotNumber;
	
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getProductMsn() {
		return productMsn;
	}
	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	public String getRackId() {
		return rackId;
	}
	public void setRackId(String rackId) {
		this.rackId = rackId;
	}
	public String getBinId() {
		return binId;
	}
	public void setBinId(String binId) {
		this.binId = binId;
	}
	
	public String getLotNumber() {
		return lotNumber;
	}
	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		return obj instanceof PutawayListItemsDTO
				&& ((PutawayListItemsDTO) obj).getProductMsn().equals(this.getProductMsn()) && ((PutawayListItemsDTO) obj).getLocation().equals(this.getLocation());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
