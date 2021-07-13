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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moglix.wms.constants.PacketStatus;

@Entity
@Table(name = "packet_return")
public class ReturnPacket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -374852423088916542L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "ems_packet_id")
	private Integer emsPacketId;
	
	@Column(name = "invoice_number")
	private String invoiceNumber;
	
	@Column(name = "customer_name")
	private String customerName;
		
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PacketStatus status;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "returnPacket", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<ReturnPacketItem> returnPacketItems;

	@Column(name = "ems_return_id")
	private Integer emsReturnId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_id", nullable = false)
	@JsonBackReference
	private Warehouse warehouse;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "packet_id")
    @JsonBackReference
    private Packet packet;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
    private Date created = new Date();
	
	@Column(name = "total_quantity")
	private Double totalQuantity;
	
	@Column(name = "msn_count")
	private Long msnCount;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "returnPacket", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<ReturnPickupList> returnPickupList;
	
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

	public PacketStatus getStatus() {
		return status;
	}

	public void setStatus(PacketStatus status) {
		this.status = status;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Long getMsnCount() {
		return msnCount;
	}

	public void setMsnCount(Long msnCount) {
		this.msnCount = msnCount;
	}

	public Set<ReturnPickupList> getReturnPickupList() {
		return returnPickupList;
	}

	public void setReturnPickupList(Set<ReturnPickupList> returnPickupList) {
		this.returnPickupList = returnPickupList;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Set<ReturnPacketItem> getReturnPacketItems() {
		return returnPacketItems;
	}

	public void setReturnPacketItems(Set<ReturnPacketItem> returnPacketItems) {
		this.returnPacketItems = returnPacketItems;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}
}
