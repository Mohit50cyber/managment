package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.List;

import com.moglix.wms.constants.InventoriseAction;

public class DeductInventorisableRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3502022335660511887L;
	
	private Integer emsReturnId;
	
	private Integer supplierId;
	
	private InventoriseAction action;
	
	public InventoriseAction getAction() {
		return action;
	}

	public void setAction(InventoriseAction action) {
		this.action = action;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public List<ProductQuantity> getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(List<ProductQuantity> productQuantity) {
		this.productQuantity = productQuantity;
	}
	
	private List<ProductQuantity> productQuantity;

	@Override
	public String toString() {
		return "DeductInventorisableRequest [emsReturnId=" + emsReturnId + ", supplierId=" + supplierId + ", action="
				+ action + ", productQuantity=" + productQuantity + "]";
	}
}
