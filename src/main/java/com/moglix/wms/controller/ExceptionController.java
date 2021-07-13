package com.moglix.wms.controller;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.exception.CancelReturnPickupListException;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.exception.WMSExpiredInventoryException;
import com.moglix.wms.exception.WMSSecurtyException;
import com.moglix.wms.util.GenericUtils;

@ControllerAdvice
public class ExceptionController {

	private static final Logger logger = LogManager.getLogger(ExceptionController.class);

	@ExceptionHandler(Exception.class)
	public @ResponseBody BaseResponse handleException(Exception ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(
				"ERROR: Error occured in processing request. Caused By: " + ex.getMessage(), false,
				HttpStatus.INTERNAL_SERVER_ERROR);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
	}
	
	
	@ExceptionHandler(ConstraintViolationException.class)
	public @ResponseBody BaseResponse constraintViolationException(ConstraintViolationException ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(
				"ERROR: Bad Request. Caused By: " + ex.getMessage(), false,
				HttpStatus.BAD_REQUEST);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public @ResponseBody BaseResponse failedValidationException(MethodArgumentNotValidException ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(
				"ERROR: Bad Request. Caused By: " + ex.getMessage(), false,
				HttpStatus.BAD_REQUEST);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
    }
	
	@ExceptionHandler(WMSException.class)
	public @ResponseBody BaseResponse failedValidationException(WMSException ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(
				"ERROR: Bad Request. Caused By: " + ex.getMessage(), false,
				HttpStatus.BAD_REQUEST);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
    }
	
	@ExceptionHandler(WMSSecurtyException.class)
	public @ResponseBody BaseResponse failedSecurityException(WMSSecurtyException ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(
				"Forbidden: " + ex.getMessage(), false,
				HttpStatus.FORBIDDEN);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
    }
	
	@ExceptionHandler(LockAcquisitionException.class)
	public @ResponseBody BaseResponse failedLockingException(LockAcquisitionException ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(
				"ERROR: Something Went wrong on WMS. Please Retry", false,
				HttpStatus.INTERNAL_SERVER_ERROR);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
    }
	
	@ExceptionHandler(WMSExpiredInventoryException.class)
	public @ResponseBody BaseResponse inventoryExpiredException(WMSExpiredInventoryException ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(
				ex.getMessage(), false,
				HttpStatus.BAD_REQUEST);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
    }
	
	@ExceptionHandler(CancelReturnPickupListException.class)
	public @ResponseBody BaseResponse cancelReturnPickupListException(CancelReturnPickupListException ex) {
		logger.info("in handleException method");
		BaseResponse response = GenericUtils.getResponseMessage(ex.getMessage(), false, HttpStatus.PRECONDITION_FAILED);
		logger.error("Error Occured:" + ex.toString() + "\n" + ExceptionUtils.getStackTrace(ex));
		return response;
    }

}