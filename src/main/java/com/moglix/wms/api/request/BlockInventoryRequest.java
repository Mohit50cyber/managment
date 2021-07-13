package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

import com.moglix.wms.constants.BlockInventoryAction;

public class BlockInventoryRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1919405131627562826L;

	@NotNull
	private Integer warehouseId;
	
	@NotNull
	private String productMsn;
	
	@NotNull
	private Double quantity;
	
	@NotNull
	private String bulkInvoiceId;
	
	@NotNull
	private String email;
	
	@NotNull
	private BlockInventoryAction action;
	
	private String uniqueblockid;

	
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
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

	public String getBulkInvoiceId() {
		return bulkInvoiceId;
	}

	public void setBulkInvoiceId(String bulkInvoiceId) {
		this.bulkInvoiceId = bulkInvoiceId;
	}

	public BlockInventoryAction getAction() {
		return action;
	}

	public void setAction(BlockInventoryAction action) {
		this.action = action;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUniqueblockid() {
		return uniqueblockid;
	}

	public void setUniqueblockid(String uniqueblockid) {
		this.uniqueblockid = uniqueblockid;
	}

	@Override
	public String toString() {
		return "BlockInventoryRequest [warehouseId=" + warehouseId + ", productMsn=" + productMsn + ", quantity="
				+ quantity + ", bulkInvoiceId=" + bulkInvoiceId + ", email=" + email + ", action=" + action
				+ ", uniqueblockid=" + uniqueblockid + "]";
	}

	
}
