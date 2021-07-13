package com.moglix.wms.api.request;

import java.io.Serializable;
import java.util.Set;

import com.moglix.wms.api.request.ReturnBatchRequest.ReturnPacketCustom;

public class SupplierDetailRequestIMS extends BaseRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2321556338701073011L;

	private Integer emsPacketId;

	private Integer emsReturnId;

	private Set<ReturnPacketCustom> returnPackets;


	public Integer getEmsPacketId() {
		return emsPacketId;
	}

	public void setEmsPacketId(Integer emsPacketId) {
		this.emsPacketId = emsPacketId;
	}

	public Integer getEmsReturnId() {
		return emsReturnId;
	}

	public void setEmsReturnId(Integer emsReturnId) {
		this.emsReturnId = emsReturnId;
	}

	public Set<ReturnPacketCustom> getReturnPackets() {
		return returnPackets;
	}

	public void setReturnPackets(Set<ReturnPacketCustom> returnPackets) {
		this.returnPackets = returnPackets;
	}

	@Override
	public String toString() {
		return "SupplierDetailsForCustomerInvoiceRequest [emsPacketId=" + emsPacketId + ", emsReturnId=" + emsReturnId
				+ ", returnPackets=" + returnPackets + ", getEmsPacketId()=" + getEmsPacketId() + ", getEmsReturnId()="
				+ getEmsReturnId() + ", getReturnPackets()=" + getReturnPackets() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
