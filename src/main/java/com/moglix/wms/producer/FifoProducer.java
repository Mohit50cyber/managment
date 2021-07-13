package com.moglix.wms.producer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author pankaj on 9/5/19
 */
@Component
public class FifoProducer {
    private static final Logger logger = LoggerFactory.getLogger(FifoProducer.class);

    @Autowired
    @Qualifier("amazonSqs")
    private AmazonSQS amazonSQS;

    @Value("${queue.allocation}")
    private String ALLOCATION_QUEUE;

    public void sendMessage(String queue, String json) {
        logger.info("Sending message[Queue: " + queue + "] = " + json);
        try {
            String queue_url = amazonSQS.getQueueUrl(queue).getQueueUrl();
            SendMessageRequest sendMessageFifoQueue = new SendMessageRequest()
                    .withQueueUrl(queue_url)
                    .withMessageBody(json).withMessageGroupId("Fifo_Inventory_Allocation")
                    .withMessageDeduplicationId(UUID.randomUUID().toString());
            SendMessageResult result = amazonSQS.sendMessage(sendMessageFifoQueue);
            logger.info("Message Sent to Queue with Message Id: " + result.getMessageId());
        } catch (Exception | Error e) {
            logger.error("Exception Occured: " + ExceptionUtils.getStackTrace(e));
        }
    }
}