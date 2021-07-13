package com.moglix.wms.dto;

import java.util.List;
import lombok.Data;

public @Data class InvoiceRequestDTO{
	private String creditTerms;
	private Address consigneeAddressShipping;
	private String customerGstin;
	private String callerTransactionId;
	private int vatRate;
	private Address consignorAddress;
	private String customerOrderRef;
	private int countryCode;
	private int invoiceType;
	private boolean isTaxComponentIncludedInSezInvoice;
	private AddedFields addedFields;
	private String invoiceNo;
	private String currencyUnit;
	private boolean isSez;
	private int tcs;
	private Address consigneeAddressBilling;
	private int subsidiaryValue;
	private String invoiceDate;
	private boolean isIgst;
    private String stockChallanId;
	private int invoiceSource;
	private String customerName;
	private int vatAmount;
	private String sellerGSTIN;
	private Address consignorAddressShipping;
	private List<InvoiceItemsItem> invoiceItems;
	private String warehouseId;
	private Object remarks;
	private String freightCharge;
	private String miscCharges;
	private Double cgstAmount;
	private Double sgstAmount;
	private Double sgstRate;
	private Double cgstRate;
	private Double taxableValue;
	private Double invoiceValue;


}