package com.moglix.wms.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import com.moglix.wms.api.request.GetWarehouseByIdRequest;
import com.moglix.wms.api.request.SearchWarehouseRequest;
import com.moglix.wms.api.response.BlockedInventoryDataResponse;
import com.moglix.wms.api.response.ExpiredInventoryDataResponse;
import com.moglix.wms.api.response.GetWarehouseByIdResponse;
import com.moglix.wms.api.response.InventoryDataResponse;
import com.moglix.wms.api.response.SearchWarehouseResponse;
import com.moglix.wms.api.response.VmiReportDataResponse;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.dto.BlockedInventoryDTO;
import com.moglix.wms.dto.DnDataDTO;
import com.moglix.wms.dto.ExpiredInventoryDTO;
import com.moglix.wms.dto.InventoryDataDTO;
import com.moglix.wms.dto.InventoryDataResult;
import com.moglix.wms.dto.VmiReportDataDTO;
import com.moglix.wms.dto.WarehouseDto;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.mapper.InboundStorageMapper;
import com.moglix.wms.repository.BlockedProductInventoryRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.InboundStorageRepository;
import com.moglix.wms.repository.WarehouseRepository;
import com.moglix.wms.service.IWarehouseService;
import com.moglix.wms.util.CSVUtil;

/**
 * @author pankaj on 30/4/19
 */
@Service("warehouseService")
public class WarehouseServiceImpl implements IWarehouseService {

    Logger log = LogManager.getLogger(WarehouseServiceImpl.class);

    @Autowired
    private WarehouseRepository repository;
    
    @Autowired
    private InboundStorageRepository inboundStoragerepo;
    
    @Autowired
    private BlockedProductInventoryRepository blockedProductInventoryRepo;
    
    @Autowired
    private InboundRepository inboundRepo;
    
    @Override
    public Warehouse upsert(Warehouse warehouse) {
        repository.save(warehouse);
        return warehouse;
    }

    @Override
    public Warehouse getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Warehouse> getAll() {
        return repository.findAll();
    }
    
    @Override
	public List<Warehouse> getAllByISONumber(Integer isoNumber, Boolean isActive) {
		return repository.findAllByIsoNumberAndIsActive(isoNumber, isActive);
	}

    @Override
    @Transactional
    public SearchWarehouseResponse searchWarehouse(Integer countryId) { // request can be used later on for filters
        log.info("Search Warehouse Service Started");
        SearchWarehouseResponse response = new SearchWarehouseResponse();
        //List<Warehouse> warehouses = getAll();
        List<Warehouse> warehouses = getAllByISONumber(countryId, true);
        if(!CollectionUtils.isEmpty(warehouses)) {
            for(Warehouse warehouse : warehouses) {
                response.getWarehouses().add(new WarehouseDto(warehouse));
            }
            response.setMessage("warehouse found : " + warehouses.size());
        } else {
            response.setMessage("No Warehouse found");
        }
        response.setStatus(true);
        response.setCode(HttpStatus.OK.value());
        log.info("Search Warehouse Service Ended");
        return response;
    }

    @Override
    @Transactional
    public GetWarehouseByIdResponse getWarehouseById(GetWarehouseByIdRequest request) {
        log.info("Get Warehouse By Id Service Started");
        GetWarehouseByIdResponse response = new GetWarehouseByIdResponse();
        Warehouse warehouse = getById(request.getId());
        if(warehouse != null) {
            response.setWarehouse(new WarehouseDto(warehouse));
            response.setMessage("warehouse found");
        } else {
            response.setMessage("No Warehouse found for id: " + request.getId());
        }
        response.setStatus(true);
        response.setCode(HttpStatus.OK.value());
        log.info("Get Warehouse By Id Service Ended");
        return response;
    }

	@Override
	@Transactional
	public void getInventoryData(Optional<Integer> warehouseId, HttpServletResponse response) throws IOException {
		
		List<InventoryDataDTO> freeInventoryData;
		List<InventoryDataDTO> inboundDataStarted;
		List<InventoryDataDTO> inventoryAllocated;
		List<InventoryDataDTO> inventoryPacked;
		List<InventoryDataDTO> inventoryPackedQuantityGreaterThanZero = new ArrayList<>();
		List<InventoryDataDTO> inventoryInboundData = new ArrayList<>();
		if(warehouseId.isPresent()) {
			
			freeInventoryData = inboundStoragerepo.getFreeInventoryDataByWarehouseId(warehouseId.get());
			inventoryAllocated= inboundStoragerepo.getInventoryAllocatedByWarehouseId(warehouseId.get());
			inventoryPacked =inboundStoragerepo.getInventoryPackedByWarehouseId(warehouseId.get());
			  inboundDataStarted =inboundRepo.getInboundDataByWarehouseIdStarted(warehouseId.get());
		}else {
			freeInventoryData = inboundStoragerepo.getFreeInventoryData();
		    inventoryAllocated = inboundStoragerepo.getInventoryAllocated();
			inventoryPacked =inboundStoragerepo.getInventoryPacked();
			  inboundDataStarted =inboundRepo.getInboundDataStarted();
			 
		}
		    double scale = Math.pow(10, 4);
		for(InventoryDataDTO inventPacked : inventoryPacked) {
			if((Math.round(inventPacked.getQuantity() * scale) / scale)>0) {
			inventoryPackedQuantityGreaterThanZero.add(inventPacked);
			}
		}
		
		inventoryInboundData.addAll(freeInventoryData);
		inventoryInboundData.addAll(inventoryAllocated);
		inventoryInboundData.addAll(inventoryPackedQuantityGreaterThanZero);
	  inventoryInboundData.addAll(inboundDataStarted);
		 
		
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventoryData.csv");
		CSVUtil.writeInventoryDataToCsv(response.getWriter(), inventoryInboundData);
	}
	
	@Override
	@Transactional
	public InventoryDataResponse getInventoryDataAngular(Optional<Integer> warehouseId, HttpServletResponse response) throws IOException {
		
		List<InventoryDataDTO> freeInventoryData;
		List<InventoryDataDTO> returnFreeInventory;
		List<InventoryDataDTO> inboundDataStarted;
		List<InventoryDataDTO> returnInboundDataStarted;
		List<InventoryDataDTO> inventoryAllocated;
		List<InventoryDataDTO> inventoryPacked;
		List<DnDataDTO> DnData;
		List<InventoryDataDTO> inventoryPackedQuantityGreaterThanZero = new ArrayList<>();
		List<InventoryDataDTO> inventoryInboundData = new ArrayList<>();
		if(warehouseId.isPresent()) {
			freeInventoryData = inboundStoragerepo.getFreeInventoryDataByWarehouseId(warehouseId.get());
			returnFreeInventory = inboundStoragerepo.getFreeReturnInventoryDataByWarehouseId(warehouseId.get());
			inventoryAllocated= inboundStoragerepo.getInventoryAllocatedByWarehouseId(warehouseId.get());
			inventoryPacked =inboundStoragerepo.getInventoryPackedByWarehouseId(warehouseId.get());
			inboundDataStarted =inboundRepo.getInboundDataByWarehouseIdStarted(warehouseId.get());
			returnInboundDataStarted = inboundRepo.getReturnInboundDataByWarehouseIdStarted(warehouseId.get());
			DnData=inboundRepo.getDnDataByWarehouseId(warehouseId.get());
		} else {
			freeInventoryData = inboundStoragerepo.getFreeInventoryData();
			returnFreeInventory = inboundStoragerepo.getReturnFreeInventoryData();
			inventoryAllocated = inboundStoragerepo.getInventoryAllocated();
			inventoryPacked = inboundStoragerepo.getInventoryPacked();
			inboundDataStarted = inboundRepo.getInboundDataStarted();
			returnInboundDataStarted = inboundRepo.getReturnInboundDataStarted();
			DnData=inboundRepo.getDnData();
		}
		    double scale = Math.pow(10, 4);
		for(InventoryDataDTO inventPacked : inventoryPacked) {
			if((Math.round(inventPacked.getQuantity() * scale) / scale)>0) {
			inventoryPackedQuantityGreaterThanZero.add(inventPacked);
			}
		}
		inventoryInboundData.addAll(returnFreeInventory);
		inventoryInboundData.addAll(freeInventoryData);
		inventoryInboundData.addAll(inventoryAllocated);
		inventoryInboundData.addAll(inventoryPackedQuantityGreaterThanZero);
		inventoryInboundData.addAll(returnInboundDataStarted);
		inventoryInboundData.addAll(inboundDataStarted);
		 
	  InventoryDataResponse respons = new InventoryDataResponse("Successfully found inventory data", true, HttpStatus.OK.value());
	  respons.setInventoryData(inventoryInboundData);
	  respons.setDnData(DnData);
		
	  return respons;
	}

	@Override
	public void downloadInventory(HttpServletResponse response) {
		List<InventoryDataResult> storages = inboundStoragerepo.getInventoryReportData();
		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventoryData.csv");
		try (
			     CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT
	                      .withHeader("wareHouseName", "productMsn", "productName", "inventoryType", "zone", "bin", "supplierPoId", "suplierPoItemId", "mrnDate", "allocatedQuantity", "availableQuantity","totalQty", "purchasePrice", "invoiceNumber"));
	    ){
			for (InventoryDataResult storage : storages) {
				List<String> record = null;
				if (storage.getInventoryType().equals(InboundType.NEW)
						|| storage.getInventoryType().equals(InboundType.SUPPLIER_RETURN)) {
					record = InboundStorageMapper.createInventoryDataFromStorage(storage);
				} else if (storage.getInventoryType().equals(InboundType.CUSTOMER_RETURN)) {
					record = InboundStorageMapper.createInventoryDataFromStorage(storage);
					if (!storage.getInventorize()) {
						record.add(storage.getInvoiceNumber());
					}else {
						record.add("N/A");
					}
				}
				csvPrinter.printRecord(record);
			}
			csvPrinter.flush();
		}catch (Exception e) {
			log.error("Error Occurred while creating CSV", e);
		}
	}

	@Override
	@Transactional
	public BlockedInventoryDataResponse getBlockedInventoryDataAngular(Optional<Integer> warehouseId) {
		
		List<BlockedInventoryDTO> bulkInvoiceReportRecords = new ArrayList<>();
		if (warehouseId.isPresent()) {
			bulkInvoiceReportRecords = blockedProductInventoryRepo.getBlockedProductInventoryOrderByWarehouseId(warehouseId.get());
			Set<String> productMSN = bulkInvoiceReportRecords.stream().collect(Collectors.groupingBy(BlockedInventoryDTO::getProductMsn)).keySet();
			if(!CollectionUtils.isEmpty(productMSN)) {
				bulkInvoiceReportRecords.addAll(blockedProductInventoryRepo.findByProductMsnNotInAndWarehouseId(productMSN, warehouseId.get()));
			}else {
				bulkInvoiceReportRecords.addAll(blockedProductInventoryRepo.findByWarehouseId(warehouseId.get()));
			}
		}else {
			bulkInvoiceReportRecords = blockedProductInventoryRepo.getBlockedProductInventoryOrder();
			Set<String> productMSN = bulkInvoiceReportRecords.stream().collect(Collectors.groupingBy(BlockedInventoryDTO::getProductMsn)).keySet();
			bulkInvoiceReportRecords.addAll(blockedProductInventoryRepo.findByProductMsnNotIn(productMSN));
		}
		
		BlockedInventoryDataResponse response = new BlockedInventoryDataResponse("BlockedInventory Response", true, HttpStatus.OK.value());
		
		response.setInventoryData(bulkInvoiceReportRecords);

		return response;
	}

	@Override
	public VmiReportDataResponse getVmiReportDataAngular(HttpServletResponse response, Integer isoNumber) {
		VmiReportDataResponse vmiReportDataResponse = new VmiReportDataResponse("Successfully found vmi report data", true, HttpStatus.OK.value());
		List<VmiReportDataDTO> vmiReportData =  inboundStoragerepo.getVmiReport(isoNumber);
//		for(VmiReportDataDTO vmiReportDataDTO : vmiReportData) {
//			log.info(vmiReportDataDTO.getProductMsn()+ " "+ vmiReportDataDTO.getProductMsn()+" "+vmiReportDataDTO.getMaximumQuantity()+ " "+ vmiReportDataDTO.getMinimumQuantity()+"  "+vmiReportDataDTO.getPlantId()+" "+ vmiReportDataDTO.getPurchasePrice()+ " "+ vmiReportDataDTO.getWarehouseId());
//		}
		log.info("vmi report size : "+ vmiReportData.size() );
		vmiReportDataResponse.setVmiReportData(vmiReportData);
		
		return vmiReportDataResponse;
		
	}

	@Override
	@Transactional
	public ExpiredInventoryDataResponse getExpiredInventoryReportData(Optional<Integer> warehouseId) {
		List<ExpiredInventoryDTO> expiredInventories = new ArrayList<>();

		if (warehouseId.isPresent()) {
			expiredInventories = inboundStoragerepo.getExpiredInventoryByWarehouseId(warehouseId.get());
		} else {
			expiredInventories = inboundStoragerepo.getExpiredInventory();
		}

		ExpiredInventoryDataResponse response = new ExpiredInventoryDataResponse(
				"Found " + expiredInventories.size() + " expired inventories", true, HttpStatus.OK.value());

		response.setExpiredInventory(expiredInventories);
		return response;
	}

	
}
