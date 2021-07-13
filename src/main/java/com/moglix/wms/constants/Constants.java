package com.moglix.wms.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
	
	private Constants() {}

	public static final String EMS_PACKABLE_QUANTITY_API = "http://emsqa.moglilabs.com/api/wms/updatePackableQuantity.json";
	
	public static final String EMS_TOLERANCE_QUANTITY_API = "http://emsqa.moglilabs.com/api/wms/updateQuantityTolerance.json";
	
	public static final String EMS_CANCEL_PACKET_PACKABLE_QUANTITY_API = "http://emsqa.moglilabs.com/api/wms/updatePackableQuantityDeletePacket.json";
	
	public static final String SALES_OPS_DEMAND_API = "https://salesopsqa.moglilabs.com/api/v1/enterprise/allocate-Inventory";
	
	public static final String SALES_OPS_INCREASE_DEMAND_API = "https://salesopsqa.moglilabs.com/api/v1/purchase/item/transfer-inventory";
	
	public static final String WMS_API_BASE_URL_QA = "wms.moglilabs.com";
	
	private static final List<Double> GST_VALUES = Arrays.asList(0.10, 0.00, 0.25, 3.00, 5.00, 12.00, 18.00, 28.00);

	public static final String CSV_FILE_PATH = "/opt/batchdata.csv";
	
	public static final String PRODUCT_CSV_FILE_PATH = "/opt/productdata.csv";

	public static final String FAILURE_CSV_FILE_PATH = "/opt/batchfailedCsvRecords.csv";
	
	public static final String PRODUCT_FAILURE_CSV_FILE_PATH = "/opt/productfailedCsvRecords.csv";

	public static final String STORAGE_CSV_FILE_PATH = "/opt/storagedata.csv";

	public static final String STORAGE_FAILURE_CSV_FILE_PATH = "/opt/storagefailedCsvRecords.csv";
	
	private static final List<String> INBOUND_SEARCH_COLUMNS = Arrays.asList("supplierPoItemId", "supplierPoId");

	public static final String CSV_RETURN_FILE_PATH = "/opt/returnbatchdata.csv";

	public static final String SALES_OPS_AUTH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwcm9kaW50ZWdyYXRpb24uYm90QG1vZ2xpeC5jb20iLCJlbWFpbCI6InByb2RpbnRlZ3JhdGlvbi5ib3RAbW9nbGl4LmNvbSIsInVzZXJLZXkiOiJQR2RIRkRZU3dMTEtKalZMcU1iV3BrYml5Y3Z0YXlrcWlWaHh0VWdJTmRGQmF3ZWJzUXZHSGpNRFVCbHZ4ZUVNIn0.pSKoZu_ZYfa-ot_Qf3pLB_gwM48FGyzCcPZBh0v22Qs";
	
	public static final String IMS_AUTH_TOKEN ="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkaWxlZXAua3VtYXJAbW9nbGl4LmNvbSIsImVtYWlsIjoiZGlsZWVwLmt1bWFyQG1vZ2xpeC5jb20iLCJ1c2VyS2V5IjoiTEVEbnlCbU1RSUlBVXdEemRkTGRXc29oZ0lxVW9sYkhidUFOWnpWSkxrWUZMbU54UXhtT0VybVVodGhtRk9CaiJ9.wXeG42k0dP02DYcWeNTfPzFyHKHZYi4VGHHnVRXBqfI";

	public static final String PURCHASE_URL = "https://purchase.moglilabs.com";
    
	public static final String PURCHASE_DEMAND_MAPPING_API =  PURCHASE_URL + "/purchase/api/v1/demand/";
    
	public static final String PURCHASE_ITEM_REF_DROPSHIP_QUANTITY = "/purchase/api/v1/po/item/saleorder/dropship/quantity";
	
	public static final String PRODUCTMSN_SYNC_API = "/cassandraFeed/catalog/getProductDetailsForProductIds";
	
	public static final String IMS_URL = "https://imsqa.moglilabs.com/";
    public static final String IMS_ITEM_REF_PACKED_QUANTITY  = "/api/saleOrder/getPackedQuantity/";
	
	public static final String UPLOADED_FOLDER = "/opt/";
	
	public static List<Double> getGstValues(){
		return Collections.unmodifiableList(GST_VALUES);
	}
	
	public static String getInventoryEmailContent(String productMSN, String productDescription, String warehouseName) {
		return "Hi Team \nThe WMS team wants to inform you that the Inventory Quantity has reached the Minimum threshold level for "
				+ productMSN + " - " + productDescription + " in the " + warehouseName
				+ " warehouse.\n\nRequest you to please raise a new Supplier Purchase order to improve the Inventory quantity level in the Warehouse.\nBest\nWMS Team";
	}
	
	public static String getProductNotSyncEmailContent(String productMSN, String itemRef) {
		return "Hi Team, \nThe ProductMSN " + productMSN + " for order having itemRef :: " + itemRef + " is not in sync with wms system. [Invalid ProductMSN] \n\n\nRegards,\n\nWMS Team";
	}
	
	public static List<String> getInboundSearchColumns(){
		return Collections.unmodifiableList(INBOUND_SEARCH_COLUMNS);
	}

	//Auth constants
	public static final String AUTH_HEADER_NAME = "Authorization";
	public static final String AUTH_HEADER_TOKEN_PREFIX = "Bearer";
	public static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000; // 10 days validity


	public static final String WMS_RECONCILE_TOPIC = "wms-invoice-tracking";
	
	public static final String IMS_SUPPLIER_INFO_API = "https://imsqa.moglilabs.com/api/packet/supplierDetails";

	public static final String TEST_EMAIL = "sparsh.eronmicro@moglix.com";
	
	public static final String INVENTORY_MAIL_SUBJECT = "Notification for Minimum Inventory Quantity Level";

	public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    // Email subject for order validation
    public static final String SUBJECT_ORDER_VALIDATION_EMAIL_QA   = "QA || WMS || Invalid order data from salesops for %s";
    public static final String SUBJECT_ORDER_VALIDATION_EMAIL_PROD = "Production || WMS || Invalid order data from salesops for %s";
    
    // Email subject for product msn validation
    public static final String SUBJECT_PRODUCT_MSN_VALIDATION_EMAIL_QA   = "QA || WMS || Invalid productMSN from Catalog for %s";
    public static final String SUBJECT_PRODUCT_MSN_VALIDATION_EMAIL_PROD = "Production || WMS || Invalid productMSN from Catalog for %s";
}
