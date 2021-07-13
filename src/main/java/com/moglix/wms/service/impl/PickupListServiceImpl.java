package com.moglix.wms.service.impl;

import com.moglix.wms.api.request.BulkGeneratePickupListRequest;
import com.moglix.wms.api.request.GeneratePickupListByMSNRequest;
import com.moglix.wms.api.request.GeneratePickupListRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.GeneratePickupListAppResponse;
import com.moglix.wms.api.response.GeneratePickupListResponse;
import com.moglix.wms.api.response.CountWarehouseDataResponse;
import com.moglix.wms.constants.PacketItemStatus;
import com.moglix.wms.constants.PacketStatus;
import com.moglix.wms.controller.BulkGeneratePickupListResponse;
import com.moglix.wms.dto.InvoiceDetail;
import com.moglix.wms.dto.PickupListAppDto;
import com.moglix.wms.dto.PickupListDto;
import com.moglix.wms.dto.PickupListItemAppDto;
import com.moglix.wms.dto.PickupListItemDto;
import com.moglix.wms.entities.*;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.PacketItemRepository;
import com.moglix.wms.repository.PacketRespository;
import com.moglix.wms.repository.PickupListItemRepository;
import com.moglix.wms.repository.PickupListRepository;
import com.moglix.wms.service.IPacketService;
import com.moglix.wms.service.IPickupListService;
import com.moglix.wms.service.IWarehouseService;
import com.moglix.wms.util.NumberUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import javax.validation.Valid;

/**
 * @author pankaj on 14/5/19
 */
@Service
public class PickupListServiceImpl implements IPickupListService {

    Logger logger = LogManager.getLogger(PickupListServiceImpl.class);

    @Autowired
    private PickupListRepository pickupListRepository;

    @Autowired
    private PickupListItemRepository pickupListItemRepository;
    
    @Autowired
    private PacketItemRepository packetItemRepo;

    @Autowired
    private IPacketService packetService;  

    @Autowired
    private IWarehouseService warehouseService;
    
    @Autowired
    private PacketRespository packetRepo;
    
    @Autowired
    private InboundRepository inboundRepo;

    @Override
    public PickupList upsert(PickupList obj) {
        return pickupListRepository.save(obj);
    }

    @Override
    public PickupList getById(Integer id) {
        return pickupListRepository.findById(id).orElse(null);
    }

    @Override
    public Iterable<PickupList> upsertAll(List<PickupList> pickupLists) {
        return pickupListRepository.saveAll(pickupLists);
    }

    @Override
    public PickupListItem upsertPickListItem(PickupListItem obj) {
        return pickupListItemRepository.save(obj);
    }

    @Override
    public PickupListItem getPickupListItemById(Integer id) {
        return pickupListItemRepository.findById(id).orElse(null);
    }

    @Override	
    @Transactional
    public GeneratePickupListResponse generatePickList(GeneratePickupListRequest request) {
        logger.info("Generate Pickup List Service Started");
        GeneratePickupListResponse response = new GeneratePickupListResponse();
        PickupListItem pickupListItem;
        PickupListDto pickupListDto;
        PickupListItemDto pickupListItemDto;
        Map<String, PickupListItemDto> map = new HashMap<>();
        List<PickupListItemDto> pickupListItemDtos;
        List<Integer> packetIds = new ArrayList<>();
        Warehouse warehouse = warehouseService.getById(request.getWarehouseId());
        int itemCount = 0;
        String key;

        List<Packet> packetList = packetService.getByIdIn(request.getPacketIds());

        //generate pickup list for input packets, ignore status
        if(!CollectionUtils.isEmpty(packetList)) {
            PickupList pickupList = new PickupList();
            pickupList.setGeneratedBy(request.getGeneratedBy());
            pickupList.setWarehouse(warehouse);
            upsert(pickupList);

            for (Packet packet : packetList) {
                for (PacketItem packetItem : packet.getPacketItems()) {
                    pickupListItem = new PickupListItem();
                    pickupListItem.setPickupList(pickupList);
                    pickupListItem.setPacketItem(packetItem);
                    upsertPickListItem(pickupListItem);

                    key = packetItem.getInboundStorage().getStorageLocation().getId() + "_" + packetItem.getInboundStorage().getProduct().getId()+"_"+packetItem.getInboundStorage().getLotId();

                    pickupListItemDto = map.get(key);
                    if (pickupListItemDto == null) {
                        itemCount++;
                        pickupListItemDto = new PickupListItemDto(pickupListItem);
                    } else {
                        pickupListItemDto.setQuantity(NumberUtil.round4(pickupListItemDto.getQuantity() + packetItem.getQuantity()));
                    }
                    map.put(key, pickupListItemDto);
                }
                if(packet.getStatus().equals(PacketStatus.INVOICED)){
                	packet.setStatus(PacketStatus.PICKUPLIST_DONE);
                }
                packetService.upsert(packet);

                packetIds.add(packet.getId());
            }

            pickupList.setItemCount(itemCount);
            upsert(pickupList);

            pickupListDto = new PickupListDto(pickupList);
            pickupListDto.setPacketIds(packetIds);

            pickupListItemDtos = new ArrayList<>(map.values());
            pickupListItemDtos.sort(Comparator.comparing(PickupListItemDto::getZoneId)
                    .thenComparing(PickupListItemDto::getRackId).thenComparing(PickupListItemDto::getBinId));
            pickupListDto.setPickupListItems(pickupListItemDtos);

            response.setPickupList(pickupListDto);
            response.setMessage(packetList.size() + " successfully added in pickup list");
        } else {
            response.setMessage("No Packets Found");
        }
        response.setStatus(true);
        logger.info("Generate Pickup List Service Ended");
        return response;
    }

	@Override
	public BulkGeneratePickupListResponse bulkGeneratePickList(@Valid BulkGeneratePickupListRequest bulkRequest) {
		
		BulkGeneratePickupListResponse bulkPickupListResponse = new BulkGeneratePickupListResponse("PickupLists Generated successfully", true, HttpStatus.OK.value());
		
		List<GeneratePickupListResponse> responses =  new ArrayList<>();
		
		for (InvoiceDetail detail : bulkRequest.getInvoiceDetails()) {
			GeneratePickupListRequest request = new GeneratePickupListRequest();
			
			request.setWarehouseId(detail.getWarehouseId());
			
			request.setGeneratedBy(bulkRequest.getGeneratedBy());
			
			Packet packet = packetRepo.findByInvoiceNumberAndStatusNot(detail.getInvoiceNumber(), PacketStatus.RETURNED).orElse(null);
			
			if(packet != null) {
				request.setPacketIds(Collections.singletonList(packet.getId()));
			}else {
				request.setPacketIds(Collections.emptyList());
			}
			
			try {
				GeneratePickupListResponse pickupListResponse = generatePickList(request);

				responses.add(pickupListResponse);
			} catch (Exception e) {
				logger.warn("Error occured in generating pickuplist for invoice Number: " + packet.getInvoiceNumber(),
						e);
			}
		}
		
		bulkPickupListResponse.setPickupListResponse(responses);
		
		return bulkPickupListResponse;
	}

	@Override
	public GeneratePickupListResponse generatePickListByMSN(GeneratePickupListByMSNRequest request) {
		logger.info("Generate Pickup List Service Started for product ID "+request.getProductId());
        GeneratePickupListResponse response = new GeneratePickupListResponse();
        PickupListItem pickupListItem;
        PickupListDto pickupListDto;
        PickupListItemDto pickupListItemDto;
        Map<String, PickupListItemDto> map = new HashMap<>();
        List<PickupListItemDto> pickupListItemDtos;
        List<Integer> packetIds = new ArrayList<>();
        Warehouse warehouse = warehouseService.getById(request.getWarehouseId());
        int itemCount = 0;
        String key;

        Packet packet = packetService.getById(request.getPacketId());
       
        //generate pickup list for input packets, ignore status
        if(packet!=null) {
        	logger.info("Got Packet with Packet id "+packet.getId());
            PickupList pickupList = new PickupList();
            pickupList.setGeneratedBy(request.getGeneratedBy());
            pickupList.setWarehouse(warehouse);
            upsert(pickupList);

            
                for (PacketItem packetItem : packet.getPacketItems()) {
                	logger.info("Comparing "+packetItem.getSaleOrder().getProduct().getId()+" <-->"+request.getProductId());
                	if((int)packetItem.getSaleOrder().getProduct().getId()==(int)request.getProductId())
                	{
                	logger.info("Found to be equal");
                    pickupListItem = new PickupListItem();
                    pickupListItem.setPickupList(pickupList);
                    pickupListItem.setPacketItem(packetItem);
                    logger.info("Setting PacketListItem with Pickuplist Id :: "+pickupList.getId() );
                    upsertPickListItem(pickupListItem);

                    key = packetItem.getInboundStorage().getStorageLocation().getId() + "_" + packetItem.getInboundStorage().getProduct().getId()+"_"+packetItem.getInboundStorage().getLotId();
                    logger.info("Got Key as "+key);
                    pickupListItemDto = map.get(key);
                    if (pickupListItemDto == null) {
                        itemCount++;
                        pickupListItemDto = new PickupListItemDto(pickupListItem);
                    } else {
                        pickupListItemDto.setQuantity(NumberUtil.round4(pickupListItemDto.getQuantity() + packetItem.getQuantity()));
                    }
                    map.put(key, pickupListItemDto);
                	}
                }
               

                packetIds.add(packet.getId());
            

            pickupList.setItemCount(itemCount);
            upsert(pickupList);

            pickupListDto = new PickupListDto(pickupList);
            pickupListDto.setPacketIds(packetIds);

            pickupListItemDtos = new ArrayList<>(map.values());
            pickupListItemDtos.sort(Comparator.comparing(PickupListItemDto::getZoneId)
                    .thenComparing(PickupListItemDto::getRackId).thenComparing(PickupListItemDto::getBinId));
            pickupListDto.setPickupListItems(pickupListItemDtos);

            response.setPickupList(pickupListDto);
            response.setMessage(packet.getId() + " successfully added in pickup list");
        } else {
            response.setMessage("No Packets Found");
        }
        response.setStatus(true);
        logger.info("Generate Pickup List Service Ended");
        return response;
	}

	@Override
	public BaseResponse updateStatus(Integer packetId) {

		Packet packet = packetService.getById(packetId);

		if(packet==null) {
			logger.info("Packet not found for ID :: "+packetId);
			return new BaseResponse("Packet Not Found with ID ::"+packetId,false,200);
		}
		
		logger.info("Packet current status is "+packet.getStatus());
		if(packet.getStatus().equals(PacketStatus.IN_PROGRESS)){
        	packet.setStatus(PacketStatus.PICKUPLIST_DONE);
        	packetService.upsert(packet);
        	logger.info("Packet New status is "+packet.getStatus());
        	return new BaseResponse(" Status Changed for packetId :: "+packetId,true,200);
        }else {
        	return new BaseResponse("Status of Packet is currently "+packet.getStatus(),true,200);
        }

		
	}
	
	@Override
	public BaseResponse packetInProgress(Integer packetId,String usermail) {

		Packet packet = packetService.getById(packetId);

		if(packet==null) {
			logger.info("Packet not found for ID :: "+packetId);
			return new BaseResponse("Packet Not Found with ID ::"+packetId,false,200);
		}
		
		logger.info("Packet current status is "+packet.getStatus());
		if(packet.getStatus().equals(PacketStatus.INVOICED)){
        	packet.setStatus(PacketStatus.IN_PROGRESS);
        	packet.setPickedby(usermail);
        	packetService.upsert(packet);
        	logger.info("Packet New status is "+packet.getStatus());
        	return new BaseResponse(" Status Changed for packetId :: "+packetId,true,200);
        }else {
        	return new BaseResponse("Status of Packet was already "+packet.getStatus(),true,200);
        }

		
	}
	
	@Override
	public BaseResponse updatePacketItem(Integer packetId,Integer packetItemId,String user) {

		Packet packet = packetService.getById(packetId);

		if(packet==null) {
			logger.info("Packet not found for ID :: "+packetId);
			return new BaseResponse("Packet Not Found with ID ::"+packetId,false,200);
		}
		int picked=0;
		 for (PacketItem packetItem : packet.getPacketItems()) {
			 System.out.println("\n"+packetItem.getId()+" "+packetItemId);
		 if(packetItem.getId().equals(packetItemId)) {
			 packetItem.setStatus(PacketItemStatus.PICKED);
			 packet.setStatus(PacketStatus.IN_PROGRESS);
			 packet.setPickedby(user);
			 packetService.upsert(packet);
			 packetItemRepo.save(packetItem);
			 picked=1;
			 break;
		 }
		 }
		 if(picked==0) {
			 logger.info("PacketItem not found for ID :: "+packetItemId);
				return new BaseResponse("PacketItem Not Found with ID ::"+packetItemId,false,200);
		 }else {
			 logger.info("PacketItem found for ID :: "+packetItemId);
				return new BaseResponse("PacketItem Found with ID ::"+packetItemId,true,200);
		 }

		
	}

	@Override
	public CountWarehouseDataResponse countWarehouseData(Integer warehouseId) {
		
		Integer packetcount = packetRepo.countdata(warehouseId);
		logger.info(" Packet Quantity Found to be ::"+packetcount);
		
		Integer inboundcount = inboundRepo.pickuplistcount(warehouseId);
		logger.info(" inboundcount Quantity Found to be ::"+inboundcount);
		
		CountWarehouseDataResponse response = new CountWarehouseDataResponse();
		
		response.setPickupListCount(packetcount);
		response.setInboundCount(inboundcount);
		
		return response;
	}

	@Override
	public GeneratePickupListAppResponse generatePickListApp(GeneratePickupListRequest request) {
		logger.info("Generate Pickup List Service Started");
		GeneratePickupListAppResponse response = new GeneratePickupListAppResponse();
        PickupListItem pickupListItem;
        PickupListAppDto pickupListDto;
        PickupListItemAppDto pickupListItemDto;
        Map<String, PickupListItemAppDto> map = new HashMap<>();
        List<PickupListItemAppDto> pickupListItemDtos;
        List<Integer> packetIds = new ArrayList<>();
        Warehouse warehouse = warehouseService.getById(request.getWarehouseId());
        int itemCount = 0;
        Integer count=1;
        String key;

        List<Packet> packetList = packetService.getByIdIn(request.getPacketIds());

        //generate pickup list for input packets, ignore status
        if(!CollectionUtils.isEmpty(packetList)) {
            PickupList pickupList = new PickupList();
            pickupList.setGeneratedBy(request.getGeneratedBy());
            pickupList.setWarehouse(warehouse);
            upsert(pickupList);

            for (Packet packet : packetList) {
                for (PacketItem packetItem : packet.getPacketItems()) {
                    pickupListItem = new PickupListItem();
                    pickupListItem.setPickupList(pickupList);
                    pickupListItem.setPacketItem(packetItem);
                    upsertPickListItem(pickupListItem);

                    key = packetItem.getInboundStorage().getStorageLocation().getId() + "_" + packetItem.getInboundStorage().getProduct().getId()+"_"+packetItem.getInboundStorage().getLotId();

                    pickupListItemDto = map.get(key);
                    if (pickupListItemDto == null) {
                        itemCount++;
                        pickupListItemDto = new PickupListItemAppDto(pickupListItem);
                    } else {
                        pickupListItemDto.setQuantity(NumberUtil.round4(pickupListItemDto.getQuantity() + packetItem.getQuantity()));
                    }
                    pickupListItemDto.setSerialNumber(count);
                    count++;
                    map.put(key, pickupListItemDto);
                }
               
                packetService.upsert(packet);

                packetIds.add(packet.getId());
            }

            pickupList.setItemCount(itemCount);
            upsert(pickupList);

            pickupListDto = new PickupListAppDto(pickupList);
            pickupListDto.setPacketIds(packetIds);

            pickupListItemDtos = new ArrayList<>(map.values());
            pickupListItemDtos.sort(Comparator.comparing(PickupListItemAppDto::getZoneId)
                    .thenComparing(PickupListItemAppDto::getRackId).thenComparing(PickupListItemAppDto::getBinId));
            pickupListDto.setPickupListItems(pickupListItemDtos);

            response.setPickupList(pickupListDto);
            response.setMessage(packetList.size() + " successfully added in pickup list");
        } else {
            response.setMessage("No Packets Found");
        }
        response.setStatus(true);
        logger.info("Generate Pickup List Service Ended");
        return response;
	}
}
