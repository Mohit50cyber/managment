package com.moglix.wms.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.api.request.CreateCustomerDebitNoteRequest;
import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.CreateCustomerDebitNoteResponse;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.constants.CustomerDebitNoteStatus;
import com.moglix.wms.constants.CustomerDebitNoteType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.dto.CutomerDebitNoteReturnDetailDTO;
import com.moglix.wms.entities.CustomerDebitNote;
import com.moglix.wms.entities.CustomerDebitNoteItem;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.InboundStorage;
import com.moglix.wms.repository.CustomerDebitNoteRepository;
import com.moglix.wms.repository.InboundRepository;
import com.moglix.wms.service.IBatchService;
import com.moglix.wms.service.ICustomerDebitNoteService;
import com.moglix.wms.util.NumberUtil;

@Service
public class CustomerDebitNoteServiceImpl implements ICustomerDebitNoteService {
	
	@Autowired
	private CustomerDebitNoteRepository customerDebitNoteRepository;

	@Autowired
	private InboundRepository inboundRepo;
	
	@Autowired
	@Qualifier("batchService")
	private IBatchService batchService;
	
	private Logger logger = LogManager.getLogger(CustomerDebitNoteServiceImpl.class);
	
	@Override
	@Transactional
	public CreateCustomerDebitNoteResponse createBatch(@Valid CreateCustomerDebitNoteRequest request) {

		logger.info("Creating Customer Debit Note for: " + request.getCustomerDebitNoteNumber());
		CustomerDebitNote customerDebitNote = new CustomerDebitNote();
		
		Set<CustomerDebitNoteItem> customerDebitNoteItems = new HashSet<>();
		
		customerDebitNote.setStatus(CustomerDebitNoteStatus.CREATED);
		
		customerDebitNote.setDebitNoteNumber(request.getCustomerDebitNoteNumber());
		
		customerDebitNote.setWarehouseId(request.getWarehouseId());
		
		customerDebitNote.setWarehouseName(request.getWarehouseName());
		
		customerDebitNote.setType(request.getType());

		customerDebitNote.setTotalQuantity(request.getCustomerDebitNoteDetails().stream()
				.mapToDouble(CutomerDebitNoteReturnDetailDTO::getQuantity).sum());
		
		for (CutomerDebitNoteReturnDetailDTO returnDetail : request.getCustomerDebitNoteDetails()) {
			CustomerDebitNoteItem customerDebitNoteItem = new CustomerDebitNoteItem();
			
			customerDebitNoteItem.setCustomerDebitNote(customerDebitNote);
			
			customerDebitNoteItem.setEmsReturnId(returnDetail.getEmsReturnId());
			
			customerDebitNoteItem.setProductMsn(returnDetail.getProductMsn());
			
			customerDebitNoteItem.setProductName(returnDetail.getProductName());
			
			customerDebitNoteItem.setPurchasePrice(returnDetail.getPurchasePrice());
			
			customerDebitNoteItem.setQuantity(returnDetail.getQuantity());
			
			customerDebitNoteItem.setSupplierId(returnDetail.getSupplierId());
			
			customerDebitNoteItem.setSupplierName(returnDetail.getSupplierName());
			
			customerDebitNoteItem.setSupplierPoId(returnDetail.getSupplierPoId());
			
			customerDebitNoteItem.setSupplierPoItemId(returnDetail.getSupplierPoItemId());
			
			customerDebitNoteItem.setTax(returnDetail.getTax());
			
			customerDebitNoteItem.setUom(returnDetail.getUom());
			
			customerDebitNoteItems.add(customerDebitNoteItem);
			
			customerDebitNoteItem.setInboundId(returnDetail.getInboundId());

			logger.info("Deducting values for Inbound: " + returnDetail.getInboundId() + " Quantity: " + returnDetail.getQuantity());
			deductInboundQuantity(returnDetail.getInboundId(), returnDetail.getQuantity());
		}
		
		customerDebitNote.setDebitNoteItems(customerDebitNoteItems);
		
		customerDebitNoteRepository.save(customerDebitNote);
		return new CreateCustomerDebitNoteResponse("Debit Note Successfully created", true, HttpStatus.OK.value());
	}

	
	@Transactional
	private void deductInboundQuantity(Integer inboundId, Double deductQuantity) {
		Inbound inbound = inboundRepo.findById(inboundId).orElse(null);
		logger.info("Setting inventorisable Quantity: " + NumberUtil.round4(inbound.getInventorisableQuantity() - deductQuantity));
		inbound.setInventorisableQuantity(NumberUtil.round4(inbound.getInventorisableQuantity() - deductQuantity));
		
		logger.info("Setting Customer Debit done Quantity: " + NumberUtil.round4(inbound.getCustomerDeditDoneQuantity() + deductQuantity));
		inbound.setCustomerDeditDoneQuantity(
				NumberUtil.round4(inbound.getCustomerDeditDoneQuantity() + deductQuantity));
		logger.info("Deducting available quantities from inbound storage for inbound: " + inbound.getId());
		Set<InboundStorage> inboundStorages = inbound.getInboundStorages();
		logger.trace("Found " + inboundStorages.size() + " for inbound: " + inbound.getId());
		
		for(InboundStorage inboundStorage: inboundStorages) {
			logger.trace("Inside inboundStorages loop for deducting available quantities");
			logger.debug("Inbound Storage Available Quantity: " + inboundStorage.getAvailableQuantity());
			logger.debug("Inbound Storage Current Quantity: " + inboundStorage.getQuantity());
			logger.debug("Debit Note Deduct Quantity: " + deductQuantity);
			
			double deductibleQuantity = Math.min(inboundStorage.getAvailableQuantity(), deductQuantity);
			
			logger.debug(
					"Deducting " + deductibleQuantity + " quantity from inbound storage: " + inboundStorage.getId());
			
			inboundStorage.setAvailableQuantity(
					NumberUtil.round4(inboundStorage.getAvailableQuantity() - deductibleQuantity));
			
			inboundStorage.setQuantity(NumberUtil.round4(inboundStorage.getQuantity() - deductibleQuantity));
			
			logger.debug("New Inbound Storage Available Quantity: " + inboundStorage.getAvailableQuantity());
			
			logger.debug("New Inbound Storage Current Quantity: " + inboundStorage.getQuantity());

			deductQuantity = NumberUtil.round4(deductQuantity - deductibleQuantity);
			
			logger.debug("LeftOver Deduct Quantity: " + deductQuantity);
			
			if(deductQuantity == 0) {
				logger.trace("No quantity left to be deducted. Breaking the loop");
				break;
			}
		}
		inboundRepo.save(inbound);
	}
	
	@Transactional
	private void addInboundQuantity(Integer inboundId, Double addQuantity) {
		Inbound inbound = inboundRepo.findById(inboundId).orElse(null);
		inbound.setInventorisableQuantity(NumberUtil.round4(inbound.getInventorisableQuantity() + addQuantity));
		inbound.setCustomerDeditDoneQuantity(
				NumberUtil.round4(inbound.getCustomerDeditDoneQuantity() - addQuantity));
		logger.info("Adding available quantities from inbound storage for inbound: " + inbound.getId());
		Set<InboundStorage> inboundStorages = inbound.getInboundStorages();
		logger.trace("Found " + inboundStorages.size() + " for inbound: " + inbound.getId());
		
		double partialAddQuantity = NumberUtil.round4(addQuantity/inboundStorages.size());

		for (InboundStorage inboundStorage : inboundStorages) {
			logger.trace("Inside inboundStorages loop for deducting available quantities");
			logger.debug("Inbound Storage Available Quantity: " + inboundStorage.getAvailableQuantity());
			logger.debug("Inbound Storage Current Quantity: " + inboundStorage.getQuantity());
			logger.debug("Debit Note Add Quantity: " + addQuantity);


			logger.debug(
					"Adding " + partialAddQuantity + " quantity to inbound storage: " + inboundStorage.getId());

			inboundStorage.setAvailableQuantity(
					NumberUtil.round4(inboundStorage.getAvailableQuantity() + partialAddQuantity));

			inboundStorage.setQuantity(NumberUtil.round4(inboundStorage.getQuantity() + partialAddQuantity));

			logger.debug("New Inbound Storage Available Quantity: " + inboundStorage.getAvailableQuantity());

			logger.debug("New Inbound Storage Current Quantity: " + inboundStorage.getQuantity());

			logger.debug("LeftOver Deduct Quantity: " + addQuantity);

		}
		inboundRepo.save(inbound);
	}

	@Override
	@Transactional
	public BaseResponse cancelCustomerDebitNoteNumber(String customerDebitNoteNumber) {
		
		CustomerDebitNote customerDebitNote = customerDebitNoteRepository.findByDebitNoteNumberAndStatus(customerDebitNoteNumber, CustomerDebitNoteStatus.CREATED);
		
		if(customerDebitNote == null) {
			return new BaseResponse("Customer Debit Note: " + customerDebitNoteNumber + " not found or already cancelled.", false, HttpStatus.OK.value());
		}
		
		if(customerDebitNote.getType().equals(CustomerDebitNoteType.RATE_DIFFERENCE)) {
			for(CustomerDebitNoteItem customerDebitNoteItem : customerDebitNote.getDebitNoteItems()) {
				addInboundQuantity(customerDebitNoteItem.getInboundId(), customerDebitNoteItem.getQuantity());
			}
		}else if(customerDebitNote.getType().equals(CustomerDebitNoteType.QUANTITY)){
			Map<String, List<CustomerDebitNoteItem>> debitNoteItemsMap = customerDebitNote.getDebitNoteItems().stream().collect(Collectors.groupingBy(p -> getGroupingByMsn(p), Collectors.mapping((CustomerDebitNoteItem p) -> p, Collectors.toList())));
			
			for (Map.Entry<String, List<CustomerDebitNoteItem>> entry : debitNoteItemsMap.entrySet()) {
				List<ProductInput>productInputs = new ArrayList<>();
				CreateBatchRequest request = new CreateBatchRequest();
				request.setRefNo(customerDebitNoteNumber + "_" + entry.getKey());
				request.setBatchType(BatchType.CUSTOMER_DEBIT_NOTE_RETURN);
				request.setSupplierId(Integer.parseInt(entry.getKey().split("\\|")[0]));
				request.setSupplierName(entry.getKey().split("\\|")[1]);
				request.setWarehouseId(customerDebitNote.getWarehouseId());
				request.setWarehouseName(customerDebitNote.getWarehouseName());
				for(CustomerDebitNoteItem  debitNoteItem :entry.getValue()) {
					ProductInput input  = new ProductInput();
					input.setInboundType(InboundType.CUSTOMER_DEBIT_NOTE_RETURN);
					input.setInventorize(true);
					input.setInventrisableQuantity(debitNoteItem.getQuantity());
					input.setIsSerializedProduct(true);
					input.setProductMsn(debitNoteItem.getProductMsn());
					input.setProductName(debitNoteItem.getProductName());
					input.setPurchasePrice(debitNoteItem.getPurchasePrice());
					input.setQuantity(debitNoteItem.getQuantity());
					input.setSupplierPoId(debitNoteItem.getSupplierPoId());
					input.setSupplierPoItemId(debitNoteItem.getSupplierPoItemId());
					input.setTax(debitNoteItem.getTax());
					input.setUom(debitNoteItem.getUom());
					productInputs.add(input);
				}
				request.setProducts(productInputs);
				batchService.createBatch(request);
			}
		}
		
		customerDebitNote.setStatus(CustomerDebitNoteStatus.CANCELLED);
		
		customerDebitNoteRepository.save(customerDebitNote);
		
		return new BaseResponse("Customer Debit Note: " + customerDebitNoteNumber + " cancelled successfully", true,
				HttpStatus.OK.value());
	}
	
	private String getGroupingByMsn(CustomerDebitNoteItem debitNoteItem) {
		return debitNoteItem.getSupplierId() + "|" + debitNoteItem.getSupplierName();
	}

}
