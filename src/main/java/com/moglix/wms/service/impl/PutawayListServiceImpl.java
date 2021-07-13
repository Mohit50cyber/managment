package com.moglix.wms.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moglix.wms.api.request.GeneratePutawayListRequest;
import com.moglix.wms.api.response.GeneratePutawayListResponse;
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.dto.PutawayListItemsDTO;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.PutawayList;
import com.moglix.wms.entities.PutawayListInboundStorageMapping;
import com.moglix.wms.repository.PutawayListRepository;
import com.moglix.wms.service.IInboundService;
import com.moglix.wms.service.IInboundStorageService;
import com.moglix.wms.service.IProductService;
import com.moglix.wms.service.IPutawayListInboundStorageMappingService;
import com.moglix.wms.service.IPutawayListService;
import com.moglix.wms.util.PutawayListUtil;

@Service(value = "putawaylistserviceImpl")
public class PutawayListServiceImpl implements IPutawayListService {

	Logger log = LogManager.getLogger(PutawayListServiceImpl.class);
	Gson gson = new GsonBuilder().create();
	@Autowired
	IInboundStorageService inboundStorageService;
	
	@Autowired
	@Qualifier("productService")
	IProductService productService;
	
	@Autowired
	PutawayListRepository repository;
	
	@Autowired
	@Qualifier("putawayListInboundStorageMappingServiceImpl")
	IPutawayListInboundStorageMappingService mappingService;
	
	@Autowired
	IInboundService inboundService;
	
	@Override
	@Transactional
	public GeneratePutawayListResponse generatePutawayList(GeneratePutawayListRequest request) {		
		GeneratePutawayListResponse response = new GeneratePutawayListResponse();
		List<InboundStorage> inboundStorages =  inboundStorageService.findAllByInboundIdIn(request.getInboundIds());
		
		if(inboundStorages.isEmpty()) {
			response.setMessage("No records found for Inbound Ids: " + request.getInboundIds());
			response.setCode(200);
			response.setStatus(true);
			return response;
		}
		PutawayList putawayList = PutawayListUtil.getPutAwayListFromInboundStorage(inboundStorages.get(inboundStorages.size() -1));
		putawayList.setAssignedTo(request.getAssignedTo());
		putawayList.setGeneratedBy(request.getGeneratedBy());
		
		Double quantity = 0.0;
		for(InboundStorage storage : inboundStorages) {
			Inbound inbound = storage.getInbound();
			inbound.setStatus(InboundStatusType.PUTAWAYLIST_GENERATED);
			inboundService.upsert(inbound);
			quantity = quantity + storage.getInbound().getInboundItems().size();
			PutawayListInboundStorageMapping mapping = new PutawayListInboundStorageMapping();
			PutawayListItemsDTO putawayListDTO = PutawayListUtil.getPutAwayListItemsDTOFromInboundStorage(storage);
			
			if (response.getPutawayList().contains(putawayListDTO)) {
				int index = response.getPutawayList().indexOf(putawayListDTO);

				response.getPutawayList().get(index)
						.setQuantity(putawayListDTO.getQuantity() + response.getPutawayList().get(index).getQuantity());
			}else {
				response.getPutawayList().add(putawayListDTO);
			}
			
			mapping.setInboundStorage(storage);
			mapping.setPutawayList(putawayList);
			mappingService.save(mapping);
		}
		
		Collections.sort(response.getPutawayList(), Comparator.comparing(PutawayListItemsDTO::getZoneId)
				.thenComparing(PutawayListItemsDTO::getRackId).thenComparing(PutawayListItemsDTO::getBinId));
		putawayList.setQuantity(quantity);
		save(putawayList);
		response.setMessage("PutawayList Generated for Inbounds: " + request.getInboundIds());
		response.setStatus(true);
		response.setCode(200);
		response.setGeneratedBy(request.getGeneratedBy());
		response.setAssignedTo(request.getAssignedTo());
		return response;
	}

	@Override
	@Transactional
	public PutawayList save(PutawayList list) {
		return repository.save(list);
	}

}
