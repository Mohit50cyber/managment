package com.moglix.wms.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.service.IWarehouseService;
import com.moglix.wms.util.ServiceUtils;

public class WarehouseValidator implements ConstraintValidator<CheckValidWarehouse, Integer> {

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		
		IWarehouseService warehouseService = ServiceUtils.getWarehouseService();

		Warehouse warehouse = warehouseService.getById(value);
		
		return (warehouse != null);
	}

}
