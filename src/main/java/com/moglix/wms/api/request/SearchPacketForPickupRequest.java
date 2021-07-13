package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

import com.moglix.wms.constants.PacketStatus;

/**
 * @author pankaj on 14/5/19
 */
public class SearchPacketForPickupRequest extends BaseRequest {
    private static final long serialVersionUID = 966953477855004936L;

    @NotNull
    private Integer warehouseId;
    
    private String invoiceNumber;
    
    private PacketStatus status;

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	public PacketStatus getStatus() {
		return status;
	}

	public void setStatus(PacketStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "SearchPacketForPickupRequest [warehouseId=" + warehouseId + ", invoiceNumber=" + invoiceNumber
				+ ", status=" + status + "]";
	}
	
	
}
