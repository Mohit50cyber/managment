package com.moglix.wms.util;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.moglix.wms.dto.InventoryDataDTO;

public class CSVUtil {
	
	private static Logger log = LogManager.getLogger(CSVUtil.class);

	private CSVUtil() {
		
	}
	
	public static void writeInventoryDataToCsv(PrintWriter writer,List<InventoryDataDTO> inventoryDataRecords) {
		try (
			     CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
	                      .withHeader("warehouseName", "productId", "productMsn", "name", "quantity", "uom", "zoneId", "BinId", "status"));
	    ) {
	      for (InventoryDataDTO inventoryDataRecord : inventoryDataRecords) {
	        List<String> record = Arrays.asList(
	        		String.valueOf(inventoryDataRecord.getWarehouseName()),
	        		inventoryDataRecord.getProductMsn(),
	        		inventoryDataRecord.getName(),
	        		String.valueOf(inventoryDataRecord.getQuantity()),
	        		inventoryDataRecord.getUom(),
	        		inventoryDataRecord.getZone() == null ?"NA":inventoryDataRecord.getZone(),
	        		inventoryDataRecord.getBin() == null ?"NA":inventoryDataRecord.getBin(),
	        		inventoryDataRecord.getStatus()==null ?"INVENTORIZED":inventoryDataRecord.getStatus()
	        		
	          );
	        
	        csvPrinter.printRecord(record);
	      }
	      csvPrinter.flush();
		    } catch (Exception e) {
		    	log.info("Error in creating CSV File", e);
		    }
	}
}
