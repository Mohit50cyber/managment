package com.moglix.wms.api.request;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.moglix.wms.api.request.CreateSaleOrderRequest.EmsOrderItem;

public class BuyersCreateSaleOrderRequest extends BaseRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4821411760932190002L;
	
	@NotNull
    private String orderId;

    @NotNull
    private Integer fulfillmentWarehouseId;
    
    private String bulkInvoiceId;
    
    private String uniqueblockid;

	@NotNull
    @Size(min=1)
    @Valid
    private List<EmsOrderItem> items = new ArrayList<>();

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getFulfillmentWarehouseId() {
		return fulfillmentWarehouseId;
	}

	public void setFulfillmentWarehouseId(Integer fulfillmentWarehouseId) {
		this.fulfillmentWarehouseId = fulfillmentWarehouseId;
	}

	public String getBulkInvoiceId() {
		return bulkInvoiceId;
	}

	public void setBulkInvoiceId(String bulkInvoiceId) {
		this.bulkInvoiceId = bulkInvoiceId;
	}

	public List<EmsOrderItem> getItems() {
		return items;
	}

	public void setItems(List<EmsOrderItem> items) {
		this.items = items;
	}
	
	public String getUniqueblockid() {
			return uniqueblockid;
	}

	public void setUniqueblockid(String uniqueblockid) {
			this.uniqueblockid = uniqueblockid;
	}

	@Override
	public String toString() {
		return "BuyersCreateSaleOrderRequest [orderId=" + orderId + ", fulfillmentWarehouseId=" + fulfillmentWarehouseId
				+ ", bulkInvoiceId=" + bulkInvoiceId + ", uniqueblockid=" + uniqueblockid + ", items=" + items + "]";
	}

	
}
