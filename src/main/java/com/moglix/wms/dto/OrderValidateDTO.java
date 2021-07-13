package com.moglix.wms.dto;

import java.util.List;

/**
 * @author sparsh saxena on 10/5/21
 */
public class OrderValidateDTO {
	
    private List<String> orderValidations;

    public OrderValidateDTO(List<String> orderValidations) {
        this.orderValidations = orderValidations;
    }

    public List<String> getOrderValidations() {
		return orderValidations;
	}

	public void setOrderValidations(List<String> orderValidations) {
		this.orderValidations = orderValidations;
	}
}
