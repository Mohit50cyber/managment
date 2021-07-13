package com.moglix.wms.service;

import com.moglix.wms.entities.InboundItem;

public interface IInboundItemService {
	public void saveInbounds(Iterable<InboundItem> inbounds);
}
