package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "bin_transfer")
public class BinTransferDao implements Serializable {

/**
*
*/
private static final long serialVersionUID = -8704614947645262582L;

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

@Column(name = "transferred_by")
private String transferredBy;

@OneToMany(fetch = FetchType.LAZY, mappedBy = "binTransfers", cascade = CascadeType.ALL)
private List<BinTransferHistory> binTransferHistory = new ArrayList<>();

@Column(nullable = false, updatable = false)
@CreationTimestamp
@Temporal(TemporalType.TIMESTAMP)
private Date created;

@Column(nullable = false)
@UpdateTimestamp
@Temporal(TemporalType.TIMESTAMP)
private Date modified;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}



public String getTransferredBy() {
return transferredBy;
}

public void setTransferredBy(String transferredBy) {
this.transferredBy = transferredBy;
}

public List<BinTransferHistory> getBinTransferHistory() {
return binTransferHistory;
}

public void setBinTransferHistory(List<BinTransferHistory> binTransferHistory) {
this.binTransferHistory = binTransferHistory;
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

@Override
public String toString() {
return "BinDao [id=" + id + ", transferredBy=" + transferredBy + ", binTransferHistory=" + binTransferHistory
+ ", created=" + created + ", modified=" + modified + ", getId()=" + getId() + ", getTransferredBy()="
+ getTransferredBy() + ", getBinTransferHistory()=" + getBinTransferHistory() + ", getCreated()="
+ getCreated() + ", getModified()=" + getModified() + ", getClass()=" + getClass() + ", hashCode()="
+ hashCode() + ", toString()=" + super.toString() + "]";
}







}
