package com.moglix.wms.api.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetFreeInventoryResponse extends BaseResponse {

	/**
	 * 
	 */
	
	public GetFreeInventoryResponse(String message, boolean status, int code) {
		this.setMessage(message);
		this.setStatus(status);
		this.setCode(code);
	}
	
	
	private static final long serialVersionUID = -7903406631475986142L;
	
	private List<Inventory> inventories = new ArrayList<>();
	
	public List<Inventory> getInventory() {
		return inventories;
	}

	public void setInventory(List<Inventory> inventory) {
		this.inventories = inventory;
	}

	public static class Inventory implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4482093549449414930L;

		private String refNo;
		
		private Integer supplierPoId;
		
		private Double freeQuantity;
		
		private Integer supplierPoItemId;

		private Double allocatedQuantity;
		
		public Integer getSupplierPoItemId() {
			return supplierPoItemId;
		}

		public void setSupplierPoItemId(Integer supplierPoItemId) {
			this.supplierPoItemId = supplierPoItemId;
		}

		public String getRefNo() {
			return refNo;
		}

		public void setRefNo(String refNo) {
			this.refNo = refNo;
		}

		public Integer getSupplierPoId() {
			return supplierPoId;
		}

		public void setSupplierPoId(Integer supplierPoId) {
			this.supplierPoId = supplierPoId;
		}

		public Double getFreeQuantity() {
			return freeQuantity;
		}

		public void setFreeQuantity(Double freeQuantity) {
			this.freeQuantity = freeQuantity;
		}

		public Double getAllocatedQuantity() {
			return allocatedQuantity;
		}

		public void setAllocatedQuantity(Double allocatedQuantity) {
			this.allocatedQuantity = allocatedQuantity;
		}
	}

}
