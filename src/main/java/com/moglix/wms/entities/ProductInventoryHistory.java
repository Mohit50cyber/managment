package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;

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

import com.moglix.wms.constants.InventoryMovementType;
import com.moglix.wms.constants.InventoryTransactionType;

@Entity
@Table(name = "product_inventory_history")
public class ProductInventoryHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1863431649797553966L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "prev_quantity")
    private Double prevQuantity;

    @Column (name = "current_quantity")
    private Double currentQuantity;
    
    
    @Column (name = "product_msn")
    private String productMsn;
    
    @Column (name = "transaction_ref")
    private String transactionRef;    
    
    @Enumerated(EnumType.STRING)
	@Column(name = "transaction_type")
    private InventoryTransactionType transactionType;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;
    
    @Enumerated(EnumType.STRING)
	@Column(name = "movement_type")
    private InventoryMovementType movementType;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)

    private Product product;
    
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

	public Double getPrevQuantity() {
		return prevQuantity;
	}

	public void setPrevQuantity(Double prevQuantity) {
		this.prevQuantity = prevQuantity;
	}

	public Double getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(Double currentQuantity) {
		this.currentQuantity = currentQuantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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
	
    public InventoryTransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(InventoryTransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public InventoryMovementType getMovementType() {
		return movementType;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public void setMovementType(InventoryMovementType movementType) {
		this.movementType = movementType;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
}
