package com.moglix.wms.projectionObjects;

/**
 * @author pankaj on 17/5/19
 */
public class AverageAgeAndActiveMsn {

    private double avgAge;
    private long msnActive;

    public AverageAgeAndActiveMsn() {
    }

    public AverageAgeAndActiveMsn(double avgAge, long msnActive) {
        this.avgAge = avgAge;
        this.msnActive = msnActive;
    }

    public double getAvgAge() {
        return avgAge;
    }

    public void setAvgAge(double avgAge) {
        this.avgAge = avgAge;
    }

    public long getMsnActive() {
        return msnActive;
    }

    public void setMsnActive(long msnActive) {
        this.msnActive = msnActive;
    }
}








