package com.moglix.wms.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moglix.wms.entities.InboundItem;
import com.moglix.wms.repository.InboundItemsRepository;
import com.moglix.wms.service.IInboundItemService;

@Service(value = "inboundItemService")
public class InboundItemServiceImpl implements IInboundItemService {

	Logger log = LogManager.getLogger(InboundItemServiceImpl.class);
	@Autowired
	InboundItemsRepository inboundItemsRepo;

	@Override
	public void saveInbounds(Iterable<InboundItem> data) {
		inboundItemsRepo.saveAll(data);		
	}	

}
