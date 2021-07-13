package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

public class GetProductByBarcodeRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6765202488078841019L;
	
	@NotNull
	private String barcode;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

}
