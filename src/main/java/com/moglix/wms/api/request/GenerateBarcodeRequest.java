package com.moglix.wms.api.request;


import java.sql.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

public class GenerateBarcodeRequest extends BaseRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6470001382289609799L;
	
	@NotNull
	private String msn;
	private String description;
	private List<Integer>serialNumbers;
	
	@NotNull
	private Boolean isSerializedProduct;
	
	@NotNull
	private Integer inboundId;
	
	@NotNull
	private Integer quantity;
	
	private Integer lotno;
	
	private Date expirydate;
	
	
	public Integer getLotno() {
		return lotno;
	}
	public void setLotno(Integer lotno) {
		this.lotno = lotno;
	}
	public Date getExpirydate() {
		return expirydate;
	}
	public void setExpirydate(Date expirydate) {
		this.expirydate = expirydate;
	}
	public String getMsn() {
		return msn;
	}
	public void setMsn(String msn) {
		this.msn = msn;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<Integer> getSerialNumbers() {
		return serialNumbers;
	}
	public void setSerialNumbers(List<Integer> serialNumbers) {
		this.serialNumbers = serialNumbers;
	}
	public Boolean getIsSerializedProduct() {
		return isSerializedProduct;
	}
	public void setIsSerializedProduct(Boolean isSerializedProduct) {
		this.isSerializedProduct = isSerializedProduct;
	}
	public Integer getInboundId() {
		return inboundId;
	}
	public void setInboundId(Integer inboundId) {
		this.inboundId = inboundId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}
