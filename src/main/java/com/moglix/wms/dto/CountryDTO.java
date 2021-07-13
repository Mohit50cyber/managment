package com.moglix.wms.dto;

import java.util.Date;

import com.moglix.wms.entities.Country;

/**
 * @author sparsh saxena on 22/4/21
 */
public class CountryDTO {
	
	private Integer id;
    private String countryName;
    private String code;
    private Date created;
    private Date modified;
    private Integer isoNumber;
	private boolean isActive;
	
	
	public CountryDTO() {
	
	}

	public CountryDTO( Country obj) {
		
		this.id          = obj.getId();
		this.countryName = obj.getName();
		this.code        = obj.getCode();
		this.created     = obj.getCreated();
		this.modified    = obj.getModified();
		this.isoNumber   = obj.getIsoNumber();
		this.isActive    = obj.isActive();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public Integer getIsoNumber() {
		return isoNumber;
	}

	public void setIsoNumber(Integer isoNumber) {
		this.isoNumber = isoNumber;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
}
