package com.moglix.wms.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.moglix.wms.constants.SaleOrderSupplierPurchaseOrderMappingStatus;
import com.moglix.wms.validator.CheckValidWarehouse;

/**
 * @author sparsh saxena on 22/3/21
 */

@Entity
@Table(name = "sale_order_supplier_purchase_order_mapping_item")
public class SaleOrderSupplierPurchaseOrderMappingItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	 
	@Column(name = "sale_order_id")
	private Integer saleOrderId;

	@Column(name = "item_ref")
	private String itemRef;
	
	@Column(name = "ref_no")
	private String refNo;
	
	@Column(name = "product_id")
	private Integer productID;
	
	@Column(name = "product_msn")
	private String productMSN;
	
	@Column(name = "product_name",columnDefinition="TEXT")
	private String productName;
 	
	@NotNull
	@Column(name = "quantity")
	private Double quantity;
	
	@Column(name = "batch_id")
	private Integer batchId;
	
	@Column(name = "inbound_id")
	private Integer inboundId;
	 
	@Column(name = "supplier_id")
	private Integer supplierId;

	@Column(name = "supplier_name")
	private String supplierName;
	 
	@Column(name = "supplier_po_id")
	private Integer supplierPoId;
 	 
	@Column(name = "supplier_po_item_id")
	private Integer supplierPoItemId;
	
	@Column(name = "sale_order_supplier_purchase_order_mapping_id")
	private Integer saleOrderSupplierPurchaseOrderMappingId;
 	 
	@CheckValidWarehouse
	@Column(name = "warehouse_id")
	private Integer warehouseId;
 	
	@Column(name = "warehouse_name")
	private String warehouseName;
	
	@Column(name = "pickup_warehouse_id")
	private Integer pickUpWarehouseId;
 	
	@Column(name = "pickup_warehouse_name")
	private String pickUpWarehouseName;
 	 
	@Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SaleOrderSupplierPurchaseOrderMappingStatus status = SaleOrderSupplierPurchaseOrderMappingStatus.INBOUNDED;
	
	@Column(name = "is_active")
	private boolean isActive;
	
	@Column(name = "order_ref")
    private String orderRef;
	 
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSaleOrderId() {
		return saleOrderId;
	}

	public void setSaleOrderId(Integer saleOrderId) {
		this.saleOrderId = saleOrderId;
	}

	public String getItemRef() {
		return itemRef;
	}

	public void setItemRef(String itemRef) {
		this.itemRef = itemRef;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public Integer getProductID() {
		return productID;
	}

	public void setProductID(Integer productID) {
		this.productID = productID;
	}

	public String getProductMSN() {
		return productMSN;
	}

	public void setProductMSN(String productMSN) {
		this.productMSN = productMSN;
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

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public Integer getInboundId() {
		return inboundId;
	}

	public void setInboundId(Integer inboundId) {
		this.inboundId = inboundId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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

	public Integer getSaleOrderSupplierPurchaseOrderMappingId() {
		return saleOrderSupplierPurchaseOrderMappingId;
	}

	public void setSaleOrderSupplierPurchaseOrderMappingId(Integer saleOrderSupplierPurchaseOrderMapping) {
		saleOrderSupplierPurchaseOrderMappingId = saleOrderSupplierPurchaseOrderMapping;
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

	public Integer getPickUpWarehouseId() {
		return pickUpWarehouseId;
	}

	public void setPickUpWarehouseId(Integer pickUpWarehouseId) {
		this.pickUpWarehouseId = pickUpWarehouseId;
	}

	public String getPickUpWarehouseName() {
		return pickUpWarehouseName;
	}

	public void setPickUpWarehouseName(String pickUpWarehouseName) {
		this.pickUpWarehouseName = pickUpWarehouseName;
	}

	public SaleOrderSupplierPurchaseOrderMappingStatus getStatus() {
		return status;
	}

	public void setStatus(SaleOrderSupplierPurchaseOrderMappingStatus status) {
		this.status = status;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
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
}
