package com.moglix.wms.service.impl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.moglix.wms.api.request.ProductMsnSyncRequest;
import com.moglix.wms.api.response.GetProductDetailsByCatalogResponse;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.service.ICatalogService;

/**
 * @author sparsh saxena on 10/5/21
 */
@Service("catalogService")
public class CatalogueServiceImpl implements ICatalogService{
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogueServiceImpl.class);
	
    @Value("${catalogue.host.url}")
    private String CATALOGUE_HOST;

	@Override
	public ResponseEntity<GetProductDetailsByCatalogResponse> syncProductMSN(String productMSN) {
		
		ResponseEntity<GetProductDetailsByCatalogResponse> response = null;
		try {
			RestTemplate restTemplate = new RestTemplate();
			ArrayList<String> productMSNList = new ArrayList<>();
			productMSNList.add(productMSN);
			ProductMsnSyncRequest productMsnSyncRequest = new ProductMsnSyncRequest(productMSNList);
			logger.info(String.format("Catalog Request URL :: %s :: Catalog Request for fetch productMSN :: %s", CATALOGUE_HOST + Constants.PRODUCTMSN_SYNC_API, new Gson().toJson(productMsnSyncRequest)));
			response = restTemplate.postForEntity(CATALOGUE_HOST + Constants.PRODUCTMSN_SYNC_API, productMsnSyncRequest, GetProductDetailsByCatalogResponse.class);
			logger.info(String.format("Catalog Response for fetch productMSN :: %s", new Gson().toJson(response)));
		} 
		catch (RestClientException e) {
			
			logger.error(String.format("Something went wrong while fetching productMSN details :: %s from catalogue system!, %s", productMSN, e.getMessage()));
            e.printStackTrace();
           
            response.getBody().setStatusCode(500);
            response.getBody().setStatus(false);
            response.getBody().setMessage("Something went wrong while fetching productMSN details");
            
            return response;
		}
		
		return response;
	}
}
