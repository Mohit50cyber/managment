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
@Table(name = "supplier")
public class Supplier {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "ems_supplier_id")
	private Integer emsSupplierId;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier", cascade = CascadeType.ALL)
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
    
    public Supplier() {
    	
    }
    
    public Supplier(Integer emsSupplierId) {
		this.emsSupplierId = emsSupplierId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Set<SupplierProductInventoryConfigMapping> getSupplierProductInventoryConfigMappings() {
		return supplierProductInventoryConfigMappings;
	}

	public void setSupplierProductInventoryConfigMappings(
			Set<SupplierProductInventoryConfigMapping> supplierProductInventoryConfigMappings) {
		this.supplierProductInventoryConfigMappings = supplierProductInventoryConfigMappings;
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

	public Integer getEmsSupplierId() {
		return emsSupplierId;
	}

	public void setEmsSupplierId(Integer emsSupplierId) {
		this.emsSupplierId = emsSupplierId;
	}
}
