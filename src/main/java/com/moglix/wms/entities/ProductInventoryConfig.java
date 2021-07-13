package com.moglix.wms.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_inventory_config")
public class ProductInventoryConfig {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "maximum_quantity")
    private Double maximumQuantity;

    @Column (name = "minimum_quantity")
    private Double minimumQuantity;

    @Column (name = "purchase_price")
    private Double purchasePrice;
    
    @Column (name = "product_msn")
    private String productMsn;
    
    @Column (name = "warehouse_id")
    private Integer warehouseId;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productInventoryConfig", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<PlantProductInventoryConfigMapping> plantProductInventoryConfigMappings;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "productInventoryConfig", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<SupplierProductInventoryConfigMapping> supplierProductInventoryConfigMappings;
    
    
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

	public Double getMaximumQuantity() {
		return maximumQuantity;
	}

	public void setMaximumQuantity(Double maximumQuantity) {
		this.maximumQuantity = maximumQuantity;
	}

	public Double getMinimumQuantity() {
		return minimumQuantity;
	}

	public void setMinimumQuantity(Double minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}

	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
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

	public Set<PlantProductInventoryConfigMapping> getPlantProductInventoryConfigMappings() {
		return plantProductInventoryConfigMappings;
	}

	public void setPlantProductInventoryConfigMappings(
			Set<PlantProductInventoryConfigMapping> plantProductInventoryConfigMappings) {
		this.plantProductInventoryConfigMappings = plantProductInventoryConfigMappings;
	}

	public Set<SupplierProductInventoryConfigMapping> getSupplierProductInventoryConfigMappings() {
		return supplierProductInventoryConfigMappings;
	}

	public void setSupplierProductInventoryConfigMappings(
			Set<SupplierProductInventoryConfigMapping> supplierProductInventoryConfigMappings) {
		this.supplierProductInventoryConfigMappings = supplierProductInventoryConfigMappings;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
