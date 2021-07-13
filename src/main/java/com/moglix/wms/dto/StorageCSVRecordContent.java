package com.moglix.wms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StorageCSVRecordContent {
	
	
	@JsonProperty("State")
	private String stateName;
	
	@JsonProperty("State Code")
	private String stateCode;
	
	@JsonProperty("City")
	private String cityName;
	
	@JsonProperty("Warehouse")
	private String warehouseName;
	
	@JsonProperty("Zone")
	private String zoneName;
	
	@JsonProperty("Rack")
	private String rackName;
	
	@JsonProperty("Bin")
	private String binName;
	
	@JsonProperty("Storage Location")
	private String storageLocationName;
	
	@JsonProperty("Height (cm)")
	private String height;
	
	@JsonProperty("Depth (cm)")
	private String depth;
	
	@JsonProperty("Width (cm)")
	private String width;

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getRackName() {
		return rackName;
	}

	public void setRackName(String rackName) {
		this.rackName = rackName;
	}

	public String getBinName() {
		return binName;
	}

	public void setBinName(String binName) {
		this.binName = binName;
	}

	public String getStorageLocationName() {
		return storageLocationName;
	}

	public void setStorageLocationName(String storageLocationName) {
		this.storageLocationName = storageLocationName;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}
}
