package com.moglix.wms.controller;

import com.google.gson.Gson;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.constants.OrderType;
import com.moglix.wms.producer.FifoProducer;
import com.moglix.wms.queueModel.InventoryAllocationRequest;
import com.moglix.wms.util.MailUtil;

import java.io.IOException;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sparsh saxena on 9/3/21
 */
@RestController
@RequestMapping("/api/test/")
public class TestController {

    private Logger logger = LogManager.getLogger(TestController.class);

    @Autowired
    private FifoProducer producer;

    @Value("${queue.allocation}")
    private String ALLOCATION_QUEUE;
    
    @Value("${queue.allocation.new}")
    private String NEW_ALLOCATION_QUEUE;
    
    @Autowired
    private MailUtil mailUtil;

    @GetMapping("ping")
    public String ping() {
        return "Welcome to Test Controller";
    }

    @GetMapping("queue")
    public boolean testQueue(@RequestParam(name="saleOrderId", required = false) Integer saleOrderId, @RequestParam(name="productId", required = false) Integer productId, @RequestParam(name="orderType", required = false) OrderType orderType) {
        logger.info("Request received to push data to queue");
        InventoryAllocationRequest input = saleOrderId != null ? new InventoryAllocationRequest(saleOrderId,null) : new InventoryAllocationRequest(null, productId);
       
        if(orderType.equals(OrderType.NEW)) {
        	producer.sendMessage(NEW_ALLOCATION_QUEUE, new Gson().toJson(input));
        }else if(orderType.equals(OrderType.BULK_INVOICING)) {
        	producer.sendMessage(ALLOCATION_QUEUE, new Gson().toJson(input));
        }
        
        return true;
    }
    
    @GetMapping("sendTestEmail")
    public boolean testQueue() throws IOException, MessagingException {
    	String mailContent = Constants.getInventoryEmailContent(
				"MSN123",
				"Elder wand", "Area 51");
    	mailUtil.sendMail(mailContent, Constants.INVENTORY_MAIL_SUBJECT,1);
        return true;
    }
}
