package com.moglix.wms.dto;

import java.util.Date;

import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;
import com.moglix.wms.entities.ProductInventoryHistory;

public class ProductInventoryHistoryDTO {
	private Integer id;
	private Double prevQuantity;
	private Double currentQuantity;
	private String productMsn;
	private Date created;
    private Date modified;
    private InventoryTransactionType inventoryTransactionType;
    private InventoryMovementType inventoryMovementType;
    private String warehouseName;
    private String transactionRef;
    
    public ProductInventoryHistoryDTO(ProductInventoryHistory history) {
    	this.id = history.getId();
    	this.prevQuantity = history.getPrevQuantity();
    	this.currentQuantity = history.getCurrentQuantity();
    	this.productMsn = history.getProductMsn();
    	this.inventoryTransactionType = history.getTransactionType();
    	this.inventoryMovementType = history.getMovementType();
    	this.created = history.getCreated();
    	this.modified = history.getModified();
    	this.warehouseName = history.getWarehouse().getName();
    	this.transactionRef = history.getTransactionRef();
    }
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getPrevQuantity() {
		return prevQuantity;
	}
	public void setPrevQuantity(Double prevQuantity) {
		this.prevQuantity = prevQuantity;
	}
	public Double getCurrentQuantity() {
		return currentQuantity;
	}
	public void setCurrentQuantity(Double currentQuantity) {
		this.currentQuantity = currentQuantity;
	}
	public String getProductMsn() {
		return productMsn;
	}
	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
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

	public InventoryTransactionType getInventoryTransactionType() {
		return inventoryTransactionType;
	}

	public void setInventoryTransactionType(InventoryTransactionType inventoryTransactionType) {
		this.inventoryTransactionType = inventoryTransactionType;
	}

	public InventoryMovementType getInventoryMovementType() {
		return inventoryMovementType;
	}

	public void setInventoryMovementType(InventoryMovementType inventoryMovementType) {
		this.inventoryMovementType = inventoryMovementType;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}
}
