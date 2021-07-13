package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 6/5/19
 */
public class CreateSaleOrderResponse extends BaseResponse {
    private static final long serialVersionUID = 1894823295932320635L;
    
    private List<Integer>saleOrderIds = new ArrayList<>();
    
    private List<Integer>emsOrderItemIds = new ArrayList<>();

	public List<Integer> getSaleOrderIds() {
		return saleOrderIds;
	}

	public void setSaleOrderIds(List<Integer> saleOrderIds) {
		this.saleOrderIds = saleOrderIds;
	}

	public List<Integer> getEmsOrderItemIds() {
		return emsOrderItemIds;
	}

	public void setEmsOrderItemIds(List<Integer> emsOrderItemIds) {
		this.emsOrderItemIds = emsOrderItemIds;
	}
}
