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
import com.moglix.wms.constants.InboundStorageType;

@Entity
@Table(name = "inbound_storage")
public class InboundStorage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6711200271565579239L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "quantity")
	private Double quantity;

	@Column(name = "available_quantity")
	private Double availableQuantity;

	@Column(name = "allocated_quantity")
	private Double allocatedQuantity = 0d;
	
	@Column(name = "confirmed")
	private Boolean confirmed = true;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private InboundStorageType type = InboundStorageType.FRESH;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference
	private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "inbound_id")
    @JsonBackReference
    private Inbound inbound;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "storage_location_id")
    @JsonBackReference
    private StorageLocation storageLocation;
    
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inboundStorage",cascade = CascadeType.ALL)
    @OrderBy
	@JsonManagedReference
    private Set<PacketItem> packetItem;
    
    
    @Column(name = "lot_id")
    private Integer lotId;
    

    @Column(name = "lot_number")
    private String lotNumber;
    
    @Column(name = "expiry_date")
	private Date expiryDate;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inboundStorage", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<ReturnPickupListItem> returnPickupListItems;
    
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

	public Boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
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

	public Inbound getInbound() {
		return inbound;
	}

	public void setInbound(Inbound inbound) {
		this.inbound = inbound;
	}

	public StorageLocation getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(StorageLocation storageLocation) {
		this.storageLocation = storageLocation;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public Set<PacketItem> getPacketItem() {
		return packetItem;
	}

	public void setPacketItem(Set<PacketItem> packetItem) {
		this.packetItem = packetItem;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public InboundStorageType getType() {
		return type;
	}

	public void setType(InboundStorageType type) {
		this.type = type;
	}

	public Integer getLotId() {
		return lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public Set<ReturnPickupListItem> getReturnPickupListItems() {
		return returnPickupListItems;
	}

	public void setReturnPickupListItems(Set<ReturnPickupListItem> returnPickupListItems) {
		this.returnPickupListItems = returnPickupListItems;
	}
}
