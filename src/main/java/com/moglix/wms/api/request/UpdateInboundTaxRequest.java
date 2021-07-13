package com.moglix.wms.api.request;

import java.util.List;

import com.moglix.wms.validator.CheckValidTaxRate;

public class UpdateInboundTaxRequest extends BaseRequest{
	
private static final long serialVersionUID = 825483604454275032L;
	
	private List<Integer> inboundIds;
	
	@CheckValidTaxRate
	private Double tax;

	public List<Integer> getInboundIds() {
		return inboundIds;
	}

	public void setInboundIds(List<Integer> inboundIds) {
		this.inboundIds = inboundIds;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	@Override
	public String toString() {
		return "UpdateInboundTaxRequest [inboundIds=" + inboundIds + ", tax=" + tax + ", getInboundIds()="
				+ getInboundIds() + ", getTax()=" + getTax() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	


}
