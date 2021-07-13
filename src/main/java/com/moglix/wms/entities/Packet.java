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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moglix.wms.constants.PacketStatus;

@Entity
@Table(name = "packet")
public class Packet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7434157552297790327L;
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "ems_packet_id")
	private Integer emsPacketId;
	
	@Column(name = "invoice_number")
	private String invoiceNumber;
	
	@Column(name = "is_cancelled")
	private Boolean cancelled;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PacketStatus status;
	
	@Column(name = "is_lot_enabled")
	private Boolean isLotEnabled;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "packet", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<PacketItem> packetItems;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
    private Date created = new Date();
	
	@Column(name = "total_quantity")
	private Double totalQuantity;
	
	@Column(name = "msn_count")
	private Integer msnCount;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "packet", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
    private Set<ReturnPacket> returnPackets;
		
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modified = new Date();

    @Column(name = "pickedby")
	private String pickedby;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public PacketStatus getStatus() {
		return status;
	}

	public void setStatus(PacketStatus status) {
		this.status = status;
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

	public Set<PacketItem> getPacketItems() {
		return packetItems;
	}

	public void setPacketItems(Set<PacketItem> packetItems) {
		this.packetItems = packetItems;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Integer getMsnCount() {
		return msnCount;
	}

	public void setMsnCount(Integer msnCount) {
		this.msnCount = msnCount;
	}

	public Set<ReturnPacket> getReturnPackets() {
		return returnPackets;
	}

	public void setReturnPackets(Set<ReturnPacket> returnPackets) {
		this.returnPackets = returnPackets;
	}

	public Boolean getIsLotEnabled() {
		return isLotEnabled;
	}

	public void setIsLotEnabled(Boolean isLotEnabled) {
		this.isLotEnabled = isLotEnabled;
	}

	public String getPickedby() {
		return pickedby;
	}

	public void setPickedby(String pickedby) {
		this.pickedby = pickedby;
	}
	
	
}
