package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moglix.wms.validator.CheckValidWarehouse;

public class StockTransferRequest extends BaseRequest {

	private static Gson gson = new GsonBuilder().create();
	/**
	*
	*/
	private static final long serialVersionUID = 6573147684974308918L;

	Location sourceLocation;

	private List<DestLocation> destinationtLocations;

	String productMsn;

	private String email;

	private Double quantity;

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public Location getSourceLocation() {
		return sourceLocation;
	}

	public void setSourceLocation(Location sourceLocation) {
		this.sourceLocation = sourceLocation;
	}

	public List<DestLocation> getDestinationtLocations() {
		return destinationtLocations;
	}

	public void setDestinationtLocations(List<DestLocation> destinationtLocations) {
		this.destinationtLocations = destinationtLocations;
	}

	public static class Location implements Serializable {
		/**
		*
		*/
		private static final long serialVersionUID = -8246013102392286625L;

		@CheckValidWarehouse
		private Integer warehouseId;

		private Integer zoneId;

		private Integer rackId;

		private Integer binId;

		public Integer getWarehouseId() {
			return warehouseId;
		}

		@Override
		public String toString() {
			return gson.toJson(this);
		}

		public void setWarehouseId(Integer warehouseId) {
			this.warehouseId = warehouseId;
		}

		public Integer getRackId() {
			return rackId;
		}

		public void setRackId(Integer rackId) {
			this.rackId = rackId;
		}

		public Integer getBinId() {
			return binId;
		}

		public void setBinId(Integer binId) {
			this.binId = binId;
		}

		public Integer getZoneId() {
			return zoneId;
		}

		public void setZoneId(Integer zoneId) {
			this.zoneId = zoneId;
		}
	}

	public static class DestLocation implements Serializable {
		/**
		*
		*/
		private static final long serialVersionUID = -8246013102392286625L;

		private Double quantity;

		private Integer storageLocationId;

		@Override
		public String toString() {
			return gson.toJson(this);
		}

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public Integer getStorageLocationId() {
			return storageLocationId;
		}

		public void setStorageLocationId(Integer storageLocationId) {
			this.storageLocationId = storageLocationId;
		}

	}

}