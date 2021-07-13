package com.moglix.wms.constants;

public enum PacketStatus {
	INVOICED, PICKUPLIST_DONE, ASN, SHIPPED, DELIVERED, DELIVERED_POD, CANCELLED,RETURNED, BATCH_CREATED, SCANNED, IN_PROGRESS;


	public boolean isPacked() {
		return this.equals(INVOICED) || this.equals(PICKUPLIST_DONE) || this.equals(SCANNED);
	}
}
