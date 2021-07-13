package com.moglix.wms.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.moglix.wms.constants.Constants;

public class TaxRateValidator implements ConstraintValidator<CheckValidTaxRate, Double>{

	@Override
	public boolean isValid(Double value, ConstraintValidatorContext context) {
		return Constants.getGstValues().contains(value);
	}

}
