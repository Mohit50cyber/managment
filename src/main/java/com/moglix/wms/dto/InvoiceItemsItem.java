package com.moglix.wms.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public @Data class InvoiceItemsItem{
    private String brandName;
	private Object productMpn;
	private Object itemQty;
	private Object orderId;
	private String priceBeforeDiscount;
	private Object customerGstin;
	private String discountAmount;
	private Object productUnit;
	private String productName;
	private Object productCPN;
	private String uom;
	private String price;
	private String totalAmountExcludingTax;
	private String productRef;
	private Object buyerProductName;
	private Object addedFields;
	private String hsnCode;
	private String quantity;
	private String discountPercent;
	private String taxRateApplicable;
	private Double netAmount;
	private boolean sameGstin;
	private String totalAmount;
	private Object itemId;
	private Double sgstAmount;
	private String taxableValue;
	private Double cgstAmount;
	private String igstAmount;
	private boolean priceDiscounted;
	private String cgstRate;
	private String sgstRate;
	private String igstRate;
	private Object vendorGstin;
	private String transferPrice;
}