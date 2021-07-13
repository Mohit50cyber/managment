package com.moglix.wms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moglix.wms.entities.ProductInventory;
import com.moglix.wms.entities.SaleOrderAllocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 15/5/19
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryLocationDto {

    private int age;
    private Integer productId;
    private String productName;
    private String productMsn;
    private Integer warehouseId;
    private String warehouseName;
    private Double quantity;
    private String uom;
    private List<LocationDto> locations = new ArrayList<>();

    public InventoryLocationDto() {}
    public InventoryLocationDto(ProductInventory productInventory, List<LocationDto> locations) {
        this.age = productInventory.getAverageAge();
        this.productId = productInventory.getProduct().getId();
        this.productMsn = productInventory.getProduct().getProductMsn();
        this.productName = productInventory.getProduct().getProductName();
        this.quantity = productInventory.getAllocatedQuantity();
        this.warehouseId = productInventory.getWarehouse().getId();
        this.warehouseName = productInventory.getWarehouse().getName();
        this.uom = productInventory.getProduct().getUom();
        this.locations = locations;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public String getProductMsn() {
        return productMsn;
    }

    public void setProductMsn(String productMsn) {
        this.productMsn = productMsn;
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public List<LocationDto> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDto> locations) {
        this.locations = locations;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LocationDto {
        private String storageLocationName;
        private String zoneName;
        private Integer storageLocationId;
        private Integer zoneId;
        private Double quantity;
        private Double packedQuantity;
        private String invoiceNumber;

        //to be used for allocated qty only
        private Integer emsOrderId;
        private Integer emsOrderItemId;
        private Integer saleOrderId;


        public LocationDto(){}

        public LocationDto(SaleOrderAllocation saleOrderAllocation,Double quantity,Double packedQuantity, String invoiceNumber) {
            this.setQuantity(quantity);
            this.setPackedQuantity(packedQuantity);
            this.setStorageLocationId(saleOrderAllocation.getInboundStorage().getStorageLocation().getId());
            this.setStorageLocationName(saleOrderAllocation.getInboundStorage().getStorageLocation().getName());
            this.setZoneId(saleOrderAllocation.getInboundStorage().getStorageLocation().getZone().getId());
            this.setZoneName(saleOrderAllocation.getInboundStorage().getStorageLocation().getZone().getName());
            this.setEmsOrderId(saleOrderAllocation.getSaleOrder().getEmsOrderId());
            this.setEmsOrderItemId(saleOrderAllocation.getSaleOrder().getEmsOrderItemId());
            this.setSaleOrderId(saleOrderAllocation.getSaleOrder().getId());
            this.invoiceNumber = invoiceNumber;
        }

        public String getStorageLocationName() {
            return storageLocationName;
        }

        public void setStorageLocationName(String storageLocationName) {
            this.storageLocationName = storageLocationName;
        }

        public String getZoneName() {
            return zoneName;
        }

        public void setZoneName(String zoneName) {
            this.zoneName = zoneName;
        }

        public Integer getStorageLocationId() {
            return storageLocationId;
        }

        public void setStorageLocationId(Integer storageLocationId) {
            this.storageLocationId = storageLocationId;
        }

        public Integer getZoneId() {
            return zoneId;
        }

        public void setZoneId(Integer zoneId) {
            this.zoneId = zoneId;
        }

        public Double getQuantity() {
            return quantity;
        }

        public void setQuantity(Double quantity) {
            this.quantity = quantity;
        }

        public Integer getEmsOrderId() {
            return emsOrderId;
        }

        public void setEmsOrderId(Integer emsOrderId) {
            this.emsOrderId = emsOrderId;
        }

        public Integer getEmsOrderItemId() {
            return emsOrderItemId;
        }

        public void setEmsOrderItemId(Integer emsOrderItemId) {
            this.emsOrderItemId = emsOrderItemId;
        }

        public Integer getSaleOrderId() {
            return saleOrderId;
        }

        public void setSaleOrderId(Integer saleOrderId)
        {
            this.saleOrderId = saleOrderId;
        }

        public Double getPackedQuantity() {
            return packedQuantity;
        }

        public void setPackedQuantity(Double packedQuantity)
        {
            this.packedQuantity = packedQuantity;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }
    }
}
