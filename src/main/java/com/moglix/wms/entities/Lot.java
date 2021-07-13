package com.moglix.wms.entities;

import java.io.Serializable;
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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "lot", uniqueConstraints=@UniqueConstraint(columnNames={"lot_no", "supplier_id", "product_msn"}))
public class Lot implements Serializable{

	/**
	 * 
	 * msn
	 * supplierid
	 * lot_numer
	 * 
	 * dbset   find  
	 * localset 
	 * 
	 */
	private static final long serialVersionUID = -3538966863816140254L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
    @Column(name = "lot_no")
    private String lotNumber;
    
    @Column(name = "product_msn")
    private String productMsn;
    
    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date created = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modified = new Date();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lot", cascade = CascadeType.ALL)
	@OrderBy
	@JsonManagedReference
	private Set<InboundLot> inboundLotAssoc;
    
    @Column(name = "lot_msn_supplier_id")
    private String lotMsnSupplierId;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
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

	
	
	public String getLotMsnSupplierId() {
		return lotMsnSupplierId;
	}

	public void setLotMsnSupplierId(String lotMsnSupplierId) {
		this.lotMsnSupplierId = lotMsnSupplierId;
	}

	public Set<InboundLot> getInboundLotAssoc() {
		return inboundLotAssoc;
	}

	public void setInboundLotAssoc(Set<InboundLot> inboundLotAssoc) {
		this.inboundLotAssoc = inboundLotAssoc;
	}
	
	

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Override
	public String toString() {
		return "Lot [id=" + id + ", lotNumber=" + lotNumber + ", productMsn=" + productMsn + ", supplierId="
				+ supplierId + ", created=" + created + ", modified=" + modified + ", inboundLotAssoc="
				+ inboundLotAssoc + ", lotMsnSupplierId=" + lotMsnSupplierId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lotMsnSupplierId == null) ? 0 : lotMsnSupplierId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lot other = (Lot) obj;
		if (lotMsnSupplierId == null) {
			if (other.lotMsnSupplierId != null)
				return false;
		} else if (!lotMsnSupplierId.equals(other.lotMsnSupplierId))
			return false;
		return true;
	}

	
	
	
	

	
	


    
}
