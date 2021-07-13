package com.moglix.wms.api.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

public class GetSuppliersByReturnPacketResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8587351797208144502L;
	
	public GetSuppliersByReturnPacketResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
	
	List<SupplierInfo>suppliers = new ArrayList<>();
	
	
	public List<SupplierInfo> getSuppliers() {
		return suppliers;
	}


	public void setSuppliers(List<SupplierInfo> suppliers) {
		this.suppliers = suppliers;
	}


	public static class SupplierInfo implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5426600742739800460L;

		@NotNull
		private Double quantity;
		
		private String supplierName;
		
		private Integer supplierId;
		
		private Integer supplierPoId;
		
		private Integer supplierPoItemId;
	
		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public String getSupplierName() {
			return supplierName;
		}

		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}

		public Integer getSupplierId() {
			return supplierId;
		}

		public void setSupplierId(Integer supplierId) {
			this.supplierId = supplierId;
		}
		
		public Integer getSupplierPoId() {
			return supplierPoId;
		}

		public void setSupplierPoId(Integer supplierPoId) {
			this.supplierPoId = supplierPoId;
		}

		public Integer getSupplierPoItemId() {
			return supplierPoItemId;
		}

		public void setSupplierPoItemId(Integer supplierPoItemId) {
			this.supplierPoItemId = supplierPoItemId;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}
			
			if(obj instanceof SupplierInfo) {
				SupplierInfo pi = (SupplierInfo) obj;
				return (Double.compare(pi.quantity, this.quantity) == 0 && Double.compare(pi.supplierId, this.supplierId) == 0 && Integer.compare(pi.supplierPoId, this.supplierPoId) == 0);
			}else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			int hashcode = 0;
			hashcode = supplierId*20;
			hashcode = supplierPoId * 20;
			hashcode += quantity.hashCode();
			return hashcode;
		}
	}

}
