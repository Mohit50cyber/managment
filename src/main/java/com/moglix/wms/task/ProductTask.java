package com.moglix.wms.task;

import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.service.IProductInventoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author pankaj on 17/5/19
 */
@Component
public class ProductTask {

    private Logger logger = LogManager.getLogger(ProductTask.class);

    @Autowired
    private IProductInventoryService productInventoryService;

    //cron to update product inventory average age, starts at 00:01 everyday
    //@Scheduled(cron = "0 1 0 * * ?")
    public void updateAverageAge() {
        logger.info("Cron for updating product average age started");
        List<ProductInventory> list = productInventoryService.getAll();

        logger.info("products found: " + list.size());
        if(!CollectionUtils.isEmpty(list)) {
            for(ProductInventory productInventory : list) {
                productInventory.setAverageAge(productInventory.getAverageAge()+1);
                productInventoryService.upsert(productInventory);
            }
        }

        logger.info("Cron for updating product average age ended");
    }
}
