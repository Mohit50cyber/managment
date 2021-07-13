package com.moglix.wms.dto;

import java.io.Serializable;
import java.util.Date;

import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.entities.Inbound;

public class InboundDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4897945073342305486L;
	private Integer id;
	private Integer supplierPoItemId;
	private String uom;
	private Integer warehouseId;
	private String warehouseName;
	private Double quantity;
	private Date mfgDate;
	private Date expDate;
	private Date purchaseDate;
	private Double purchasePrice;
	private Double tax;
	private InboundStatusType status;
	private String supplierName;
	private Integer supplierId;	
	private String productName;
	private Integer supplierPoId;
	private Date created;
	private Date modified;
	private String productMsn;
	private Boolean serializedProduct;
	private Boolean inventorize;
	private InboundType type;
	private Boolean expiryEnabled;
	private Boolean lotEnabled;
	

	public InboundDTO(Inbound inbound) {
		this.id = inbound.getId();
		this.supplierPoItemId = inbound.getSupplierPoItemId();
		this.uom = inbound.getUom();
		this.warehouseId = inbound.getWarehouseId();
		this.warehouseName = inbound.getWarehouseName();
		this.quantity = inbound.getQuantity();
		this.mfgDate = inbound.getMfgDate();
		this.expDate = inbound.getExpDate();
		this.purchaseDate = inbound.getPurchaseDate();
		this.purchasePrice = inbound.getPurchasePrice();
		this.tax = inbound.getTax();
		this.status = inbound.getStatus();
		this.supplierName = inbound.getSupplierName();
		this.productName = inbound.getProductName();
		this.supplierPoId = inbound.getSupplierPoId();
		this.created = inbound.getCreated();
		this.modified = inbound.getModified();
		this.supplierId = inbound.getSupplierId();
		this.productMsn = inbound.getProduct().getProductMsn();
		this.serializedProduct = inbound.getProduct().getSerializedProduct();
		this.inventorize = inbound.getInventorize();
		this.type = inbound.getType();
		this.expiryEnabled = inbound.getProduct().getExpiryDateManagementEnabled();
		this.lotEnabled = inbound.getProduct().getLotManagementEnabled();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSupplierPoItemId() {
		return supplierPoItemId;
	}

	public void setSupplierPoItemId(Integer supplierPoItemId) {
		this.supplierPoItemId = supplierPoItemId;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Date getMfgDate() {
		return mfgDate;
	}

	public void setMfgDate(Date mfgDate) {
		this.mfgDate = mfgDate;
	}

	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public InboundStatusType getStatus() {
		return status;
	}

	public void setStatus(InboundStatusType status) {
		this.status = status;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Boolean getSerializedProduct() {
		return serializedProduct;
	}

	public void setSerializedProduct(Boolean serializedProduct) {
		this.serializedProduct = serializedProduct;
	}

	public Boolean getInventorize() {
		return inventorize;
	}

	public void setInventorize(Boolean inventorize) {
		this.inventorize = inventorize;
	}
	public InboundType getType() {
		return type;
	}

	public void setType(InboundType type) {
		this.type = type;
	}

	public Boolean getExpiryEnabled() {
		return expiryEnabled;
	}

	public void setExpiryEnabled(Boolean expiryEnabled) {
		this.expiryEnabled = expiryEnabled;
	}

	public Boolean getLotEnabled() {
		return lotEnabled;
	}

	public void setLotEnabled(Boolean lotEnabled) {
		this.lotEnabled = lotEnabled;
	}
	
}
