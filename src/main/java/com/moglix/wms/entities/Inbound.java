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
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.validator.CheckValidTaxRate;

@Entity
@Table(name = "inbound")
public class Inbound implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2261618250959355159L;

	// id, batch_id, supplier_po_item_id, product_id, uom, warehouse_id,
	// quantity(float), mfg_date, exp_date, purchase_date, purchase_price(float),
	// tax, length, width, depth, status(Started, Done) created, modified
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "supplier_po_item_id")
	private Integer supplierPoItemId;
	
	@Column(name = "bin_assigned_by")
	private String binAssignedBy;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	@JsonBackReference
	private Product product;
	
	
	@Column(name = "inventorisable_quantity")
	private Double inventorisableQuantity;
	
	@Column(name = "uom")
	private String uom;
	
	@Column(name = "warehouse_id")
	private Integer warehouseId;
	
	@Column(name = "warehouse_name")
	private String warehouseName;
	
	@Column(name = "quantity")
	private Double quantity; 
	
	@Column(name = "credit_done_quantity")
	private Double creditDoneQuantity = 0.0d;
	
	@Column(name = "customer_dedit_done_quantity")
	private Double customerDeditDoneQuantity = 0.0d;
	
	@Column(name = "mfg_date")
	private Date mfgDate;
	
	@Column(name = "exp_date")
	private Date expDate;
	
	@Column(name = "purchase_date")
	private Date purchaseDate;
	
	@Column(name = "purchase_price")
	private Double purchasePrice;
	
	@Column(name = "tax")
	@CheckValidTaxRate
	private Double tax;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "status")
	private InboundStatusType status;
	
	@Enumerated(EnumType.STRING)
	@Column (name = "type")
	private InboundType type;

	@Column(name = "supplier_name")
	private String supplierName;
	
	@Column(name = "supplier_id")
	private Integer supplierId;
	
	@Column(name = "product_name",columnDefinition="TEXT")
	private String productName;
	
	@Column(name = "supplier_po_id")
	private Integer supplierPoId;
	
	@Column(name = "inventorize")
	private Boolean inventorize;
	
	@Column(name = "isJunkInventory")
	private Boolean isJunkInventory;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inbound", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<InboundStorage> inboundStorages;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "batch_id")
	@JsonBackReference	
	private Batch batch;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inbound", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<InboundItem> inboundItems;
	
	@OneToMany(mappedBy = "inbound", cascade = CascadeType.ALL)
    private Set<InboundLot> inboundLotAssoc;

	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSupplierPoItemId() {
		return supplierPoItemId;
	}


	public void setSupplierPoItemId(Integer supplierPoItemId) {
		this.supplierPoItemId = supplierPoItemId;
	}


	public Double getInventorisableQuantity() {
		return inventorisableQuantity;
	}


	public void setInventorisableQuantity(Double inventorisableQuantity) {
		this.inventorisableQuantity = inventorisableQuantity;
	}


	public String getUom() {
		return uom;
	}


	public void setUom(String uom) {
		this.uom = uom;
	}


	public Integer getWarehouseId() {
		return warehouseId;
	}


	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}


	public Double getQuantity() {
		return quantity;
	}


	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}


	public Date getMfgDate() {
		return mfgDate;
	}


	public void setMfgDate(Date mfgDate) {
		this.mfgDate = mfgDate;
	}


	public Date getExpDate() {
		return expDate;
	}


	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}


	public Date getPurchaseDate() {
		return purchaseDate;
	}


	public Double getCreditDoneQuantity() {
		return creditDoneQuantity;
	}


	public void setCreditDoneQuantity(Double creditDoneQuantity) {
		this.creditDoneQuantity = creditDoneQuantity;
	}


	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}


	public Double getPurchasePrice() {
		return purchasePrice;
	}


	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}


	public Double getTax() {
		return tax;
	}


	public void setTax(Double tax) {
		this.tax = tax;
	}


	public InboundStatusType getStatus() {
		return status;
	}


	public void setStatus(InboundStatusType status) {
		this.status = status;
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
	
	public Integer getSupplierPoId() {
		return supplierPoId;
	}


	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}


	public Batch getBatch() {
		return batch;
	}


	public void setBatch(Batch batch) {
		this.batch = batch;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getWarehouseName() {
		return warehouseName;
	}


	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}


	public String getSupplierName() {
		return supplierName;
	}


	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}


	public Product getProduct() {
		return product;
	}


	public void setProduct(Product product) {
		this.product = product;
	}


	public Set<InboundStorage> getInboundStorages() {
		return inboundStorages;
	}


	public void setInboundStorages(Set<InboundStorage> inboundStorages) {
		this.inboundStorages = inboundStorages;
	}


	public Integer getSupplierId() {
		return supplierId;
	}


	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}


	public Set<InboundItem> getInboundItems() {
		return inboundItems;
	}


	public void setInboundItems(Set<InboundItem> inboundItems) {
		this.inboundItems = inboundItems;
	}


	public Boolean getInventorize() {
		return inventorize;
	}


	public void setInventorize(Boolean inventorize) {
		this.inventorize = inventorize;
	}
	
	public InboundType getType() {
		return type;
	}


	public void setType(InboundType type) {
		this.type = type;
	}

	public String getBinAssignedBy() {
		return binAssignedBy;
	}


	public void setBinAssignedBy(String binAssignedBy) {
		this.binAssignedBy = binAssignedBy;
	}

	public Double getCustomerDeditDoneQuantity() {
		return customerDeditDoneQuantity;
	}

	public void setCustomerDeditDoneQuantity(Double customerDeditDoneQuantity) {
		this.customerDeditDoneQuantity = customerDeditDoneQuantity;
	}
	
	
	
	
	
	public Boolean getIsJunkInventory() {
		return isJunkInventory;
	}

	public void setIsJunkInventory(Boolean isJunkInventory) {
		this.isJunkInventory = isJunkInventory;
	}

	

	public Set<InboundLot> getInboundLotAssoc() {
		return inboundLotAssoc;
	}


	public void setInboundLotAssoc(Set<InboundLot> inboundLotAssoc) {
		this.inboundLotAssoc = inboundLotAssoc;
	}


	@Override
	public String toString() {
		return "Inbound [id=" + id + ", supplierPoItemId=" + supplierPoItemId + ", binAssignedBy=" + binAssignedBy
				+ ", product=" + product + ", inventorisableQuantity=" + inventorisableQuantity + ", uom=" + uom
				+ ", warehouseId=" + warehouseId + ", warehouseName=" + warehouseName + ", quantity=" + quantity
				+ ", creditDoneQuantity=" + creditDoneQuantity + ", customerDeditDoneQuantity="
				+ customerDeditDoneQuantity + ", mfgDate=" + mfgDate + ", expDate=" + expDate + ", purchaseDate="
				+ purchaseDate + ", purchasePrice=" + purchasePrice + ", tax=" + tax + ", status=" + status + ", type="
				+ type + ", supplierName=" + supplierName + ", supplierId=" + supplierId + ", productName="
				+ productName + ", supplierPoId=" + supplierPoId + ", inventorize=" + inventorize + ", isJunkInventory="
				+ isJunkInventory + ", created="
				+ created + ", inboundStorages=" + inboundStorages + ", modified=" + modified + ", batch=" + batch
				+ ", inboundItems=" + inboundItems ;
	}
}
