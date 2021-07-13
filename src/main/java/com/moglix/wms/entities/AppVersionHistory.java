package com.moglix.wms.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.moglix.wms.api.response.CountWarehouseDataResponse;

import lombok.Data;

@Entity
@Data
@Table(name = "Appversion")
public class AppVersionHistory {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "androidversion")
    private String androidVersion;
	
	@Column(name = "iosversion")
    private String iosVersion;
	
	@Column(name="active")
    private boolean active;
	
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date created = new Date();
	
}
