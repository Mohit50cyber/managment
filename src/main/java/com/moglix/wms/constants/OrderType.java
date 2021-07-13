package com.moglix.wms.constants;

import java.util.HashMap;
import java.util.Map;

public enum OrderType {
		
	NEW(0), BULK_INVOICING(1), ABFRL(2), SERVICE_AGREEMENT (3);
	
	int code;

	OrderType(int code) {
        this.code = code;
    }
	
	public int getCode() {
        return code;
    }
	
	private static Map<Integer, OrderType> statusMap = new HashMap<>();
    static {
        for (OrderType status : OrderType.values()) {
            statusMap.put(status.getCode(), status);
        }
    }
    public static OrderType valueOf(int code) {
        return statusMap.get(code);
    }
}
