package com.moglix.wms.api.response;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StockTransferResponse extends BaseResponse {

	private static Gson gson = new GsonBuilder().create();

	/**
	*
	*/
	private static final long serialVersionUID = 7849098576749705464L;

	private String productMsn;
	private String productDesc;

	private SourceDestinationDetail sourceDetail;

	private List<SourceDestinationDetail> destinationDetails;

	public StockTransferResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}

	public StockTransferResponse(String productMsn, String productDesc, SourceDestinationDetail sourceDetail,
			List<SourceDestinationDetail> destinationDetails) {
		super();
		this.productMsn = productMsn;
		this.productDesc = productDesc;
		this.sourceDetail = sourceDetail;
		this.destinationDetails = destinationDetails;
	}

	public String getProductMsn() {
		return productMsn;
	}

	public void setProductMsn(String productMsn) {
		this.productMsn = productMsn;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public SourceDestinationDetail getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(SourceDestinationDetail sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	public List<SourceDestinationDetail> getDestinationDetails() {
		return destinationDetails;
	}

	public void setDestinationDetails(List<SourceDestinationDetail> destinationDetails) {
		this.destinationDetails = destinationDetails;
	}

	public StockTransferResponse() {
	}

	public static class SourceDestinationDetail implements Serializable {
		/**
		*
		*/
		private static final long serialVersionUID = -8246013102392286625L;

		private String storageLocationName;
		private String zoneName;
		private Integer zoneId;

		private Integer binId;

		private Double quantity;

		@Override
		public String toString() {
			return gson.toJson(this);
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

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public String getStorageLocationName() {
			return storageLocationName;
		}

		public void setStorageLocationName(String storageLocationName) {
			this.storageLocationName = storageLocationName;
		}

		public String getZoneName() {
			return zoneName;
		}

		public void setZoneName(String zoneName) {
			this.zoneName = zoneName;
		}

	}

}
