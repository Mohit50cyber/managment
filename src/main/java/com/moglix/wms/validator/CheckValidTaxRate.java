package com.moglix.wms.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = TaxRateValidator.class)
public @interface CheckValidTaxRate {
	String message() default "this value of tax rate is not allowed.";
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
