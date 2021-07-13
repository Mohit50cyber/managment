package com.moglix.wms.api.response;


import java.sql.Date;
import java.util.List;

public class GenerateBarcodeResponse extends BaseResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3437620147642823987L;

	public GenerateBarcodeResponse(String message, boolean status, int code) {
		super(message, status, code);
	}
	
	private List<Result> results;
	
	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public static class Result{
		private String barcode;
		private Integer serialNumber;
		private String uom;
		
		private String productBrand;
		
		private String productmsn;
		
		private String suppliername;
		
		private Integer supplierPoId;
		
		private Integer quantity;
		
		private Integer lotnumber;
		
		private Date expirydate;
		
		private String productDescription;
		
		public String getProductmsn() {
			return productmsn;
		}
		public void setProductmsn(String productmsn) {
			this.productmsn = productmsn;
		}
		public String getSuppliername() {
			return suppliername;
		}
		public void setSuppliername(String suppliername) {
			this.suppliername = suppliername;
		}
		public Integer getSupplierPoId() {
			return supplierPoId;
		}
		public void setSupplierPoId(Integer supplierPoId) {
			this.supplierPoId = supplierPoId;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public Integer getLotnumber() {
			return lotnumber;
		}
		public void setLotnumber(Integer lotnumber) {
			this.lotnumber = lotnumber;
		}
		public Date getExpirydate() {
			return expirydate;
		}
		public void setExpirydate(Date expirydate) {
			this.expirydate = expirydate;
		}

		public String getBarcode() {
			return barcode;
		}
		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}
		public Integer getSerialNumber() {
			return serialNumber;
		}
		public void setSerialNumber(Integer serialNumber) {
			this.serialNumber = serialNumber;
		}
		public String getUom() {
			return uom;
		}
		public void setUom(String uom) {
			this.uom = uom;
		}
		public String getProductBrand() {
			return productBrand;
		}
		public void setProductBrand(String productBrand) {
			this.productBrand = productBrand;
		}
		public String getProductDescription() {
			return productDescription;
		}
		public void setProductDescription(String productDescription) {
			this.productDescription = productDescription;
		}
	}
}
