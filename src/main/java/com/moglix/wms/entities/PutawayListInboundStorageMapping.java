package com.moglix.wms.entities;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "putaway_list_inbound_storage_mapping")
public class PutawayListInboundStorageMapping implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6743394091346163221L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "putaway_list_id")
	@JsonBackReference	
	private PutawayList putawayList;
	
	@OneToOne
	@JoinColumn(name="inbound_storage_id")
	private InboundStorage inboundStorage;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public PutawayList getPutawayList() {
		return putawayList;
	}

	public void setPutawayList(PutawayList putawayList) {
		this.putawayList = putawayList;
	}

	public InboundStorage getInboundStorage() {
		return inboundStorage;
	}

	public void setInboundStorage(InboundStorage inboundStorage) {
		this.inboundStorage = inboundStorage;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	private Date modified = new Date();
}
