package com.moglix.wms.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pankaj on 14/5/19
 */

@Configuration
public class AmazonSqsConfig {
	
	@Value("${queue.allocation}")
    private String ALLOCATION_QUEUE;
		
    private static final Logger logger = LoggerFactory.getLogger(AmazonSqsConfig.class);


    @Bean("amazonSqs")
    public AmazonSQS amazonSQS(@Value("${cloud.aws.credentials.accessKey}") String awsAccessKey,
                               @Value("${cloud.aws.credentials.secretKey}") String awsSecretKey,
                               @Value("${cloud.aws.region.static}") String awsRegion){

        AmazonSQS amazonSQS= null;
        try {
            amazonSQS = AmazonSQSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                    .withRegion(awsRegion)
                    .build();
            logger.info("SQS Connection Successful:" + amazonSQS.getQueueUrl(ALLOCATION_QUEUE));
        } 
        catch (Exception e) {
            logger.info("Exception: " + e.toString() + ExceptionUtils.getStackTrace(e));
        }

        return amazonSQS;
    }
}
