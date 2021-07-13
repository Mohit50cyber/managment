package com.moglix.wms.dto;

import java.io.Serializable;
import java.util.Date;

import com.moglix.wms.constants.DangerType;
import com.moglix.wms.constants.ProductType;
import com.moglix.wms.constants.StorageType;
import com.moglix.wms.entities.Product;

public class ProductDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2136549367663506421L;
	
	private Integer id; 
	private String productMsn;
	private String uom;
	private String productName;
	private ProductType type;
	private Double totalQuantity;
	private Double currentQuantity;
	private Double availableQuantity;
	private Double allocatedQuantity;
	private StorageType storageType;
	private DangerType dangerType;
	private Boolean serializedProduct;
	private Double tax;
	private Integer supplierPoId;
	private Integer supplierPoItemId;
	private String supplierName;
	private Double purchasePrice;
	private Boolean isExpiryEnabled;
	private Boolean isLotEnabled;
	
	private Integer serialNumber;
	
	private Date created = new Date();
	private Date modified = new Date();
	
	public ProductDTO(Product product) {
		this.id = product.getId();
		this.productMsn = product.getProductMsn();
		this.productName = product.getProductName();
		this.type = product.getType();
		this.totalQuantity = product.getTotalQuantity();
		this.availableQuantity = product.getAvailableQuantity();
		this.allocatedQuantity = product.getAllocatedQuantity();
		this.currentQuantity = product.getCurrentQuantity();
		this.storageType = product.getStorageType();
		this.dangerType = product.getDangerType();
		this.serializedProduct = product.getSerializedProduct();
		this.created = product.getCreated();
		this.modified = product.getModified();
		this.uom = product.getUom();
		this.isExpiryEnabled = product.getExpiryDateManagementEnabled();
		this.isLotEnabled = product.getLotManagementEnabled();
	}
	
	public Boolean getIsExpiryEnabled() {
		return isExpiryEnabled;
	}


	public void setIsExpiryEnabled(Boolean isExpiryEnabled) {
		this.isExpiryEnabled = isExpiryEnabled;
	}


	public Boolean getIsLotEnabled() {
		return isLotEnabled;
	}


	public void setIsLotEnabled(Boolean isLotEnabled) {
		this.isLotEnabled = isLotEnabled;
	}


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public ProductType getType() {
		return type;
	}
	public void setType(ProductType type) {
		this.type = type;
	}
	public Double getTotalQuantity() {
		return totalQuantity;
	}
	public Integer getSupplierPoId() {
		return supplierPoId;
	}
	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}
	public Integer getSupplierPoItemId() {
		return supplierPoItemId;
	}
	public void setSupplierPoItemId(Integer supplierPoItemId) {
		this.supplierPoItemId = supplierPoItemId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public Double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public Double getCurrentQuantity() {
		return currentQuantity;
	}
	public void setCurrentQuantity(Double currentQuantity) {
		this.currentQuantity = currentQuantity;
	}
	public Double getAvailableQuantity() {
		return availableQuantity;
	}
	public void setAvailableQuantity(Double availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
	public Double getAllocatedQuantity() {
		return allocatedQuantity;
	}
	public void setAllocatedQuantity(Double allocatedQuantity) {
		this.allocatedQuantity = allocatedQuantity;
	}
	public StorageType getStorageType() {
		return storageType;
	}
	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}
	public DangerType getDangerType() {
		return dangerType;
	}
	public void setDangerType(DangerType dangerType) {
		this.dangerType = dangerType;
	}
	public Boolean getSerializedProduct() {
		return serializedProduct;
	}
	public void setSerializedProduct(Boolean serializedProduct) {
		this.serializedProduct = serializedProduct;
	}
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
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
	public Integer getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}	
}
