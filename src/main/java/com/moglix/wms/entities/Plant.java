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
@Table(name = "plant")
public class Plant {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "buyers_plant_id")
	private Integer buyersPlantId;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plant", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<PlantProductInventoryConfigMapping> plantProductInventoryConfigMappings;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plant", cascade = CascadeType.MERGE)
	@OrderBy
	@JsonManagedReference
	private Set<SaleOrder> saleOrders;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modified = new Date();
    
    public Plant() {
	}
    
    public Plant(Integer buyersPlantId) {
    	this.buyersPlantId = buyersPlantId;
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

	public Set<SaleOrder> getSaleOrders() {
		return saleOrders;
	}

	public void setSaleOrders(Set<SaleOrder> saleOrders) {
		this.saleOrders = saleOrders;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBuyersPlantId() {
		return buyersPlantId;
	}

	public void setBuyersPlantId(Integer buyersPlantId) {
		this.buyersPlantId = buyersPlantId;
	}

	@Override
	public String toString() {
		return "Plant [id=" + id + ", buyersPlantId=" + buyersPlantId + ", plantProductInventoryConfigMappings="
				+ plantProductInventoryConfigMappings + ", saleOrders=" + saleOrders + ", created=" + created
				+ ", modified=" + modified + "]";
	}
}
