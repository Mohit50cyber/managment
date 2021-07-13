package com.moglix.wms.kafka.producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.moglix.wms.api.request.CreatePacketRequest;
import com.moglix.wms.constants.Constants;

@Service
public class KafkaInvoiceProducer {

	Logger log = LogManager.getLogger(KafkaInvoiceProducer.class);
	
	@Autowired
    private KafkaTemplate<String, CreatePacketRequest> kafkaTemplate;
	
    private final String WMS_RECONCILE_TOPIC = Constants.WMS_RECONCILE_TOPIC;

    public void send( CreatePacketRequest invoiceRequest )
    {
        log.debug( "Sending kafka message to topic: {}", WMS_RECONCILE_TOPIC );
        kafkaTemplate.send( WMS_RECONCILE_TOPIC, invoiceRequest );
        log.debug( "Message Sent: {}", invoiceRequest );
    }
}
