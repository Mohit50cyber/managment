package com.moglix.wms.api.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.validator.CheckValidWarehouse;

import java.io.Serializable;
import java.util.List;

public class CreatePacketRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1451497524061752605L;

	@NotNull
	private Integer emsPacketId;
	
	private String invoiceNumber;

	@NotNull
	@Size(min = 1)
	@Valid
	private List<EMSOrderItem> emsOrderItemIds;
	
	@NotNull
	private Integer warehouseId;

	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}

	public List<EMSOrderItem> getEmsOrderItemIds() {
		return emsOrderItemIds;
	}

	public void setEmsOrderItemIds(List<EMSOrderItem> emsOrderItemIds) {
		this.emsOrderItemIds = emsOrderItemIds;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public static class EMSOrderItem implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1945069481830048145L;
		

		@NotNull
		private Double quantity;

		@NotNull
		private Integer emsOrderItemId;

		public Double getQuantity() {
			return quantity;
		}

		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}

		public Integer getEmsOrderItemId() {
			return emsOrderItemId;
		}

		public void setEmsOrderItemId(Integer emsOrderItemId) {
			this.emsOrderItemId = emsOrderItemId;
		}

		@Override
		public String toString() {
			return "EMSOrderItem{" +
					"quantity=" + quantity +
					", emsOrderItemId=" + emsOrderItemId +
					'}';
		}
	}

	@Override
	public String toString() {
		return "CreatePacketRequest{" +
				"emsPacketId=" + emsPacketId +
				", invoiceNumber='" + invoiceNumber + '\'' +
				", emsOrderItemIds=" + emsOrderItemIds +
				", warehouseId=" + warehouseId +
				'}';
	}
}
