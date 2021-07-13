package com.moglix.wms.task;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moglix.wms.constants.Constants;
import com.moglix.wms.dto.ProductInventoryConfigContentCSV;
import com.moglix.wms.entities.Plant;
import com.moglix.wms.entities.PlantProductInventoryConfigMapping;
import com.moglix.wms.entities.ProductInventoryConfig;
import com.moglix.wms.entities.Supplier;
import com.moglix.wms.entities.SupplierProductInventoryConfigMapping;
import com.moglix.wms.repository.PlantRepository;
import com.moglix.wms.repository.ProductInventoryConfigRepository;
import com.moglix.wms.repository.SupplierRepository;

@Service
public class ProductInventoryConfigImportTask {
	
	@Autowired
	private ProductInventoryConfigRepository productInventoryConfigRepo;
	
	@Autowired
	private PlantRepository plantRepo;
	
	@Autowired
	private SupplierRepository supplierRepo;
	
	private Logger log = LogManager.getLogger(ProductInventoryConfigImportTask.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Transactional
	public boolean uploadProductInventoryConfigs(String filename) {
		boolean status = true;
		try {
			Map<String, ProductInventoryConfigContentCSV> csvMap = getProductInventoryImportContentCSV(
					Constants.UPLOADED_FOLDER + filename);

			Map<String, Double> minQtyMap = csvMap.values().stream().collect(Collectors.groupingBy(
					e -> e.getProductMsn() + "-" + e.getWarehouseId(), Collectors.summingDouble(e -> e.getMinQty())));

			Map<String, Double> maxQtyMap = csvMap.values().stream().collect(Collectors.groupingBy(
					e -> e.getProductMsn() + "-" + e.getWarehouseId(), Collectors.summingDouble(e -> e.getMaxQty())));

			for (Map.Entry<String, ProductInventoryConfigContentCSV> entry : csvMap.entrySet()) {

				String productMsn = entry.getKey().split("-")[0];
				Integer warehouseId = Integer.valueOf(entry.getKey().split("-")[1]);


				ProductInventoryConfig inventoryConfig = productInventoryConfigRepo.findByProductMsnAndWarehouseId(productMsn, warehouseId);

				if (inventoryConfig != null) {
					inventoryConfig.setPurchasePrice(entry.getValue().getPurchasePrice());

					inventoryConfig.setMaximumQuantity(maxQtyMap.getOrDefault(
							entry.getValue().getProductMsn() + "-" + entry.getValue().getWarehouseId(),
							inventoryConfig.getMaximumQuantity()));
					
					inventoryConfig.setProductMsn(productMsn);
					inventoryConfig.setWarehouseId(warehouseId);

					inventoryConfig.setMinimumQuantity(minQtyMap.getOrDefault(
							entry.getValue().getProductMsn() + "-" + entry.getValue().getWarehouseId(),
							inventoryConfig.getMaximumQuantity()));

					Plant plant = plantRepo.findByBuyersPlantId(entry.getValue().getPlantId())
							.orElse(new Plant(entry.getValue().getPlantId()));

					setPlantForInventoryConfig(inventoryConfig, plant);

					Supplier supplier = supplierRepo.findByEmsSupplierId(entry.getValue().getSupplierId())
							.orElse(new Supplier(entry.getValue().getSupplierId()));

					setSupplierForInventoryConfig(inventoryConfig, supplier);
				} else {
					inventoryConfig = new ProductInventoryConfig();
					
					inventoryConfig.setProductMsn(productMsn);
					inventoryConfig.setWarehouseId(warehouseId);

					inventoryConfig.setPurchasePrice(entry.getValue().getPurchasePrice());

					inventoryConfig.setMaximumQuantity(maxQtyMap.getOrDefault(
							entry.getValue().getProductMsn() + "-" + entry.getValue().getWarehouseId(),
							inventoryConfig.getMaximumQuantity()));

					inventoryConfig.setMinimumQuantity(minQtyMap.getOrDefault(
							entry.getValue().getProductMsn() + "-" + entry.getValue().getWarehouseId(),
							inventoryConfig.getMaximumQuantity()));

					Plant plant = plantRepo.findByBuyersPlantId(entry.getValue().getPlantId())
							.orElse(new Plant(entry.getValue().getPlantId()));

					setPlantForInventoryConfig(inventoryConfig, plant);

					Supplier supplier = supplierRepo.findByEmsSupplierId(entry.getValue().getSupplierId())
							.orElse(new Supplier(entry.getValue().getSupplierId()));

					setSupplierForInventoryConfig(inventoryConfig, supplier);
				}

				productInventoryConfigRepo.save(inventoryConfig);
			}
		}catch(Exception e){
			log.error("Error Occured while importing product Inventory Configs", e);
			status = false;
		}
		return status;
	}
	
	private void setSupplierForInventoryConfig(ProductInventoryConfig inventoryConfig, Supplier supplier) {
		if (inventoryConfig.getSupplierProductInventoryConfigMappings() != null && inventoryConfig
				.getSupplierProductInventoryConfigMappings().stream().map(e -> e.getSupplier().getEmsSupplierId())
				.collect(Collectors.toList()).contains(supplier.getEmsSupplierId())) {
			return;
		}else {
			SupplierProductInventoryConfigMapping mapping = new SupplierProductInventoryConfigMapping();
			mapping.setSupplier(supplier);
			mapping.setProductInventoryConfig(inventoryConfig);
			if(inventoryConfig.getSupplierProductInventoryConfigMappings() == null) {
				Set<SupplierProductInventoryConfigMapping> mappings = new HashSet<>();
				mappings.add(mapping);
				supplier.setSupplierProductInventoryConfigMappings(mappings);
				inventoryConfig.setSupplierProductInventoryConfigMappings(mappings);
			}else {
				inventoryConfig.getSupplierProductInventoryConfigMappings().add(mapping);
			}
		}
	}

	private void setPlantForInventoryConfig(ProductInventoryConfig inventoryConfig, Plant plant) {
		if (inventoryConfig.getPlantProductInventoryConfigMappings() != null && inventoryConfig
				.getPlantProductInventoryConfigMappings().stream().map(e -> e.getPlant().getBuyersPlantId())
				.collect(Collectors.toList()).contains(plant.getBuyersPlantId())) {
			return;
		}
		

		PlantProductInventoryConfigMapping mapping = new PlantProductInventoryConfigMapping();
		mapping.setPlant(plant);
		mapping.setProductInventoryConfig(inventoryConfig);
		if(inventoryConfig.getPlantProductInventoryConfigMappings() == null) {
			Set<PlantProductInventoryConfigMapping> mappings = new HashSet<>();
			mappings.add(mapping);
			inventoryConfig.setPlantProductInventoryConfigMappings(mappings);
		}else {
			inventoryConfig.getPlantProductInventoryConfigMappings().add(mapping);
		}
	
	}

	private Map<String, ProductInventoryConfigContentCSV> getProductInventoryImportContentCSV(String filePath) {
		log.info("getProductInventoryImportContentCSV () started  with filepath :" + filePath);
		Map<String, ProductInventoryConfigContentCSV> inventoryProductMsnMap = new HashMap<String, ProductInventoryConfigContentCSV>();

		if (!new File(filePath).exists()) {
			throw new RuntimeException("File Not Found at path: " + filePath);
		}

		try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

			CSVParser parser = CSVParser.parse(reader,
					CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

			List<CSVRecord> records = parser.getRecords();

			for (CSVRecord record : records) {
				ProductInventoryConfigContentCSV content = mapper.convertValue(record.toMap(),
						ProductInventoryConfigContentCSV.class);
				if (!inventoryProductMsnMap.containsKey(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getPlantId())) {
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getPlantId(), content);
				}else {
					double tempMaxQuantity = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getPlantId()).getMaxQty();
					
					double tempMinQuantity = inventoryProductMsnMap
							.get(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getPlantId()).getMinQty();
					
					if(tempMaxQuantity > 0) {
						content.setMaxQty(content.getMaxQty() + tempMaxQuantity);
					}
					
					if(tempMinQuantity > 0 ) {
						content.setMinQty(content.getMinQty() + tempMinQuantity);
					}
					inventoryProductMsnMap.put(content.getProductMsn() + "-" + content.getWarehouseId() + "-" + content.getPlantId(), content);
				}
			}
		} catch (Exception e) {
			log.error("Error Parsing CSV. Please check.", e);
		}
		log.info("getProductInventoryImportContentCSV () ended  with filepath :" + filePath + "and map: " + inventoryProductMsnMap);
		return inventoryProductMsnMap;
	}
}
