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
@Table(name = "enterprise_mrn_items")
public class EnterpriseMrnItems implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5623210361408148920L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "mrn_id")
	private Integer mrnId;
	
	@Column(name = "po_item_id")
	private Integer poItemId;
	
	@Column(name = "product_id")
	private Integer productId;
	
	@Column(name = "arrived_quantity")
	private Double arrivedQuantity;
	
	@Column(name = "is_approved")
	private Boolean isApproved;
	
	@Column(name = "status")
	private Integer status;
	
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

	public Integer getMrnId() {
		return mrnId;
	}

	public void setMrnId(Integer mrnId) {
		this.mrnId = mrnId;
	}

	public Integer getPoItemId() {
		return poItemId;
	}

	public void setPoItemId(Integer poItemId) {
		this.poItemId = poItemId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Double getArrivedQuantity() {
		return arrivedQuantity;
	}

	public void setArrivedQuantity(Double arrivedQuantity) {
		this.arrivedQuantity = arrivedQuantity;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
