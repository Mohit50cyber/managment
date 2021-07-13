package com.moglix.wms.service;

import org.springframework.http.ResponseEntity;

import com.moglix.wms.api.response.GetProductDetailsByCatalogResponse;

/**
 * @author sparsh saxena on 10/5/21
 */
public interface ICatalogService {

	ResponseEntity<GetProductDetailsByCatalogResponse> syncProductMSN(String productMSN);
}
