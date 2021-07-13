package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.validator.CheckValidWarehouse;

public class ReturnBatchRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7540911471446821102L;

	public static class PacketQuantityMapping implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -226808624793921002L;
		private String productMsn;
		private Double quantity;
		private Integer orderItemId;
		private Integer emsReturnItemId;

		public Integer getEmsReturnItemId() {
			return emsReturnItemId;
		}
		public void setEmsReturnItemId(Integer emsReturnItemId) {
			this.emsReturnItemId = emsReturnItemId;
		}
		public String getProductMsn() {
			return productMsn;
		}
		public void setProductMsn(String productMsn) {
			this.productMsn = productMsn;
		}
		public Double getQuantity() {
			return quantity;
		}
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
		public Integer getOrderItemId() {
			return orderItemId;
		}
		public void setOrderItemId(Integer orderItemId) {
			this.orderItemId = orderItemId;
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
			return obj instanceof PacketQuantityMapping
					&& ((PacketQuantityMapping) obj).getProductMsn().equals(this.getProductMsn());
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}
		
	}
	
	private String inboundedBy;
	private String warehouseName; 
	
	@NotNull
	private Integer emsPacketId;
	
	@NotNull
	private String emsInvoiceNumber;
	
	@NotNull
	@CheckValidWarehouse
	private Integer warehouseId;
	
	@NotNull
	private Integer emsReturnId;
	
	
	public static class ReturnPacketCustom implements Serializable {
		private static final long serialVersionUID = -1451044151215774303L;
		private Set<ReturnPacketItemCustom> returnPacketItems;
		private Double totalQuantity;

		public static class ReturnPacketItemCustom implements Serializable {
			private static final long serialVersionUID = -1451044151215774303L;
			private Double quantity;

			private String productMsn;

			public Double getQuantity() {
				return quantity;
			}

			public void setQuantity(Double quantity) {
				this.quantity = quantity;
			}

			public String getProductMsn() {
				return productMsn;
			}

			public void setProductMsn(String productMsn) {
				this.productMsn = productMsn;
			}

			@Override
			public String toString() {
				return "ReturnPacketItemCustom [quantity=" + quantity + ", productMsn=" + productMsn + "]";
			}
		}

		public Set<ReturnPacketItemCustom> getReturnPacketItems() {
			return returnPacketItems;
		}

		public void setReturnPacketItems(Set<ReturnPacketItemCustom> returnPacketItems) {
			this.returnPacketItems = returnPacketItems;
		}

		public Double getTotalQuantity() {
			return totalQuantity;
		}

		public void setTotalQuantity(Double totalQuantity) {
			this.totalQuantity = totalQuantity;
		}

		@Override
		public String toString() {
			return "ReturnPacketCustom [returnPacketItems=" + returnPacketItems + ", totalQuantity=" + totalQuantity
					+ "]";
		}
	}

	@Override
	public String toString() {
		return "ReturnBatchRequest [inboundedBy=" + inboundedBy + ", warehouseName=" + warehouseName + ", emsPacketId="
				+ emsPacketId + ", warehouseId=" + warehouseId + ", emsReturnId=" + emsReturnId
				+ ", packetQuantityMapping=" + packetQuantityMapping + ", customerName=" + customerName + "]";
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	@NotNull
	@Size(min  = 1)
	private List<PacketQuantityMapping> packetQuantityMapping;
	
	@NotBlank
	private String customerName;
	
	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}

	public List<PacketQuantityMapping> getPacketQuantityMapping() {
		return packetQuantityMapping;
	}

	public void setPacketQuantityMapping(List<PacketQuantityMapping> packetQuantityMapping) {
		this.packetQuantityMapping = packetQuantityMapping;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getInboundedBy() {
		return inboundedBy;
	}

	public void setInboundedBy(String inboundedBy) {
		this.inboundedBy = inboundedBy;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public String getEmsInvoiceNumber() {
		return emsInvoiceNumber;
	}

	public void setEmsInvoiceNumber(String emsInvoiceNumber) {
		this.emsInvoiceNumber = emsInvoiceNumber;
	}
	
	
	
}
