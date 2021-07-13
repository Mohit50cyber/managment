package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import com.moglix.wms.constants.PacketItemStatus;

@Entity
@Table(name = "packet_item")
public class PacketItem  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8480377050324990863L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "packet_id")
    @JsonBackReference
    private Packet packet;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "sale_order_id")
    @JsonBackReference
    private SaleOrder saleOrder;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PacketItemStatus status = PacketItemStatus.AVAILABLE;
	
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "sale_order_allocation_id")
    @JsonBackReference
    private SaleOrderAllocation saleOrderAllocation;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inbound_storage_id")
	private InboundStorage inboundStorage;
	
	@Column(name = "quantity")
	private Double quantity;
	
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

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public InboundStorage getInboundStorage() {
		return inboundStorage;
	}

	public void setInboundStorage(InboundStorage inboundStorage) {
		this.inboundStorage = inboundStorage;
	}

	public PacketItemStatus getStatus() {
		return status;
	}

	public void setStatus(PacketItemStatus status) {
		this.status = status;
	}

	public SaleOrder getSaleOrder() {
		return saleOrder;
	}

	public void setSaleOrder(SaleOrder saleOrder) {
		this.saleOrder = saleOrder;
	}

	public SaleOrderAllocation getSaleOrderAllocation() {
		return saleOrderAllocation;
	}

	public void setSaleOrderAllocation(SaleOrderAllocation saleOrderAllocation) {
		this.saleOrderAllocation = saleOrderAllocation;
	}

}
