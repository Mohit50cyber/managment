package com.moglix.wms.projectionObjects;

/**
 * @author pankaj on 17/5/19
 */
public class InventoryStats {

    private Double totalInventory;
    private Double availableInventory;
    private Double allocatedInventory;
    private long msnInInventory;
    private Double totalPrice;

    public InventoryStats() {
    }

    public InventoryStats(Double totalInventory, Double totalPrice, Double availableInventory, Double allocatedInventory, long msnInInventory) {
        this.totalInventory = totalInventory;
        this.availableInventory = availableInventory;
        this.allocatedInventory = allocatedInventory;
        this.msnInInventory = msnInInventory;
        this.totalPrice = totalPrice;
    }

    public Double getTotalInventory() {
        return totalInventory;
    }

    public void setTotalInventory(Double totalInventory) {
        this.totalInventory = totalInventory;
    }

    public Double getAvailableInventory() {
        return availableInventory;
    }

    public void setAvailableInventory(Double availableInventory) {
        this.availableInventory = availableInventory;
    }

    public Double getAllocatedInventory() {
        return allocatedInventory;
    }

    public void setAllocatedInventory(Double allocatedInventory) {
        this.allocatedInventory = allocatedInventory;
    }

    public long getMsnInInventory() {
        return msnInInventory;
    }

    public void setMsnInInventory(long msnInInventory) {
        this.msnInInventory = msnInInventory;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
