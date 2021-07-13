package com.moglix.wms.dto;

import java.util.Date;

import com.moglix.wms.entities.Warehouse;

/**
 * @author pankaj on 30/4/19
 */
public class WarehouseDto {
   
	private Integer id;
    private String city;
    private String state;
    private String country;
    private String name;
    private String address1;
    private String address2;
    private String pincode;
    private String email;
    private String phone;
    private String panNo;
    private String gstin;
    private Date created;
    private Date modified;
    private Integer isoNumber;
	private boolean isActive;

    public WarehouseDto() {
    }

    public WarehouseDto(Warehouse obj) {
        this.id        = obj.getId();
        this.name      = obj.getName();
        this.city      = obj.getCity().getName();
        this.state     = obj.getCity().getState().getName();
        this.country   = obj.getCity().getState().getCountry().getName();
        this.address1  = obj.getAddress1();
        this.address2  = obj.getAddress2();
        this.pincode   = obj.getPincode();
        this.email     = obj.getEmail();
        this.phone     = obj.getPhone();
        this.panNo     = obj.getPanNo();
        this.gstin     = obj.getGstin();
        this.created   = obj.getCreated();
        this.modified  = obj.getModified();
        this.isoNumber = obj.getIsoNumber();
        this.isActive  = obj.getIsActive();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
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
