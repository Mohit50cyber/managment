package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "packet_return_item")
public class ReturnPacketItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2746667407808966215L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "return_packet_id")
    @JsonBackReference
    private ReturnPacket returnPacket;
	
	@Column(name = "quantity")
	private Double quantity;
	
	@Column(name = "product_msn")
	private String productMsn;
	
	@Column(name = "ems_order_item_id")
    private Integer emsOrderItemId;
	
	@Column(name = "ems_return_item_id")
    private Integer emsReturnItemId;
	
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

	public ReturnPacket getReturnPacket() {
		return returnPacket;
	}

	public void setReturnPacket(ReturnPacket returnPacket) {
		this.returnPacket = returnPacket;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
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

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Integer getEmsOrderItemId() {
		return emsOrderItemId;
	}

	public void setEmsOrderItemId(Integer emsOrderItemId) {
		this.emsOrderItemId = emsOrderItemId;
	}

	public Integer getEmsReturnItemId() {
		return emsReturnItemId;
	}

	public void setEmsReturnItemId(Integer emsReturnItemId) {
		this.emsReturnItemId = emsReturnItemId;
	}
}
