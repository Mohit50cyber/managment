package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.validator.CheckValidWarehouse;

@Entity
@Table(name = "batch", uniqueConstraints=@UniqueConstraint(columnNames={"ref_no", "batch_type"}))
public class Batch implements Serializable {
	/**
	 * 
	 */
		
	private static final long serialVersionUID = -3125586791234345L;

	// id, batch_code, mrn_id, supplier_po_id, warehouse_id, purchase_date,
	// inbounded_by(user_id), created, modified

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "batch_code")
	private Integer batchCode;

	@Column(name = "ref_no")
	private String refNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "batch_type")
	BatchType batchType;
	
	@Column(name = "ems_return_id")
	private Integer emsReturnId;
	
	@Column(name = "warehouse_id")
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@Column(name = "parent_ref_no")
	private String parentRefNo;

	@Column (name = "warehouse_name")
	private String warehouseName;
	
	@Column(name = "purchase_date")
	private Date purchaseDate;

	@Column(name = "inbound_by")
	private String inboundedBy;

	@Column(name = "supplier_id")
	private Integer supplierId;
	
	@Column(name = "supplier_name")
	private String supplierName;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "batch", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<Inbound> inbounds;

	public Integer getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(Integer batchCode) {
		this.batchCode = batchCode;
	}

	public BatchType getBatchType() {
		return batchType;
	}

	public void setBatchType(BatchType batchType) {
		this.batchType = batchType;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getInboundedBy() {
		return inboundedBy;
	}

	public void setInboundedBy(String inboundedBy) {
		this.inboundedBy = inboundedBy;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Set<Inbound> getInbounds() {
		return inbounds;
	}

	public void setInbounds(Set<Inbound> inbounds) {
		this.inbounds = inbounds;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public String toString() {
		return "Batch [id=" + id + ", batchCode=" + batchCode + ", refNo=" + refNo + ", batchType=" + batchType
				+ ", emsReturnId=" + emsReturnId + ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName
				+ ", purchaseDate=" + purchaseDate + ", inboundedBy=" + inboundedBy + ", supplierId=" + supplierId
				+ ", supplierName=" + supplierName + ", created=" + created + ", modified=" + modified + ", inbounds="
				+ inbounds + "]";
	}

	public String getParentRefNo() {
		return parentRefNo;
	}

	public void setParentRefNo(String parentRefNo) {
		this.parentRefNo = parentRefNo;
	}
	
	
}
