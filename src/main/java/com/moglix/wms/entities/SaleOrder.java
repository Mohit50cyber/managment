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
import com.moglix.wms.constants.OrderType;
import com.moglix.wms.constants.SaleOrderStatus;

/**
 * @author pankaj on 6/5/19
 */
@Entity
@Table(name = "sale_order")
public class SaleOrder implements Serializable {
   
	private static final long serialVersionUID = 4612009144066497969L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "bulk_invoice_id")
    private String bulkInvoiceId;
    
    @Column(name = "batch_ref")
    private String batchRef;

    @Column(name = "ems_order_id")
    private Integer emsOrderId;

    @Column(name = "item_ref", unique = true)
    private String itemRef;

    @Column(name = "ems_order_item_id", unique = true)
    private Integer emsOrderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "ordered_quantity")
    private Double orderedQuantity;

    @Column(name = "stn_assoication")
    private Boolean stnAssoication=false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "saleOrder", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<SaleOrderAllocation> saleOrderAllocations;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "saleOrder", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<PacketItem> packetItems;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "plant_id")
    @JsonBackReference
    private Plant plant;

    @Column(name="allocated_quantity")
    private Double allocatedQuantity = 0d;

    @Column(name="packed_quantity")
    private Double packedQuantity = 0d;

    @Column(name = "remark")
    private String remark;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SaleOrderStatus status = SaleOrderStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OrderType orderType = OrderType.NEW;
    
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modified = new Date();
    
    @Column (name = "unique_block_transaction_id")
    private String uniqueblockid;

    @Column(name = "order_ref")
    private String orderRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmsOrderId() {
        return emsOrderId;
    }

    public void setEmsOrderId(Integer emsOrderId) {
        this.emsOrderId = emsOrderId;
    }

    public Integer getEmsOrderItemId() {
        return emsOrderItemId;
    }

    public void setEmsOrderItemId(Integer emsOrderItemId) {
        this.emsOrderItemId = emsOrderItemId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Double getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Double orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Double getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(Double allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public Double getPackedQuantity() {
        return packedQuantity;
    }

    public void setPackedQuantity(Double packedQuantity) {
        this.packedQuantity = packedQuantity;
    }

    public SaleOrderStatus getStatus() {
        return status;
    }

    public void setStatus(SaleOrderStatus status) {
        this.status = status;
    }
    
    public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
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

	public Set<SaleOrderAllocation> getSaleOrderAllocations() {
		return saleOrderAllocations;
	}

	public void setSaleOrderAllocations(Set<SaleOrderAllocation> saleOrderAllocations) {
		this.saleOrderAllocations = saleOrderAllocations;
	}

	public Set<PacketItem> getPacketItems() {
		return packetItems;
	}

	public void setPacketItems(Set<PacketItem> packetItems) {
		this.packetItems = packetItems;
	}

    public String getItemRef() {
        return itemRef;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }

	public String getBulkInvoiceId() {
		return bulkInvoiceId;
	}

	public void setBulkInvoiceId(String bulkInvoiceId) {
		this.bulkInvoiceId = bulkInvoiceId;
	}

	public String getBatchRef() {
		return batchRef;
	}

	public void setBatchRef(String batchRef) {
		this.batchRef = batchRef;
	}

	public Plant getPlant() {
		return plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}
	
	public String getUniqueblockid() {
		return uniqueblockid;
	}

	public void setUniqueblockid(String uniqueblockid) {
		this.uniqueblockid = uniqueblockid;
	}

	public String getOrderRef() {
		return orderRef;
	}

	public void setOrderRef(String orderRef) {
		this.orderRef = orderRef;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
    public Boolean getStnAssoication() {
        return (stnAssoication==null)?false:stnAssoication;
    }

    public void setStnAssoication(Boolean stnAssoication) {
        this.stnAssoication = stnAssoication;
    }
}
