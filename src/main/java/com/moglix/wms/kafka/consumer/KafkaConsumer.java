package com.moglix.wms.kafka.consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;
import com.moglix.wms.constants.OrderType;
import com.moglix.wms.dto.SalesOpsOrderDTO;
import com.moglix.wms.service.ISaleOrderService;

/**
 * @author sparsh saxena on 10/5/21
 */
@Service
public class KafkaConsumer {
	
   @Autowired
   @Qualifier("saleOrderService")
   private ISaleOrderService saleOrderService;
	
	private static final Logger logger = LogManager.getLogger(KafkaConsumer.class);
	
	@KafkaListener (topics = "${kafka.salesops.saleorder.topic}", groupId = "${kafka.salesops.saleorder.group}", autoStartup = "${kafka.enable}")
    public void listenOrderFromSalesOps(String message, Acknowledgment acknowledgment) {
       
		logger.info("SalesOps consumer message :: [" + message + "]");

		try {
        	SalesOpsOrderDTO salesOpsOrderDTO = new Gson().fromJson(message, SalesOpsOrderDTO.class);
            logger.info("Order received from salesops topic : " + ((!ObjectUtils.isEmpty(salesOpsOrderDTO) && !ObjectUtils.isEmpty(salesOpsOrderDTO.getOrderDetails().getOrderRef())) ? salesOpsOrderDTO.getOrderDetails().getOrderRef() : null));
        	
            if(!ObjectUtils.isEmpty(salesOpsOrderDTO) && (OrderType.valueOf(salesOpsOrderDTO.getOrderDetails().getOrderType()) != OrderType.ABFRL)) {
            	logger.info("Order Processing for message :: [" + message + "]");
            	saleOrderService.createSaleOrderFromKafka(salesOpsOrderDTO);
            }
        } 
        catch (Exception e) {
            logger.info("Exception while processing saleorder message in consumer.");
            e.printStackTrace();
        }
        acknowledgment.acknowledge();
    }
}
