package com.moglix.wms.consumer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.queueModel.InventoryAllocationRequest;
import com.moglix.wms.service.IInventoryService;
import com.moglix.wms.util.JsonUtil;
import com.moglix.wms.util.WMSExceptionHandlerV2;


/**
 * @author sparsh saxena on 9/3/21
 */
@Component
public class InventoryAllocationConsumerV2 implements Runnable {
   
	private static final Logger logger = LogManager.getLogger(InventoryAllocationConsumerV2.class);

    @Autowired
	@Qualifier("inventoryServiceV2")
    private IInventoryService inventoryServiceV2;

    private AtomicInteger threshold = new AtomicInteger(0);
    
    @Autowired
    @Qualifier("amazonSqsNew")
    private AmazonSQS sqs;
    
    @Autowired
    private WMSExceptionHandlerV2 wMSExceptionHandlerV2;
  
    @Value("${queue.allocation.new}")
    private String NEW_ALLOCATION_QUEUE;

    @Override
    public void run() {
    	
    	logger.info("Inside run() :1: InventoryAllocationConsumerV2");
    	
    	Thread.currentThread().setUncaughtExceptionHandler(wMSExceptionHandlerV2);
    	
    	logger.info("Inside run() :2: InventoryAllocationConsumerV2");
    	
        String queue_url = sqs.getQueueUrl(NEW_ALLOCATION_QUEUE).getQueueUrl();
        
        logger.info("New Queue URL :: " + queue_url);
       
        final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queue_url).withMaxNumberOfMessages(1).withWaitTimeSeconds(5);
        
        logger.info("ReceiveMessageRequest :: " + receiveMessageRequest);
        logger.info("Inventory Allocation Consumer V2 is Running...!!!");
        
        while (!Thread.currentThread().isInterrupted()) {
           
        	final List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        	
        	logger.info("Consumer recieved messages form Inventory Allocation Consumer V2 ...!!! with size : [" + messages.size() + "] :: Received messages --> [" + messages + "]");
           
        	for (Message message : messages) {
               
        		try {
                  
        			logger.info("Received Data from Normal Order Queue :: " + message.getBody() + "At [" + DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT).format(LocalDateTime.now()) + "]");
                  
        			InventoryAllocationRequest data = JsonUtil.readObject(message.getBody(), InventoryAllocationRequest.class);
                    
                    boolean result = false;
                    if (data != null) {
                    	
                    	if(data.getSaleOrderId() != null) {
                    		logger.info("No Delay for sale order queue push.");
                    		result = inventoryServiceV2.allocateInventoryForSaleOrder(data.getSaleOrderId());
                    	}
                    	else if(data.getProductId() != null) {
                    		logger.info("Sleeping for 3 seconds to wait for inventory because product is pushed.");
                            Thread.sleep(3000);
                    		result = inventoryServiceV2.allocateInventoryForProductId(data.getProductId());
                    	}
                        if(result) {
                        	logger.info("Received Data executed successfully.");
                            sqs.deleteMessage(queue_url, message.getReceiptHandle());
                            logger.info("Deleted message from Normal Order Queue :: [" + message.getBody() + "] At [" + DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT).format(LocalDateTime.now()) + "]");
                        } 
                        else {
                            logger.info("Received Data execution failed.");
                        }
                    }
                    threshold.set(0);
                    // throw new Throwable("custom throwable exception");
                }
        		catch(WMSException ex) {
                	
        			logger.error("Error occured in sending responses to sales ops API or EMS API. Deleting message from queue.", ex);
            		sqs.deleteMessage(queue_url, message.getReceiptHandle());
            		//logger.info("Deleted Order from queue: " + JsonUtil.readObject(message.getBody(), InventoryAllocationRequest.class).toString());
            		logger.info("Deleted message from Normal Order Queue :: [" + message.getBody() + "] At [" + DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT).format(LocalDateTime.now()) + "]");
                	threshold.incrementAndGet();
                	
                	if(threshold.get()>=3) {
                		logger.error("Got 3 consecutive errors from order allocation API. Take some action!", ex);
                		//Send an email about graceful interrupt of consumer
                		//Thread.currentThread().interrupt();
                	}
                }
                catch (Exception e) {
                    logger.info("Received Data failed :: " + e.toString());
                    logger.error("Exception :: " + ExceptionUtils.getStackTrace(e));
                    sqs.deleteMessage(queue_url, message.getReceiptHandle());
                    logger.info("Deleted message from Normal Order Queue :: [" + message.getBody() + "] At [" + DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT).format(LocalDateTime.now()) + "]");
                }
            }
        }
        
        logger.warn("V2 Consumer thread is down. Restart Application Now.");
        //Send an email about abnormal shutdown of consumer
    }
}

