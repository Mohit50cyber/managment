package com.moglix.wms.api.response;

/**
 * @author pankaj on 17/5/19
 */
public class GetInventoryStatsResponse extends BaseResponse {
    private static final long serialVersionUID = 2386103840980537087L;

    private Double totalInventory;
    private Double availableInventory;
    private Double allocatedInventory;
    private long msnInInventory;
    private Double totalPrice;
    private long averageAge;
    private long msnActive;

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

    public long getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(long averageAge) {
        this.averageAge = averageAge;
    }

    public long getMsnActive() {
        return msnActive;
    }

    public void setMsnActive(long msnActive) {
        this.msnActive = msnActive;
    }
}
