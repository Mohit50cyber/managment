package com.moglix.wms.dto;

import java.util.List;
import lombok.Data;

public @Data class InvoiceItem{
	private String deliveryChallanUrl;
	private Object deliveryChallanDate;
	private Address consigneeAddressShipping;
	private String customerGstin;
	private String totalNoOfItems;
	private String invoiceValue;
	private String createdAt;
	private String customerOrderRef;
	private String irnDate;
	private int invoiceType;
	private boolean isTaxComponentIncludedInSezInvoice;
	private Object addedFields;
	private String invoiceNo;
	private boolean isSez;
	private boolean checkTransactionStatus;
	private String miscCharges;
	private String refInvoiceNo;
	private String customerName;
	private String marketingFee;
	private Address consignorAddressShipping;
	private String taxRate;
	private List<InvoiceItemsItem> invoiceItems;
	private List<Object> messageList;
	private String invoiceValueExcludingOther;
	private int warehouseId;
	private String cgstRate;
	private String sgstRate;
	private String igstRate;
	private int status;
	private int retrySubmissionCount;
	private Object creditTerms;
	private Object callerTransactionId;
	private Address consignorAddress;
	private String deliveryChallanNo;
	private String tcs;
	private int transactionStatus;
	private Address consigneeAddressBilling;
	private String irnBarcode;
	private String freightCharge;
	private String invoiceDate;
	private int invoiceSource;
	private boolean isIgst;
	private String sellerGSTIN;
	private String sgstAmount;
	private String taxableValue;
	private String taxValue;
	private String cgstAmount;
	private String invoiceURL;
	private String igstAmount;
	private int gstrStatus;
	private String invoiceValueInWords;
	private Object stockChallanId;
	private String irnNo;
	private String irnAckno;
	private Object remarks;
}