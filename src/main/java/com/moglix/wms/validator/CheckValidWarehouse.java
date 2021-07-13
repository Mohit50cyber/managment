package com.moglix.wms.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = WarehouseValidator.class)
public @interface CheckValidWarehouse {
	String message() default "Invalid Warehouse ID. Warehouse Doesn't exist";
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
