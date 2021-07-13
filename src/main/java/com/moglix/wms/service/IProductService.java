package com.moglix.wms.service;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.moglix.wms.api.request.CreateProductRequest;
import com.moglix.wms.api.response.CreateProductResponse;
import com.moglix.wms.api.response.GetProductByBarcodeResponse;
import com.moglix.wms.api.response.GetProductByPacketAndSupplierResponse;
import com.moglix.wms.api.response.GetProductByPacketResponse;
import com.moglix.wms.api.response.GetProductByReturnPacketAndSupplierResponse;
import com.moglix.wms.api.response.GetProductInventoryHistoryResponse;
import com.moglix.wms.api.response.GetProuductExpiryAndLotResponse;
import com.moglix.wms.entities.Product;

/**
 * @author pankaj on 29/4/19
 */
public interface IProductService {
	Product upsert(Product product);
	Product add(Product product);
	Product getById(Integer id);
	Product getByProductMsnAndUom(String productMsn, String uom);
	GetProductByBarcodeResponse getProductByBarcode(String barcode);
	Iterable<Product> findAllByIdIn(Set<Integer> ids);
	GetProductByPacketResponse getProductsByEMSPacketId(Integer emsPacketId, Integer emsreturnId);

	CreateProductResponse create(CreateProductRequest request);
	GetProductByPacketAndSupplierResponse getProductsByEMSPacketIdAndSupplierId(Integer emsPacketId,
			Integer supplierId);
	GetProductByReturnPacketAndSupplierResponse getProductsByEMSReturnIdAndSupplierId(Integer emsReturnId,
			Integer supplierId, Integer supplierPoId);
	GetProductInventoryHistoryResponse getProductInventoryHistory(String productMsn, Integer warehouseId, Pageable page);
	GetProuductExpiryAndLotResponse getExpiryAndLotDetails(String productMsn);
}
