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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moglix.wms.constants.DangerType;
import com.moglix.wms.constants.ProductType;
import com.moglix.wms.constants.StorageType;

@Entity
@Table(name = "product", uniqueConstraints=@UniqueConstraint(columnNames={"product_msn", "uom"}))
public class Product implements Serializable {
	private static final long serialVersionUID = 2140960164767535044L;
	// id, MSN, name, base_uom_id, type(durable, perishable), total_quantity,
	// current_quantity, available_quanity, allocated_quantity,
	// storage_type(room_temp, cold_storage), danger_type(flammable, hazardous),
	// is_serialized_product, created, modified
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name = "product_msn")
	private String productMsn;
	
	@Column (name = "product_brand")
	private String productBrand;
	
	@Column (name = "name",columnDefinition="TEXT")
	private String productName;
	
	@Column (name = "uom")
	private String uom;

	@Enumerated(EnumType.STRING)
	@Column (name = "product_type")
	private ProductType type;
	
	@Column (name = "shelf_life")
	private Integer shelfLife;
	
	@Column (name = "total_quantity")
	private Double totalQuantity = 0.0d;
	
	@Column (name = "current_quantity")
	private Double currentQuantity = 0.0d;
	
	@Column (name = "available_quantity")
	private Double availableQuantity = 0.0d;
	
	@Column (name = "allocated_quantity")
	private Double allocatedQuantity = 0.0d;
	
	@Column (name = "lot_management_enabled")
	private Boolean lotManagementEnabled = false;
	
	@Column (name = "expiry_date_management_enabled")
	private Boolean expiryDateManagementEnabled = false;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "storage_type")
	private StorageType storageType;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "danger_type")
	private DangerType dangerType;
	
	@Column (name = "is_serialized_product")
	private Boolean serializedProduct;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
	@OrderBy
	@JsonManagedReference
	private Set<Inbound> inbounds;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<ReturnPickupListItem> returnPickupListItems;
	
	
	public Set<Inbound> getInbounds() {
		return inbounds;
	}

	public void setInbounds(Set<Inbound> inbounds) {
		this.inbounds = inbounds;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Double totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Double getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(Double currentQuantity) {
		this.currentQuantity = currentQuantity;
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

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public DangerType getDangerType() {
		return dangerType;
	}

	public void setDangerType(DangerType dangerType) {
		this.dangerType = dangerType;
	}
	
	public void setSerializedProduct(Boolean serializedProduct) {
		this.serializedProduct = serializedProduct;
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
	
	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	public Set<ReturnPickupListItem> getReturnPickupListItems() {
		return returnPickupListItems;
	}

	public void setReturnPickupListItems(Set<ReturnPickupListItem> returnPickupListItems) {
		this.returnPickupListItems = returnPickupListItems;
	}

	public Boolean getSerializedProduct() {
		return serializedProduct;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}
	
	public Boolean getLotManagementEnabled() {
		return lotManagementEnabled;
	}

	public void setLotManagementEnabled(Boolean lotManagementEnabled) {
		this.lotManagementEnabled = lotManagementEnabled;
	}

	public Boolean getExpiryDateManagementEnabled() {
		return expiryDateManagementEnabled;
	}

	public void setExpiryDateManagementEnabled(Boolean expiryDateManagementEnabled) {
		this.expiryDateManagementEnabled = expiryDateManagementEnabled;
	}

	public Integer getShelfLife() {
		return shelfLife;
	}

	public void setShelfLife(Integer shelfLife) {
		this.shelfLife = shelfLife;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", productMsn=" + productMsn + ", productBrand=" + productBrand + ", productName="
				+ productName + ", uom=" + uom + ", type=" + type + ", shelfLife=" + shelfLife + ", totalQuantity="
				+ totalQuantity + ", currentQuantity=" + currentQuantity + ", availableQuantity=" + availableQuantity
				+ ", allocatedQuantity=" + allocatedQuantity + ", lotManagementEnabled=" + lotManagementEnabled
				+ ", expiryDateManagementEnabled=" + expiryDateManagementEnabled + ", storageType=" + storageType
				+ ", dangerType=" + dangerType + ", serializedProduct=" + serializedProduct + ", created=" + created
				+ ", modified=" + modified + ", inbounds=" + inbounds + ", returnPickupListItems="
				+ returnPickupListItems + "]";
	}
}
