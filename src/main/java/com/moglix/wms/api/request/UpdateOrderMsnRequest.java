package com.moglix.wms.api.request;

public class UpdateOrderMsnRequest extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 825483604454275032L;
	
	private Integer emsOrderItemId;
	
	private String updatedMsn;

	public Integer getEmsOrderItemId() {
		return emsOrderItemId;
	}

	public void setEmsOrderItemId(Integer emsOrderItemId) {
		this.emsOrderItemId = emsOrderItemId;
	}

	public String getUpdatedMsn() {
		return updatedMsn;
	}

	public void setUpdatedMsn(String updatedMsn) {
		this.updatedMsn = updatedMsn;
	}
	
	@Override
    public String toString() {
        return "UpdateOrderMsnRequest{" +
                "emsOrderItemId=" + emsOrderItemId +
                ", updatedMsn=" + updatedMsn +
                '}';
    }

}
