package com.moglix.wms.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.api.request.ProductInput.ItemRefDetail;
import com.moglix.wms.api.request.SaleOrderSupplierPurchaseOrderMappingRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.entities.SaleOrderSupplierPurchaseOrderMapping;
import com.moglix.wms.mapper.SaleOrderSupplierPurchaseOrderMapper;
import com.moglix.wms.repository.SaleOrderRepository;
import com.moglix.wms.repository.SaleOrderSupplierPurchaseOrderMappingRepository;
import com.moglix.wms.service.ISaleOrderSupplierPurchaseOrderMappingService;

/**
 * @author sparsh saxena on 9/3/21
 */

@Service("saleOrderSupplierPurchaseOrderMappingService")
public class SaleOrderSupplierPurchaseOrderMappingServiceImpl implements ISaleOrderSupplierPurchaseOrderMappingService{
	
	
	private Logger logger = LogManager.getLogger(SaleOrderSupplierPurchaseOrderMappingServiceImpl.class);
	
    @Autowired
    private SaleOrderRepository saleOrderRepository;
	
	@Autowired
	private SaleOrderSupplierPurchaseOrderMappingRepository saleOrderSupplierPurchaseOrderMappingRepository;

	@Override
	public BaseResponse saveMapping(SaleOrderSupplierPurchaseOrderMappingRequest request) {
		
		for (ProductInput prod : request.getProducts()) {
			
			List<ItemRefDetail> itemRefDetails = prod.getItemRefDetails();
			
			for(ItemRefDetail itemRefDetail : itemRefDetails) {
				
				logger.info("Checking SaleOrder against ItemRef :: " + itemRefDetail.getItemRef());
				
				SaleOrder saleOrder = saleOrderRepository.findByItemRef(itemRefDetail.getItemRef());
				if (saleOrder == null) {
					logger.info("SaleOrder not found against itemRef :: [" + itemRefDetail.getItemRef() + "] for mapping.");
				}
				else {
					
					SaleOrderSupplierPurchaseOrderMapping resultSet = saleOrderSupplierPurchaseOrderMappingRepository.findByItemRefAndSupplierPoId(itemRefDetail.getItemRef(), prod.getSupplierPoId());
					
					if (ObjectUtils.isEmpty(resultSet)) {
						resultSet = SaleOrderSupplierPurchaseOrderMapper.createEntityFromSaleOrderSupplierPurchaseOrderMappingRequest(request);
						resultSet.setSupplierPoId(prod.getSupplierPoId());
						resultSet.setProductID(saleOrder.getProduct().getId());
						resultSet.setProductMSN(prod.getProductMsn());
						resultSet.setProductName(prod.getProductName());
						resultSet.setSaleOrderId(saleOrder.getId());
						resultSet.setItemRef(itemRefDetail.getItemRef());
						resultSet.setOrderRef(saleOrder.getOrderRef());
						resultSet.setQuantity(itemRefDetail.getQuantity());
                    } else {
                    	resultSet.setQuantity(resultSet.getQuantity() + itemRefDetail.getQuantity());
                    }

                    saleOrderSupplierPurchaseOrderMappingRepository.save(resultSet);
                    logger.info("SaleOrderSupplierPurchaseOrderMapping saved against SaleOrder :: [" + saleOrder.getId() + "]");
				}
			}
		}
		return new BaseResponse("Order Details mapped successfully.", true, HttpStatus.OK.value());
	}
}
