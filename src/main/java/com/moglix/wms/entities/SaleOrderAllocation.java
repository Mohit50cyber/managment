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
import com.moglix.wms.constants.SaleOrderAllocationStatus;
/**
 * @author pankaj on 6/5/19
 */
@Entity
@Table(name = "sale_order_allocation")
public class SaleOrderAllocation implements Serializable {
    private static final long serialVersionUID = -7683665032016196520L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_order_id", nullable = false)
    private SaleOrder saleOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_storage_id", nullable = false)
    private InboundStorage inboundStorage;

    @Column(name = "allocated_quantity")
    private Double allocatedQuantity;           //allocationQty = availableQty(unpacked) + packedQty

    @Column(name = "available_quantity")
    private Double availableQuantity;
    
    @Column(name = "moved_quantity")
    private Double movedQuantity = 0d;

    @Column(name = "packed_quantity")
    private Double packedQuantity = 0d;

    /*@Column(name = "shipped_quantity")
    private Double shippedQuantity = 0d;*/

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SaleOrderAllocationStatus status = SaleOrderAllocationStatus.ALLOCATED;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "saleOrderAllocation", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<PacketItem> packetItems;

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

    public SaleOrder getSaleOrder() {
        return saleOrder;
    }

    public void setSaleOrder(SaleOrder saleOrder) {
        this.saleOrder = saleOrder;
    }

    public InboundStorage getInboundStorage() {
        return inboundStorage;
    }

    public void setInboundStorage(InboundStorage inboundStorage) {
        this.inboundStorage = inboundStorage;
    }

    public Double getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(Double allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
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
	
	public SaleOrderAllocationStatus getStatus() {
        return status;
    }

    public void setStatus(SaleOrderAllocationStatus status) {
        this.status = status;
    }

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Double getPackedQuantity() {
        return packedQuantity;
    }

    public void setPackedQuantity(Double packedQuantity) {
        this.packedQuantity = packedQuantity;
    }

	public Set<PacketItem> getPacketItems() {
		return packetItems;
	}

	public void setPacketItems(Set<PacketItem> packetItems) {
		this.packetItems = packetItems;
	}

	public Double getMovedQuantity() {
		return movedQuantity;
	}

	public void setMovedQuantity(Double movedQuantity)
    {
		this.movedQuantity = movedQuantity;
	}

    /*public Double getShippedQuantity() {
        return shippedQuantity;
    }

    public void setShippedQuantity(Double shippedQuantity) {
        this.shippedQuantity = shippedQuantity;
    }*/
}
