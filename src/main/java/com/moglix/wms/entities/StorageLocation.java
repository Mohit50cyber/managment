package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.moglix.wms.constants.StorageLocationType;

/**
 * @author pankaj on 1/5/19
 */
@Entity
@Table(name = "storage_location")
public class StorageLocation implements Serializable {
    private static final long serialVersionUID = 2824630082696139670L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_id", nullable = false)
    private Rack rack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @Column(name="height")
    private Double height;

    @Column(name="width")
    private Double width;

    @Column(name="depth")
    private Double depth;

    @Column(name="active")
    private boolean active;

    @Column(name="full")
    private boolean full;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storageLocation", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<InboundStorage> inboundStorages;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storageLocation", cascade = CascadeType.ALL)
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
    
    @Enumerated(EnumType.STRING)
    @Column(name="type", columnDefinition = "varchar(255)")
    private StorageLocationType type;
    
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fromStorageLocation", cascade = CascadeType.ALL)
    private List<BinTransferHistory> fromBinTransferHistory = new ArrayList<>();
   
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "toStorageLocation", cascade = CascadeType.ALL)
    private List<BinTransferHistory> toBinTransferHistory = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public Bin getBin() {
        return bin;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public Set<InboundStorage> getInboundStorages() {
		return inboundStorages;
	}

	public void setInboundStorages(Set<InboundStorage> inboundStorages) {
		this.inboundStorages = inboundStorages;
	}

	public Set<ReturnPickupListItem> getReturnPickupListItems() {
		return returnPickupListItems;
	}

	public void setReturnPickupListItems(Set<ReturnPickupListItem> returnPickupListItems) {
		this.returnPickupListItems = returnPickupListItems;
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

	public StorageLocationType getType() {
		return type;
	}

	public void setType(StorageLocationType type) {
		this.type = type;
	}
    
    
}
