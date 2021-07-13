package com.moglix.wms.api.request;

import java.util.List;

import com.moglix.wms.constants.CustomerDebitNoteType;
import com.moglix.wms.dto.CutomerDebitNoteReturnDetailDTO;

public class CreateCustomerDebitNoteRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4953733628358419826L;
	
	private String customerDebitNoteNumber;
	
	private Integer warehouseId;
	
	private String warehouseName;
	
	private CustomerDebitNoteType type;
	
	private List<CutomerDebitNoteReturnDetailDTO> customerDebitNoteDetails;

	public String getCustomerDebitNoteNumber() {
		return customerDebitNoteNumber;
	}

	public void setCustomerDebitNoteNumber(String customerDebitNoteNumber) {
		this.customerDebitNoteNumber = customerDebitNoteNumber;
	}

	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public List<CutomerDebitNoteReturnDetailDTO> getCustomerDebitNoteDetails() {
		return customerDebitNoteDetails;
	}

	public void setCustomerDebitNoteDetails(List<CutomerDebitNoteReturnDetailDTO> customerDebitNoteDetails) {
		this.customerDebitNoteDetails = customerDebitNoteDetails;
	}

	public CustomerDebitNoteType getType() {
		return type;
	}

	public void setType(CustomerDebitNoteType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "CreateCustomerDebitNoteRequest [customerDebitNoteNumber=" + customerDebitNoteNumber + ", warehouseId="
				+ warehouseId + ", warehouseName=" + warehouseName + ", type=" + type + ", customerDebitNoteDetails="
				+ customerDebitNoteDetails + "]";
	}
}
