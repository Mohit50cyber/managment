package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.LotInfo;

public class PacketLotResponse extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5347957206530057246L;
	
	public PacketLotResponse(String message, boolean status, int code) {
		super.setMessage(message);
		super.setStatus(status);
		super.setCode(code);
	}
	
	private List<LotInfo> lotInfo = new ArrayList<>();

	public List<LotInfo> getLotInfo() {
		return lotInfo;
	}

	public void setLotInfo(List<LotInfo> lotInfo) {
		this.lotInfo = lotInfo;
	}
}
