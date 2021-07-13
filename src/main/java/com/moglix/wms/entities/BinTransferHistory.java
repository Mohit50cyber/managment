package com.moglix.wms.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.moglix.wms.constants.BinTransferStatus;

@Entity
@Table(name = "bin_transfer_history")
public class BinTransferHistory implements Serializable{

/**
*
*/
private static final long serialVersionUID = 839887611175633688L;

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

private String msn;
private Double quantity;

@Enumerated(EnumType.STRING)
private BinTransferStatus status;

@Column(nullable = false, updatable = false)
@CreationTimestamp
@Temporal(TemporalType.TIMESTAMP)
private Date created;

@Column(nullable = false)
@UpdateTimestamp
@Temporal(TemporalType.TIMESTAMP)
private Date modified;

@ManyToOne(cascade=CascadeType.ALL)
@JoinColumn(name="from_storage_location_id")
private StorageLocation fromStorageLocation;

@ManyToOne(cascade=CascadeType.ALL)
@JoinColumn(name="to_storage_location_id")
private StorageLocation toStorageLocation;

@ManyToOne(cascade=CascadeType.ALL)
@JoinColumn(name="bin_transfer_id")
private BinTransferDao binTransfers;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getMsn() {
return msn;
}

public void setMsn(String msn) {
this.msn = msn;
}

public Double getQuantity() {
return quantity;
}

public void setQuantity(Double quantity) {
this.quantity = quantity;
}

public BinTransferStatus getStatus() {
return status;
}

public void setStatus(BinTransferStatus status) {
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

public StorageLocation getFromStorageLocation() {
return fromStorageLocation;
}

public void setFromStorageLocation(StorageLocation fromStorageLocation) {
this.fromStorageLocation = fromStorageLocation;
}

public StorageLocation getToStorageLocation() {
return toStorageLocation;
}

public void setToStorageLocation(StorageLocation toStorageLocation) {
this.toStorageLocation = toStorageLocation;
}

public BinTransferDao getBinTransfers() {
return binTransfers;
}

public void setBinTransfers(BinTransferDao binTransfers) {
this.binTransfers = binTransfers;
}

@Override
public String toString() {
return "BinTransferHistory [id=" + id + ", msn=" + msn + ", quantity=" + quantity + ", status=" + status
+ ", created=" + created + ", modified=" + modified + ", fromStorageLocation=" + fromStorageLocation
+ ", toStorageLocation=" + toStorageLocation + ", binTransfers=" + binTransfers + "]";
}





}
