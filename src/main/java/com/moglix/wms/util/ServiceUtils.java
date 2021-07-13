package com.moglix.wms.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.moglix.wms.service.IWarehouseService;

@Component
public class ServiceUtils {
  private static ServiceUtils instance;

  @Autowired
  private IWarehouseService warehouseService;

  /* Post constructor */

  @PostConstruct
  public void fillInstance() {
    instance = this;
  }

  /*static methods */

  public static IWarehouseService getWarehouseService() {
    return instance.warehouseService;
  }
}