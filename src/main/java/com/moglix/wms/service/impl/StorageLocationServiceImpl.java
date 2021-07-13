package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.moglix.wms.api.request.FindStorageLocationRequest;
import com.moglix.wms.api.request.SearchStorageLocationRequest;
import com.moglix.wms.api.request.SearchWithMsnRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.FetchStorageResponse;
import com.moglix.wms.api.response.GetProductsInStorageLocationResponse;
import com.moglix.wms.api.response.SearchStorageLocationResponse;
import com.moglix.wms.constants.StorageLocationType;
import com.moglix.wms.dto.StorageContent;
import com.moglix.wms.dto.StorageLocationDto;
import com.moglix.wms.entities.StorageLocation;
import com.moglix.wms.repository.StorageLocationRepository;
import com.moglix.wms.repository.ZoneRepository;
import com.moglix.wms.service.IStorageLocationService;
import com.moglix.wms.util.PaginationUtil;

/**
 * @author pankaj on 1/5/19
 */
@Service("storageLocationService")
public class StorageLocationServiceImpl implements IStorageLocationService {

    Logger log = LogManager.getLogger(StorageLocationServiceImpl.class);

    @Autowired
    private StorageLocationRepository repository;
    
    @Autowired
    private ZoneRepository zoneRepo;

    @Override
    public StorageLocation upsert(StorageLocation zone) {
        return repository.save(zone);
    }

    @Override
    public StorageLocation getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<StorageLocation> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public SearchStorageLocationResponse searchStorageLocation(SearchStorageLocationRequest request, Pageable page) {
        log.info("Search Storage Locations By Warehouse and Zone Service Started");
        SearchStorageLocationResponse response = null;
        Page<StorageLocation> storageLocations = repository.findByWarehouseIdAndZoneIdAndActiveAndTypeOrderByModifiedDesc(request.getWarehouseId(), request.getZoneId(),true, StorageLocationType.GOOD,  page);
        List<StorageLocationDto> storageLocationDtos = new ArrayList<>();
        if(!storageLocations.getContent().isEmpty()) {
            for(StorageLocation storageLocation : storageLocations) {
                storageLocationDtos.add(new StorageLocationDto(storageLocation));
            }
            response = (SearchStorageLocationResponse) PaginationUtil.setPaginationParams(storageLocations, new SearchStorageLocationResponse("Storage Location found : " + storageLocations.getTotalElements(), true, HttpStatus.OK.value()));
            response.setStorageLocations(storageLocationDtos);
        } else {
            return new SearchStorageLocationResponse("No Storage Location found for warehouse id: " + request.getWarehouseId() + " and zone id: " + request.getZoneId(), true, HttpStatus.OK.value());
        }
        log.info("Search Storage Locations By Warehouse and Zone Service Ended");
        return response;
    }
    
    @Override
    @Transactional
    public SearchStorageLocationResponse searchStorageLocationForBinTransfer(SearchStorageLocationRequest request, Pageable page) {
        log.info("Search Storage Locations By Warehouse and Zone Service Started");
        SearchStorageLocationResponse response = null;
        Page<StorageLocation> storageLocations = repository.findByWarehouseIdAndZoneIdAndTypeNotAndActiveOrderByModifiedDesc(request.getWarehouseId(), request.getZoneId(), StorageLocationType.BAD, true, page);
        List<StorageLocationDto> storageLocationDtos = new ArrayList<>();
        if(!storageLocations.getContent().isEmpty()) {
            for(StorageLocation storageLocation : storageLocations) {
                storageLocationDtos.add(new StorageLocationDto(storageLocation));
            }
            response = (SearchStorageLocationResponse) PaginationUtil.setPaginationParams(storageLocations, new SearchStorageLocationResponse("Storage Location found : " + storageLocations.getTotalElements(), true, HttpStatus.OK.value()));
            response.setStorageLocations(storageLocationDtos);
        } else {
            return new SearchStorageLocationResponse("No Storage Location found for warehouse id: " + request.getWarehouseId() + " and zone id: " + request.getZoneId(), true, HttpStatus.OK.value());
        }
        log.info("Search Storage Locations By Warehouse and Zone Service Ended");
        return response;
    }

	@Override
	public StorageLocation findByWarehouseIdAndZoneIdAndRackIdAndBinId(Integer warehouseId, Integer zoneId,
			Integer rackId, Integer binId) {
		return repository.findByWarehouseIdAndZoneIdAndRackIdAndBinId(warehouseId, zoneId, rackId, binId);
	}

	@Override
	public GetProductsInStorageLocationResponse getProductsInStorageLocation(int storageLocationId) {

		List<StorageContent> contents = new ArrayList<StorageContent>();
		contents = repository.getStorageContents(storageLocationId);

		if (CollectionUtils.isEmpty(contents)) {
			return new GetProductsInStorageLocationResponse("No Storage Location Found", false, HttpStatus.OK.value());
		} else {
			GetProductsInStorageLocationResponse response = new GetProductsInStorageLocationResponse(
					"Found " + contents.size() + " products for selected storage location", true,
					HttpStatus.OK.value());

			response.setProducts(contents);
			return response;
		}
	}
	
	@Override
	public GetProductsInStorageLocationResponse getProductsInStorageLocation(int storageLocationId, @Valid SearchWithMsnRequest request) {
		
		List<StorageContent> contents = new ArrayList<StorageContent>();
		log.info("Request :: " + request);
		
		if (request.getProductMsnList().isEmpty()) {
			
			contents = repository.getStorageContents(storageLocationId);
		} 
		else {
			for (int i = 0; i < request.getProductMsnList().size(); i++) {
				contents.addAll(repository.getStorageContentsWithMsnAndStorageId(storageLocationId, request.getProductMsnList().get(i)));
			}
		}

		if (CollectionUtils.isEmpty(contents)) {
			return new GetProductsInStorageLocationResponse("No Storage Location Found", false, HttpStatus.OK.value());
		}
		else {
			GetProductsInStorageLocationResponse response = new GetProductsInStorageLocationResponse("Found " + contents.size() + " products for selected storage location", true, HttpStatus.OK.value());
			response.setProducts(contents);
			return response;
		}
	}

	@Override
	public GetProductsInStorageLocationResponse getProductsInStorageLocation(@Valid SearchWithMsnRequest request) {
		List<StorageContent> contents = new ArrayList<StorageContent>();
		if (request != null) {
			// contents = repository.getStorageContentsWithMsn(productMsn.get(0));
			for (int i = 0; i < request.getProductMsnList().size(); i++) {
				contents.addAll(repository.getStorageContentsWithMsn(request.getProductMsnList().get(i),
						request.getWarehouseId()));
			}
		}

		if (CollectionUtils.isEmpty(contents)) {
			return new GetProductsInStorageLocationResponse("No Storage Location Found", false, HttpStatus.OK.value());
		} else {
			GetProductsInStorageLocationResponse response = new GetProductsInStorageLocationResponse(
					"Found " + contents.size() + " products for selected warehouse and productMsnList", true,
					HttpStatus.OK.value());

			response.setProducts(contents);
			return response;
		}
	}

	@Override
	public GetProductsInStorageLocationResponse getProductsInExpiry(Integer storageLocationId,
			@Valid SearchWithMsnRequest request) {

		List<StorageContent> contents = new ArrayList<StorageContent>();
		if (request.getProductMsnList().isEmpty()) {
			contents = repository.getStorageContents(storageLocationId);
		} else {
			for (int i = 0; i < request.getProductMsnList().size(); i++) {
				contents.addAll(repository.getStorageContentsWithMsnExpiry(request.getProductMsnList().get(i),
						request.getWarehouseId(), storageLocationId));
			}

		}

		if (CollectionUtils.isEmpty(contents)) {
			return new GetProductsInStorageLocationResponse("No Storage Location Found", false, HttpStatus.OK.value());
		} else {
			GetProductsInStorageLocationResponse response = new GetProductsInStorageLocationResponse(
					"Found " + contents.size() + " products for selected storage location", true,
					HttpStatus.OK.value());

			response.setProducts(contents);
			return response;
		}
	}

	//apps
	@Override
	public FetchStorageResponse fetchStorageLocation(FindStorageLocationRequest request) {
       
		StorageLocation storageLocation = repository.fetchstoragelocationFromZoneandBin(request.getZoneBin() , request.getWarehouseId() );
		
		FetchStorageResponse response=new FetchStorageResponse();
		
		if(storageLocation!=null) {
			String zone=storageLocation.getZone().getName();
			Integer storageLocationId=storageLocation.getId();
			log.info("Storage Location :: "+storageLocation.getId());
			response.setZone(zone);
			response.setStorageLocationId(storageLocationId);
			response.setStatus(true);
			return response;
		}else {
			response.setMessage("NO LOCATION FOUND");
			response.setStatus(false);
			return response;
		}
		
	
	}

}
