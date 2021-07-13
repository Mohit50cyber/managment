package com.moglix.ems.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "enterprise_return_items")
public class EnterpriseReturnItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1238633383016758378L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "return_id")
	private Integer returnId;
	
	@Column(name = "item_id")
	private Integer itemId;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "quantity")
	private Double quantity;
	
	@Column(name = "unit_price")
	private Integer unitPrice;
	
	@Column(name = "calculation_type")
	private String calculationType;
	
	@Column(name = "item_value")
	private Integer itemValue;
	
	@Column(name = "other_charges")
	private Integer otherCharges;
	
	@Column(name = "i_gst")
	private Integer iGst;
	
	@Column(name = "c_gst")
	private Integer cGst;
	
	@Column(name = "s_gst")
	private Integer sGst;
	
	@Column(name = "igst_amount")
	private Integer igstAmount;
	
	@Column(name = "cgst_amount")
	private Integer cgstAmount;
	
	@Column(name = "sgst_amount")
	private Integer sgstAmount;
	
	@Column(name = "is_inventory_updated")
	private Boolean isInventoryUpdated;
	
	@Column(name = "item_status")
	private Integer itemStatus;
	
	@Column(name = "is_rtv_done")
	private Boolean isRtvDone;	
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date createdAt = new Date();

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date updateAt = new Date();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getReturnId() {
		return returnId;
	}

	public void setReturnId(Integer returnId) {
		this.returnId = returnId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
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

	public Integer getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Integer unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(String calculationType) {
		this.calculationType = calculationType;
	}

	public Integer getItemValue() {
		return itemValue;
	}

	public void setItemValue(Integer itemValue) {
		this.itemValue = itemValue;
	}

	public Integer getOtherCharges() {
		return otherCharges;
	}

	public void setOtherCharges(Integer otherCharges) {
		this.otherCharges = otherCharges;
	}

	public Integer getiGst() {
		return iGst;
	}

	public void setiGst(Integer iGst) {
		this.iGst = iGst;
	}

	public Integer getcGst() {
		return cGst;
	}

	public void setcGst(Integer cGst) {
		this.cGst = cGst;
	}

	public Integer getsGst() {
		return sGst;
	}

	public void setsGst(Integer sGst) {
		this.sGst = sGst;
	}

	public Integer getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(Integer igstAmount) {
		this.igstAmount = igstAmount;
	}

	public Integer getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(Integer cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public Integer getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(Integer sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public Boolean getIsInventoryUpdated() {
		return isInventoryUpdated;
	}

	public void setIsInventoryUpdated(Boolean isInventoryUpdated) {
		this.isInventoryUpdated = isInventoryUpdated;
	}

	public Integer getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(Integer itemStatus) {
		this.itemStatus = itemStatus;
	}

	public Boolean getIsRtvDone() {
		return isRtvDone;
	}

	public void setIsRtvDone(Boolean isRtvDone) {
		this.isRtvDone = isRtvDone;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}
}
