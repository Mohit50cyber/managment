package com.moglix.wms.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.moglix.wms.api.request.GetWarehouseByIdRequest;
import com.moglix.wms.api.request.SearchWarehouseRequest;
import com.moglix.wms.api.response.BlockedInventoryDataResponse;
import com.moglix.wms.api.response.ExpiredInventoryDataResponse;
import com.moglix.wms.api.response.GetWarehouseByIdResponse;
import com.moglix.wms.api.response.InventoryDataResponse;
import com.moglix.wms.api.response.SearchWarehouseResponse;
import com.moglix.wms.api.response.VmiReportDataResponse;
import com.moglix.wms.entities.Warehouse;

/**
 * @author pankaj on 30/4/19
 */
public interface IWarehouseService {

    Warehouse upsert(Warehouse warehouse);
    Warehouse getById(Integer id);
    List<Warehouse> getAll();
    
    List<Warehouse> getAllByISONumber(Integer isoNumber, Boolean isActive);

    SearchWarehouseResponse searchWarehouse(Integer countryId);
    GetWarehouseByIdResponse getWarehouseById(GetWarehouseByIdRequest request);
	void getInventoryData(Optional<Integer> warehouseId, HttpServletResponse response) throws IOException;
	InventoryDataResponse getInventoryDataAngular(Optional<Integer> warehouseId, HttpServletResponse response) throws IOException;
	void downloadInventory(HttpServletResponse response);
	BlockedInventoryDataResponse getBlockedInventoryDataAngular(Optional<Integer> warehouseId);
	VmiReportDataResponse getVmiReportDataAngular(HttpServletResponse response, Integer countryId);
	ExpiredInventoryDataResponse getExpiredInventoryReportData(Optional<Integer> warehouseId);
}
