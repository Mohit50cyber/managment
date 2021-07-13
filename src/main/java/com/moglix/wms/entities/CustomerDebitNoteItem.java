package com.moglix.wms.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moglix.wms.validator.CheckValidTaxRate;

@Entity
@Table(name = "customer_debit_note_item")
public class CustomerDebitNoteItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "supplier_po_item_id")
	private Integer supplierPoItemId;
	
	@Column(name = "uom")
	private String uom; 
	
	@Column(name = "quantity")
	private Double quantity;
	
	@Column(name = "purchase_price")
	private Double purchasePrice;
	
	@Column(name = "tax")
	@CheckValidTaxRate
	private Double tax;
	
	@Column(name = "product_name",columnDefinition="TEXT")
	private String productName;
	
	@Column(name = "supplier_po_id")
	private Integer supplierPoId;
	
	@Column(name = "product_msn")
	private String productMsn;
	
	@Column(name = "ems_return_id")
    private Integer emsReturnId;
	
	@Column(name = "supplier_id")
    private Integer supplierId;
	
	@Column(name = "inbound_id")
    private Integer inboundId;
	
	@Column(name = "supplier_name")
    private String supplierName;
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_debit_note_id")
    @JsonBackReference
    private CustomerDebitNote customerDebitNote;

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

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getSupplierPoId() {
		return supplierPoId;
	}

	public void setSupplierPoId(Integer supplierPoId) {
		this.supplierPoId = supplierPoId;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public CustomerDebitNote getCustomerDebitNote() {
		return customerDebitNote;
	}

	public void setCustomerDebitNote(CustomerDebitNote customerDebitNote) {
		this.customerDebitNote = customerDebitNote;
	}

	public Integer getInboundId() {
		return inboundId;
	}

	public void setInboundId(Integer inboundId) {
		this.inboundId = inboundId;
	}
}
