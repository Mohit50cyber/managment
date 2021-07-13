package com.moglix.wms.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.moglix.wms.consumer.InventoryAllocationConsumer;

@Component
public class WMSExceptionHandler implements UncaughtExceptionHandler {
	
	private static final Logger logger = LogManager.getLogger(WMSExceptionHandler.class);
	 
	@Autowired
	private InventoryAllocationConsumer consumer;
	
	public void uncaughtException(Thread t, Throwable e){
			 
		logger.error("Exception occurred in InventoryAllocationConsumer :",e);
		      
	    new Thread(consumer).start();
		      
		logger.info("new thread of inventory allocation consumer started after Exception in InventoryAllocationConsumer ");
   }
}
