package com.moglix.wms.mapper;

import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.dto.ProductCSVRecordContent;
import com.moglix.wms.entities.Product;

/**
 * @author pankaj on 29/4/19
 */
public class ProductMapper {

	private ProductMapper() {

	}

	public static Product createEntityFromInput(ProductInput input) {
		Product obj = new Product();
		if (input.getProductMsn() != null) {
			obj.setProductMsn(input.getProductMsn());
		}
		if (input.getDangerType() != null) {
			obj.setDangerType(input.getDangerType());
		}
		if (input.getType() != null) {
			obj.setType(input.getType());
		}
		if (input.getUom() != null) {
			obj.setUom(input.getUom());
		}
		if (input.getStorageType() != null) {
			obj.setStorageType(input.getStorageType());
		}
		if (input.getDangerType() != null) {
			obj.setDangerType(input.getDangerType());
		}
		if (input.getIsSerializedProduct() != null) {
			obj.setSerializedProduct(input.getIsSerializedProduct());
		}
		if (input.getProductName() != null) {
			obj.setProductName(input.getProductName());
		}
		return obj;
	}
	
	public static Product createProductFromCSVRecord(ProductCSVRecordContent input) {
		Product obj = new Product();
		if (input.getProductMsn() != null) {
			obj.setProductMsn(input.getProductMsn());
		}
		if (input.getDangerType() != null) {
			obj.setDangerType(input.getDangerType());
		}
		if (input.getProductType() != null) {
			obj.setType(input.getProductType());
		}
		
		if (input.getUom() != null) {
			obj.setUom(input.getUom());
		}
		if (input.getStorageType() != null) {
			obj.setStorageType(input.getStorageType());
		}
		if (input.getDangerType() != null) {
			obj.setDangerType(input.getDangerType());
		}
		if (input.getSerializedProduct() != null) {
			obj.setSerializedProduct(input.getSerializedProduct());
		}
		if (input.getProductName() != null) {
			obj.setProductName(input.getProductName());
		}
		
		if(input.getTotalQuantity() != null) {
			obj.setTotalQuantity(input.getCurrentQuantity());
		}
		
		if(input.getCurrentQuantity() != null) {
			obj.setCurrentQuantity(input.getCurrentQuantity());
		}
		
		if(input.getProductBrand() != null) {
			obj.setProductBrand(input.getProductBrand());
		}
		
		if(input.getExpiryDateManagementEnabled() != null) {
			obj.setExpiryDateManagementEnabled(input.getExpiryDateManagementEnabled());
		}
		
		if(input.getLotManagementEnabled() != null){
			obj.setLotManagementEnabled(input.getLotManagementEnabled());
		}
		

		return obj;
	}
	
	public static ProductInput createInputFromEntity(Product input) {
		ProductInput obj = new ProductInput();
		if (input.getProductMsn() != null) {
			obj.setProductMsn(input.getProductMsn());
		}
		if (input.getDangerType() != null) {
			obj.setDangerType(input.getDangerType());
		}
		if (input.getType() != null) {
			obj.setType(input.getType());
		}
		if (input.getUom() != null) {
			obj.setUom(input.getUom());
		}
		if (input.getStorageType() != null) {
			obj.setStorageType(input.getStorageType());
		}
		if (input.getDangerType() != null) {
			obj.setDangerType(input.getDangerType());
		}
		if (input.getSerializedProduct() != null) {
			obj.setIsSerializedProduct(input.getSerializedProduct());
		}
		if (input.getProductName() != null) {
			obj.setProductName(input.getProductName());
		}
		return obj;
	}
}
