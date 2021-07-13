package com.moglix.wms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.ems.repository.EMSInventoryRepository;
import com.moglix.ems.repository.EMSReturnRepository;
import com.moglix.wms.dto.EMSInventory;
import com.moglix.wms.dto.EMSReturnInventory;
import com.moglix.wms.service.IEmsInventoryService;

@Service
public class EMSInventoryServiceImpl implements IEmsInventoryService{

	@Autowired
	EMSInventoryRepository emsInventoryRepo;
	
	@Autowired
	EMSReturnRepository emsReturnRepo;
	
	@Override
	@Transactional
	public List<EMSInventory> getProductMrnInventory() {
		return emsInventoryRepo.getProductMrnInventory();
	}
	
	@Override
	@Transactional
	public List<EMSInventory> getProductMrnInventoryCSV() {
		return emsInventoryRepo.getProductMrnInventoryCSV();
	}

	@Override
	@Transactional
	public List<EMSReturnInventory> getProductReturnInventory() {
		return emsReturnRepo.getProductReturnInventory();
	}
	
}
