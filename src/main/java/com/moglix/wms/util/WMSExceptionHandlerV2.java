package com.moglix.wms.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.moglix.wms.consumer.InventoryAllocationConsumerV2;

/**
 * @author sparsh saxena on 18/3/21
 */

@Component
public class WMSExceptionHandlerV2 implements UncaughtExceptionHandler {
	
	private static final Logger logger = LogManager.getLogger(WMSExceptionHandlerV2.class);
	  
	@Autowired
	private InventoryAllocationConsumerV2 consumerV2;
	
	public void uncaughtException(Thread t, Throwable e){
			
		logger.error("Exception occurred in InventoryAllocationConsumerV2 :",e);
		      
	    new Thread(consumerV2).start();
		      
		logger.info("new thread of inventory allocation consumer V2 started after Exception in InventoryAllocationConsumerV2 ");
	}
}


