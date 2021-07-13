package com.moglix.wms.dto;

import com.moglix.wms.entities.ProductInventory;

import java.util.Date;

/**
 * @author pankaj on 6/5/19
 */
public class ProductInventoryDto implements ProductInventoryDetailsDTO {
    private Integer id;
    private Double totalQuantity;
    private Double currentQuantity;
    private Double availableQuantity;
    private Double allocatedQuantity;
    private int averageAge;
    private Double averagePrice;
    private Integer productId;
    private String productName;
    private String productMsn;
    private Integer warehouseId;
    private String warehouseName;
    private String stateName;
    private String cityName;
    private Date created;
    private Date modified;
    private Double blockedQuantity;
    private Double DNInitiatedQuantity;
    private Double DNCreatedQuantity;
    private Double PackedQuantity;


    public Double getDNInitiatedQuantity() {
		return DNInitiatedQuantity;
	}

	public void setDNInitiatedQuantity(Double dNInitiatedQuantity) {
		DNInitiatedQuantity = dNInitiatedQuantity;
	}

	public Double getDNCreatedQuantity() {
		return DNCreatedQuantity;
	}

	public void setDNCreatedQuantity(Double dNCreatedQuantity) {
		DNCreatedQuantity = dNCreatedQuantity;
	}

	public ProductInventoryDto() {
    }
    
    public ProductInventoryDto(ProductInventoryDetailsDTO obj) {
    	this.productMsn = obj.getProductMsn();
    	this.availableQuantity = obj.getAvailableQuantity();
    	this.allocatedQuantity = obj.getAllocatedQuantity();
    	this.currentQuantity = obj.getCurrentQuantity();
    	this.warehouseId = obj.getWarehouseId();
    	this.productName = obj.getProductName();
    	this.productId = obj.getProductId();
    	this.warehouseName = obj.getWarehouseName();
    }
    
	public ProductInventoryDto(String productMsn, Double availableQuantity, Double allocatedQuantity,
			Double currentQuantity, Integer warehouseId, String warehouseName, String productName) {
		this.productMsn = productMsn;
		this.availableQuantity = availableQuantity;
		this.allocatedQuantity = allocatedQuantity;
		this.currentQuantity = currentQuantity;
		this.warehouseId = warehouseId;
		this.warehouseName = warehouseName;
		this.productName = productName;
	}

    public ProductInventoryDto(ProductInventory obj) {
        this.id = obj.getId();
        this.totalQuantity = obj.getTotalQuantity();
        this.currentQuantity = obj.getCurrentQuantity();
        this.availableQuantity = obj.getAvailableQuantity();
        this.allocatedQuantity = obj.getAllocatedQuantity();
        this.averageAge = obj.getAverageAge();
        this.averagePrice = obj.getAveragePrice();
        this.productId = obj.getProduct().getId();
        this.productName = obj.getProduct().getProductName();
        this.productMsn = obj.getProduct().getProductMsn();
        this.warehouseId = obj.getWarehouse().getId();
        this.warehouseName = obj.getWarehouse().getName();
        this.stateName = obj.getWarehouse().getCity().getState().getName();
        this.cityName = obj.getWarehouse().getCity().getName();
        this.created = obj.getCreated();
        this.modified = obj.getModified();
    }
    
    public Double getBlockedQuantity() {
		return blockedQuantity;
	}

	public void setBlockedQuantity(Double blockedQuantity) {
		this.blockedQuantity = blockedQuantity;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Double getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Double getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(Double allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public int getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(int averageAge) {
        this.averageAge = averageAge;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getProductMsn() {
        return productMsn;
    }

    public void setProductMsn(String productMsn) {
        this.productMsn = productMsn;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

	public Double getPackedQuantity() {
		return PackedQuantity;
	}

	public void setPackedQuantity(Double packedQuantity) {
		PackedQuantity = packedQuantity;
	}


}
