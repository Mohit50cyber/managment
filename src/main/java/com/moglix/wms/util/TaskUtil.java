package com.moglix.wms.util;

import java.util.Collections;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moglix.wms.constants.StorageLocationType;
import com.moglix.wms.dto.BatchCSVRecordContent;
import com.moglix.wms.dto.ProductCSVRecordContent;
import com.moglix.wms.dto.StorageCSVRecordContent;
import com.moglix.wms.dto.StorageCSVRecordContentUpdate;
import com.moglix.wms.entities.Batch;
import com.moglix.wms.entities.Bin;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.entities.Product;
import com.moglix.wms.entities.Rack;
import com.moglix.wms.entities.StorageLocation;
import com.moglix.wms.entities.Warehouse;
import com.moglix.wms.entities.Zone;
import com.moglix.wms.mapper.BatchMapper;
import com.moglix.wms.mapper.InboundMapper;
import com.moglix.wms.mapper.ProductMapper;
import com.moglix.wms.repository.BatchRepository;
import com.moglix.wms.repository.BinRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.repository.ProductsRepository;
import com.moglix.wms.repository.RackRepository;
import com.moglix.wms.repository.StorageLocationRepository;
import com.moglix.wms.repository.WarehouseRepository;
import com.moglix.wms.repository.ZoneRepository;
import com.moglix.wms.service.IStorageLocationService;


@Component
public class TaskUtil {

	private static Logger log = LogManager.getLogger(TaskUtil.class);
	
	@Autowired
	private IStorageLocationService storageLocationService;
	
	@Autowired
	BatchRepository batchRepository;
	
	@Autowired
	InboundRepository inboundRepository;
	
	@Autowired
	ProductsRepository productRepository;
	
	@Autowired
	ZoneRepository zoneRepository;
	
	@Autowired
	RackRepository rackRepository;
	
	@Autowired
	BinRepository binRepository;
	
	@Autowired
	WarehouseRepository warehouseRepository;
	
	@Autowired	
	StorageLocationRepository storageLocationRepository;
	
	@Transactional
	public void processRecord(ObjectMapper mapper, CSVRecord record) {
		
		BatchCSVRecordContent content = mapper.convertValue(record.toMap(), BatchCSVRecordContent.class);
		
		Batch batch = batchRepository.findByRefNoAndBatchType(content.getRefNo(), content.getBatchType()).orElse(null);
		
		if(batch == null) {
			//Create New Batch and add inbound to it
			batch = BatchMapper.createBatchFromCsvRecord(content);
			Inbound inbound = InboundMapper.createInboundFromCSVRecord(content);
			batch.setInbounds(Collections.singleton(inbound));
			inbound.setBatch(batch);
			batchRepository.save(batch);						
		} else {
			//Add Inbound to existing batch
			Inbound inbound = InboundMapper.createInboundFromCSVRecord(content);
			inbound.setProduct(productRepository.getUniqueByProductMsn(content.getProductMsn()));
			batch.getInbounds().add(inbound);
			inbound.setBatch(batch);
			batchRepository.save(batch);
		}
	}
	
	
	@Transactional
	public void processProductRecord(ObjectMapper mapper, CSVRecord record) {
		
		ProductCSVRecordContent content = mapper.convertValue(record.toMap(), ProductCSVRecordContent.class);
		 
		Product product = productRepository.getUniqueByProductMsn(content.getProductMsn());
		
		if(product == null) {
			//Create New Product
			product = ProductMapper.createProductFromCSVRecord(content);
			productRepository.save(product);	
			log.info("Product Created for msn: " + content.getProductMsn() + " and UOM: " + content.getUom());
		}else {
			log.info("Product Already exists for msn: " + content.getProductMsn() + " and UOM: " + content.getUom());
			if(StringUtils.isBlank(product.getUom())) {
				log.info("Updating UOM of product: " + content.getProductMsn() + " to: " + content.getUom());
				product.setUom(content.getUom());
			}
			
			if(StringUtils.isBlank(product.getProductBrand())) {
				log.info("Updating Brand of product: " + product.getProductBrand() + " to: " + content.getProductBrand());
				product.setProductBrand(content.getProductBrand());
			}
			
			if(product.getExpiryDateManagementEnabled() != null) {
				product.setExpiryDateManagementEnabled(content.getExpiryDateManagementEnabled());
			}
			
			if(product.getLotManagementEnabled() != null){
				product.setLotManagementEnabled(content.getLotManagementEnabled());
			}
		}
	}


	@Transactional
	public void processStorageRecord(ObjectMapper mapper, CSVRecord record) {
		StorageCSVRecordContent content = mapper.convertValue(record.toMap(), StorageCSVRecordContent.class);
		
		Warehouse warehouse = warehouseRepository.findByName(content.getWarehouseName()).orElse(null);
		
		if(warehouse != null) {
			Zone zone = zoneRepository.findByWarehouseIdAndName(warehouse.getId(), content.getZoneName()).orElse(null);
			Rack rack = null;
			Bin bin = null;
			if(zone != null) {
				rack = rackRepository.findByZoneIdAndName(zone.getId(), content.getRackName()).orElse(null);
				if(rack != null) {
					bin = binRepository.findByRackIdAndName(rack.getId(), content.getBinName()).orElse(null);
					if(bin != null) {
						log.info("Storage Location Already exists");
					}else {
						createNewStorageLocation(content, warehouse, zone, rack, bin);
					}
				}else {
					createNewStorageLocation(content, warehouse, zone, rack, bin);
				}
			}else {
				createNewStorageLocation(content, warehouse, zone, rack, bin);				
			}
		}else {
			log.warn("Invalid Warehouse Name Value :" + content.getWarehouseName());
		}		
	}

	@Transactional
	public void processStorageRecordUpdate(ObjectMapper mapper, CSVRecord record) throws Exception {
		StorageCSVRecordContentUpdate content = mapper.convertValue(record.toMap(), StorageCSVRecordContentUpdate.class);
		
		Warehouse warehouse = warehouseRepository.findByName(content.getWarehouseName()).orElse(null);
		
		if(warehouse != null) {
			Zone zone = zoneRepository.findByWarehouseIdAndName(warehouse.getId(), content.getZoneName()).orElse(null);
			Rack rack = null;
			Bin bin = null;
			if(zone != null) {
				rack = rackRepository.findByZoneIdAndName(zone.getId(), content.getRackName()).orElse(null);
				if(rack != null) {
					bin = binRepository.findByRackIdAndName(rack.getId(), content.getBinName()).orElse(null);
					if(bin != null) {
						StorageLocation storageLocation = storageLocationService.findByWarehouseIdAndZoneIdAndRackIdAndBinId(warehouse.getId(),zone.getId() , rack.getId(), bin.getId());
						double quantity = 0.0 ;
						for(InboundStorage inboundStorage : storageLocation.getInboundStorages() ) {
						   quantity += inboundStorage.getQuantity();
						}
						if(quantity == 0.0) {
							storageLocation.setType(StorageLocationType.valueOf(content.getType()));
							if(content.getStatus().equals("active")) {
								storageLocation.setActive(true);
							} else if (content.getStatus().equals("inactive")) {
								storageLocation.setActive(false);
							}
							
							}else if (quantity > 0.0) {
							throw new Exception("Inbound storage quantity is more than zero so no update of tyoe and status");
						}
						storageLocationRepository.save(storageLocation);
						log.info("Storage Location Already exists");
					}else {
						createNewStorageLocationUpdate(content, warehouse, zone, rack, bin);
					}
				}else {
					createNewStorageLocationUpdate(content, warehouse, zone, rack, bin);
				}
			}else {
				createNewStorageLocationUpdate(content, warehouse, zone, rack, bin);				
			}
		}else {
			log.warn("Invalid Warehouse Name Value :" + content.getWarehouseName());
		}		
	}

	private void createNewStorageLocation(StorageCSVRecordContent content, Warehouse warehouse, Zone zone, Rack rack, Bin bin) {
		//Create New StorageLocation 
		if(zone == null) {
			zone = new Zone();
			zone.setWarehouse(warehouse);
			zone.setName(content.getZoneName());
			
			zone = zoneRepository.save(zone);
		}
		
		if(rack == null) {
			rack = new Rack();
			rack.setZone(zone);
			rack.setName(content.getRackName());
			
			rack = rackRepository.save(rack);
		}
		
		if(bin == null) {
			bin = new Bin();
			bin.setRack(rack);
			bin.setName(content.getBinName());			
			bin = binRepository.save(bin);
		}
		
		StorageLocation storageLocation = new StorageLocation();
		
		storageLocation.setBin(bin);
		
		storageLocation.setRack(rack);
		
		storageLocation.setZone(zone);
		
		storageLocation.setWarehouse(warehouse);
		
		storageLocation.setActive(true);
		
		storageLocation.setFull(false);
		
		if(StringUtils.isNotBlank(content.getDepth())) {
			storageLocation.setDepth(Double.parseDouble(content.getDepth()));
		}else {
			storageLocation.setDepth(0.0d);

		}
		
		if(StringUtils.isNotBlank(content.getWidth())) {
			storageLocation.setWidth(Double.parseDouble(content.getWidth()));
		}else {
			storageLocation.setWidth(0.0d);
		}
		
		if(StringUtils.isNotBlank(content.getHeight())) {
			storageLocation.setHeight(Double.parseDouble(content.getHeight()));
		}else {
			storageLocation.setHeight(0.0d);
		}
				
		storageLocation.setName(content.getStorageLocationName());
		
		storageLocationRepository.save(storageLocation);
		
		log.info("Created new Storage Location with Warehouse: " + content.getWarehouseName() + " and Zone: "
				+ content.getZoneName() + " and Rack: " + content.getRackName() + " and Bin: "
				+ content.getBinName());
	}
	
	
	private void createNewStorageLocationUpdate(StorageCSVRecordContentUpdate content, Warehouse warehouse, Zone zone, Rack rack, Bin bin) {
		//Create New StorageLocation 
		if(zone == null) {
			zone = new Zone();
			zone.setWarehouse(warehouse);
			zone.setName(content.getZoneName());
			
			zone = zoneRepository.save(zone);
		}
		
		if(rack == null) {
			rack = new Rack();
			rack.setZone(zone);
			rack.setName(content.getRackName());
			
			rack = rackRepository.save(rack);
		}
		
		if(bin == null) {
			bin = new Bin();
			bin.setRack(rack);
			bin.setName(content.getBinName());			
			bin = binRepository.save(bin);
		}
		
		StorageLocation storageLocation = new StorageLocation();
		
		storageLocation.setBin(bin);
		
		storageLocation.setRack(rack);
		
		storageLocation.setZone(zone);
		
		storageLocation.setWarehouse(warehouse);
		
		if(content.getStatus().equals("active")) {
			storageLocation.setActive(true);
		} else if (content.getStatus().equals("inactive")) {
			storageLocation.setActive(false);
		}
		
		storageLocation.setType(StorageLocationType.valueOf(content.getType()));
		storageLocation.setFull(false);
		
		if(StringUtils.isNotBlank(content.getDepth())) {
			storageLocation.setDepth(Double.parseDouble(content.getDepth()));
		}else {
			storageLocation.setDepth(0.0d);

		}
		
		if(StringUtils.isNotBlank(content.getWidth())) {
			storageLocation.setWidth(Double.parseDouble(content.getWidth()));
		}else {
			storageLocation.setWidth(0.0d);
		}
		
		if(StringUtils.isNotBlank(content.getHeight())) {
			storageLocation.setHeight(Double.parseDouble(content.getHeight()));
		}else {
			storageLocation.setHeight(0.0d);
		}
				
		storageLocation.setName(content.getStorageLocationName());
		
		storageLocationRepository.save(storageLocation);
		
		log.info("Created new Storage Location with Warehouse: " + content.getWarehouseName() + " and Zone: "
				+ content.getZoneName() + " and Rack: " + content.getRackName() + " and Bin: "
				+ content.getBinName());
	}
}


