package com.moglix.wms.kafka.producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import com.moglix.wms.api.request.InventoryUpdateRequest;


/**
 * @author sparsh saxena on 10/5/21
 */

@Service
public class KafkaEmsSalesOpsUpdateProducer {

	private static final Logger logger = LogManager.getLogger(KafkaEmsSalesOpsUpdateProducer.class);
	
	@Autowired
    private KafkaTemplate<String, InventoryUpdateRequest> kafkaTemplate;
	
    @Value("${kafka.salesops.inventory.update.topic}")
    private String EMS_SALESOPS_INVENTORY_UPDATE_TOPIC;
    
    public void sendRequest(InventoryUpdateRequest inventoryUpdateRequest){
    	
        logger.info(String.format("Publish message to %s : %s ", EMS_SALESOPS_INVENTORY_UPDATE_TOPIC, inventoryUpdateRequest));
       
        ListenableFuture<SendResult<String, InventoryUpdateRequest>> future = kafkaTemplate.send(EMS_SALESOPS_INVENTORY_UPDATE_TOPIC, inventoryUpdateRequest);
        
        logger.info(String.format("Published message to %s : %s ",EMS_SALESOPS_INVENTORY_UPDATE_TOPIC, inventoryUpdateRequest));
        
        future.addCallback(new ListenableFutureCallback<SendResult<String, InventoryUpdateRequest>>() {
            @Override
            public void onSuccess(SendResult<String, InventoryUpdateRequest> result) {
            	logger.info(String.format("Kafka publisher SUCCESS : topic %s sent message : %s with offset %s", EMS_SALESOPS_INVENTORY_UPDATE_TOPIC, inventoryUpdateRequest, result.getRecordMetadata().offset()));
            }

            @Override
            public void onFailure(Throwable ex) {
            	logger.warn(String.format("Kafka publisher FAILED : topic %s sent message : %s due to %s ,[Rolling back stuff!!!]", EMS_SALESOPS_INVENTORY_UPDATE_TOPIC, inventoryUpdateRequest, ex.getMessage()));
            }
        });
    }
}