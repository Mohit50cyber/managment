package com.moglix.wms.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.moglix.wms.consumer.InventoryAllocationConsumer;
import com.moglix.wms.consumer.InventoryAllocationConsumerV2;

/**
 * @author pankaj on 13/5/19
 */
@Configuration
@Component
public class ConsumerThreadConfig {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerThreadConfig.class);

    @Autowired
    private InventoryAllocationConsumer consumer;
   
    @Autowired
    private InventoryAllocationConsumerV2 consumerV2;
    
    @Value("${spring.consumer.enabled}")
    private Boolean isConsumerEnabled;

  //  private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Thread thread;
    private Thread newthread;

    @PostConstruct
    public void init() {
       // executorService.submit(consumer);
    	logger.info("***** Inside INIT Method() ***** ");
    	if(isConsumerEnabled) {
	    	
    		try {
		    	
	    		thread = new Thread(consumer);
		    	thread.start();
		    	logger.info("Start thread for old consumer ");
		    			    	
		    	newthread = new Thread(consumerV2);
		    	newthread.start();
		    	logger.info("Start thread for consumer V2 ");
		    	
		        addShutDown();
		        
    		}catch(Exception e){
    			
    			logger.info("Exception caught in init Method :: " + e.getMessage());
    		}
    	}
    }

    private void addShutDown() {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            logger.info("App Forcefully killed. Shutting down open consumers and executors");
//            executorService.shutdown();
//        }));
    	thread.interrupt();
    	newthread.interrupt();
    }
}
