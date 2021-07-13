package com.moglix.wms.dto;

import lombok.Data;

import java.util.List;

public @Data class InvoiceEngineResponse{
	private String msg;
	private boolean success;
	private List<InvoiceItem> invoice;
}