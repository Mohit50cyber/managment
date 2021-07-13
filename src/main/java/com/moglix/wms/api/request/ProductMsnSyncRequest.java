package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author sparsh saxena on 10/5/21
 */

@Data
@NoArgsConstructor
public class ProductMsnSyncRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6141765718522645417L;
	
	@NonNull
	private ArrayList<String> productIds = new ArrayList<>();
	
	public ProductMsnSyncRequest(ArrayList<String> productMSNList) {
		super();
		this.productIds = productMSNList;
	}

}
