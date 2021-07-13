package com.moglix.wms.service;

import com.moglix.wms.dto.SalesOpsOrderDTO;
import com.moglix.wms.entities.Product;

/**
 * @author sparsh saxena on 10/5/21
 */

public interface IOrderValidationService {

	public boolean validateOrder(SalesOpsOrderDTO salesOpsOrderDTO);
	public Product validateProductMSN(String productMSN, String itemRef);
}
