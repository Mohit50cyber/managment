package com.moglix.wms.api.response;

import java.util.List;

import com.moglix.wms.dto.VmiReportDataDTO;

public class VmiReportDataResponse extends BaseResponse{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -927731671781418342L;

	public VmiReportDataResponse() {
		super();
	}

	public VmiReportDataResponse(String message, boolean status, int code) {
		super(message, status, code);
	}

	List<VmiReportDataDTO> vmiReportData;

	public List<VmiReportDataDTO> getVmiReportData() {
		return vmiReportData;
	}

	public void setVmiReportData(List<VmiReportDataDTO> vmiReportData) {
		this.vmiReportData = vmiReportData;
	}
	
	
}
