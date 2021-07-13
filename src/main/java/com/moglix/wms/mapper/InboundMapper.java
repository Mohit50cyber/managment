package com.moglix.wms.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.moglix.wms.api.request.ProductInput;
import com.moglix.wms.constants.InboundStatusType;
import com.moglix.wms.constants.InboundType;
import com.moglix.wms.dto.BatchCSVRecordContent;
import com.moglix.wms.dto.EMSInventoryDTO;
import com.moglix.wms.dto.ProductDTO;
import com.moglix.wms.dto.ProductPacketResponseDTO;
import com.moglix.wms.dto.ProductPacketResponseDTO.SupplierInfo;
import com.moglix.wms.dto.ReturnDetail;
import com.moglix.wms.dto.SupplierCreditNoteDetailDTO;
import com.moglix.wms.entities.Inbound;
import com.moglix.wms.entities.PacketItem;
import com.moglix.wms.entities.SaleOrderAllocation;
import com.moglix.wms.util.DateUtil;
import com.moglix.wms.util.NumberUtil;

public class InboundMapper {
	private InboundMapper() {

	}

	public static Inbound createEntityFromInput(ProductInput input) {
		Inbound inbound = new Inbound();
		if (input.getSupplierPoItemId() != null) {
			inbound.setSupplierPoItemId(input.getSupplierPoItemId());
		}
		if (input.getSupplierPoId() != null) {
			inbound.setSupplierPoId(input.getSupplierPoId());
		}
		if (input.getUom() != null) {
			inbound.setUom(input.getUom());
		}
		if (input.getProductName() != null) {
			inbound.setProductName(input.getProductName());
		}
		if (input.getQuantity() != null) {
			inbound.setQuantity(NumberUtil.round4(input.getQuantity()));
		}
		
		if(input.getInventrisableQuantity() != null) {
			inbound.setInventorisableQuantity(NumberUtil.round4(input.getInventrisableQuantity()));
		}
		
		if (input.getMfgDate() != null) {
			inbound.setMfgDate(input.getMfgDate());
		}
		if (input.getExpDate() != null) {
			inbound.setExpDate(input.getExpDate());
		}
		if (input.getPurchasePrice() != null) {
			inbound.setPurchasePrice(NumberUtil.round4(input.getPurchasePrice()));
		}
		if (input.getTax() != null) {
			inbound.setTax(NumberUtil.round4(input.getTax()));
		}
		
		if(input.getInventorize() != null) {
			inbound.setInventorize(input.getInventorize());
		}
		
		if(input.getInboundType() != null) {
			inbound.setType(input.getInboundType());
		}
		
		if(input.getIsJunkInventory() != null) {
			inbound.setIsJunkInventory(input.getIsJunkInventory());
		}
		
		return inbound;
	}
	
	public static Inbound createInboundFromCSVRecord(BatchCSVRecordContent input) {
		Inbound inbound = new Inbound();
		if (input.getSupplierPoItemId() != null) {
			inbound.setSupplierPoItemId(input.getSupplierPoItemId());
		}
		if(input.getPurchaseDate() != null) {
			inbound.setPurchaseDate(input.getPurchaseDate());
		}
		
		if(input.getSupplierName() != null) {
			inbound.setSupplierName(input.getSupplierName());
		}
		
		if(input.getPurchaseDate() != null) {
			inbound.setPurchaseDate(input.getPurchaseDate());
		}
		
		if(input.getSupplierId() != null) {
			inbound.setSupplierId(input.getSupplierId());
		}
		
		if(input.getWarehouseId() != null) {
			inbound.setWarehouseId(input.getWarehouseId());
		}
		
		if(input.getWarehouseName() != null) {
			inbound.setWarehouseName(inbound.getWarehouseName());
		}
		
		if (input.getSupplierPoId() != null) {
			inbound.setSupplierPoId(input.getSupplierPoId());
		}
		if (input.getUom() != null) {
			inbound.setUom(input.getUom());
		}
		if (input.getProductName() != null) {
			inbound.setProductName(input.getProductName());
		}
		if (input.getQuantity() != null) {
			inbound.setQuantity(NumberUtil.round4(input.getQuantity()));
		}
		
		if (input.getMfgDate() != null) {
			inbound.setMfgDate(input.getMfgDate());
		}
		if (input.getExpiryDate() != null) {
			inbound.setExpDate(input.getExpiryDate());
		}
		if (input.getPurchasePrice() != null) {
			inbound.setPurchasePrice(NumberUtil.round4(input.getPurchasePrice()));
		}
		if (input.getTaxRate() != null) {
			inbound.setTax(NumberUtil.round4(input.getTaxRate()));
		}
		inbound.setInventorize(true);
		inbound.setStatus(InboundStatusType.STARTED);
		inbound.setType(InboundType.NEW);
		return inbound;
	}
	
	public static ProductPacketResponseDTO createProductPacketFromEntity(Inbound inbound, List<SaleOrderAllocation>saleOrderAllocations, Integer emsPacketId) {
		ProductPacketResponseDTO input = new ProductPacketResponseDTO();
		
		ProductPacketResponseDTO.SupplierInfo supplierInfo = new SupplierInfo();
		
		
		if (inbound.getSupplierPoItemId() != null) {
			supplierInfo.setSupplierPoItemId(inbound.getSupplierPoItemId());
		}
		if (inbound.getSupplierPoId() != null) {
			supplierInfo.setSupplierPoId(inbound.getSupplierPoId());
		}
		if (inbound.getUom() != null) {
			input.setUom(inbound.getUom());
		}
		if (inbound.getProductName() != null) {
			input.setProductName(inbound.getProductName());
		}
		if(inbound.getProduct().getProductMsn() != null) {
			input.setProductMsn(inbound.getProduct().getProductMsn());
		}
		if (inbound.getQuantity() != null) {
			double packedQuantity = 0.0d;
			for (SaleOrderAllocation saleOrderAllocation : saleOrderAllocations) {
				if (inbound.getInboundStorages().stream().map(e -> e.getId()).collect(Collectors.toList())
						.contains(saleOrderAllocation.getInboundStorage().getId())) {
					for(PacketItem item : saleOrderAllocation.getPacketItems()) {
						if(item.getPacket().getEmsPacketId().equals(emsPacketId)) {
							packedQuantity = packedQuantity + item.getQuantity();
						}
					}
				}
			}
			supplierInfo.setQuantity(packedQuantity);
		}
		if (inbound.getMfgDate() != null) {
			input.setMfgDate(inbound.getMfgDate());
		}
		if (inbound.getExpDate() != null) {
			input.setExpDate(inbound.getExpDate());
		}
		if (inbound.getPurchasePrice() != null) {
			supplierInfo.setPurchasePrice(NumberUtil.round4(inbound.getPurchasePrice()));
		}
		if (inbound.getTax() != null) {
			supplierInfo.setTax(NumberUtil.round4(inbound.getTax()));
		}
		
		if(inbound.getInventorize() != null) {
			input.setInventorize(inbound.getInventorize());
		}
		
		if(inbound.getSupplierName() != null) {
			supplierInfo.setSupplierName(inbound.getSupplierName());
		}
		if(inbound.getSupplierId() != null) {
			supplierInfo.setSupplierId(inbound.getSupplierId());
		}
		supplierInfo.setMrnId(inbound.getBatch().getRefNo());
		
		input.getSupplierDetails().add(supplierInfo);
		return input;
	}
	
	public static ProductInput createInputFromEntity(Inbound inbound) {
		ProductInput input = new ProductInput();
		if (inbound.getSupplierPoItemId() != null) {
			input.setSupplierPoItemId(inbound.getSupplierPoItemId());
		}
		if (inbound.getSupplierPoId() != null) {
			input.setSupplierPoId(inbound.getSupplierPoId());
		}
		if (inbound.getUom() != null) {
			input.setUom(inbound.getUom());
		}
		if (inbound.getProductName() != null) {
			input.setProductName(inbound.getProductName());
		}
		
		if(inbound.getProduct().getProductMsn() != null) {
			input.setProductMsn(inbound.getProduct().getProductMsn());
		}
		if (inbound.getQuantity() != null) {
			input.setQuantity(NumberUtil.round4(inbound.getQuantity()));
		}
		if (inbound.getMfgDate() != null) {
			input.setMfgDate(inbound.getMfgDate());
		}
		if (inbound.getExpDate() != null) {
			inbound.setExpDate(inbound.getExpDate());
		}
		if (inbound.getPurchasePrice() != null) {
			input.setPurchasePrice(NumberUtil.round4(inbound.getPurchasePrice()));
		}
		if (inbound.getTax() != null) {
			input.setTax(NumberUtil.round4(inbound.getTax()));
		}
		
		if(inbound.getInventorize() != null) {
			input.setInventorize(inbound.getInventorize());
		}
		
		if(inbound.getSupplierName() != null) {
			input.setSupplierName(inbound.getSupplierName());
		}
		if(inbound.getSupplierId() != null) {
			input.setSupplierId(inbound.getSupplierId());
		}
		
		if(inbound.getType() != null) {
			input.setInboundType(inbound.getType());
		}
		return input;
	}
	
	public static ProductDTO createProductDTOFromInbound(Inbound inbound) {
		ProductDTO productDto = new ProductDTO(inbound.getProduct());
		
		productDto.setSupplierPoId(inbound.getSupplierPoId());
		productDto.setSupplierPoItemId(inbound.getSupplierPoItemId());
		productDto.setSupplierName(inbound.getSupplierName());
		productDto.setPurchasePrice(inbound.getPurchasePrice());
		
		return productDto;		
	}
	
	public static SupplierCreditNoteDetailDTO createSupplierCreditNoteDetailDTOFromInbound(Inbound inbound) {
		SupplierCreditNoteDetailDTO supplierCreditNoteDetailDTO = new SupplierCreditNoteDetailDTO();
		
		supplierCreditNoteDetailDTO.setPurchasePrice(inbound.getPurchasePrice());
		
		supplierCreditNoteDetailDTO.setSupplierPoItemId(inbound.getSupplierPoItemId());
		
		supplierCreditNoteDetailDTO.setCreditDoneQuantity(inbound.getCreditDoneQuantity());
		
		supplierCreditNoteDetailDTO.setInboundId(inbound.getId());
		
		supplierCreditNoteDetailDTO.setProductName(inbound.getProductName());
		
		supplierCreditNoteDetailDTO.setProductMsn(inbound.getProduct().getProductMsn());
		
		supplierCreditNoteDetailDTO.setReturnedQuantity(inbound.getQuantity());
		
		supplierCreditNoteDetailDTO.setTax(inbound.getTax());
		
		supplierCreditNoteDetailDTO.setInventrisableQuantiy(inbound.getInventorisableQuantity());
		
		return supplierCreditNoteDetailDTO;		
	}

	public static Inbound createInboundFromEmsInventory(EMSInventoryDTO input) {
		Inbound inbound = new Inbound();
		if (input.getPoItemId() != null) {
			inbound.setSupplierPoItemId(input.getPoItemId());
		}
		if (input.getPoId() != null) {
			inbound.setSupplierPoId(input.getPoId());
		}
		if (input.getProductUnit() != null) {
			inbound.setUom(input.getProductUnit());
		}
		if (input.getProductName() != null) {
			inbound.setProductName(input.getProductName());
		}
		if (input.getArrivedQuantity() != null) {
			inbound.setQuantity(NumberUtil.round4(input.getArrivedQuantity()));
			inbound.setInventorisableQuantity(NumberUtil.round4(input.getArrivedQuantity()));
		}
		
		if(input.getMrnDate() != null) {
			inbound.setPurchaseDate(input.getMrnDate());
		}
		
		if(input.getSupplierId() != null) {
			inbound.setSupplierId(input.getSupplierId());
		}
		
		if(input.getSupplierName() != null) {
			inbound.setSupplierName(input.getSupplierName());
		}

		inbound.setMfgDate(DateUtil.getCurrentDateTime());

		if (input.getTransferPrice() != null) {
			inbound.setPurchasePrice(NumberUtil.round4(input.getTransferPrice()));
		}
		if (input.getTaxRate() != null) {
			inbound.setTax(NumberUtil.round4(input.getTaxRate()));
		}
		
		if(input.getWarehouseId() != null) {
			inbound.setWarehouseId(input.getWarehouseId());
		}
		
		inbound.setStatus(InboundStatusType.STARTED);

		inbound.setInventorize(true);

		inbound.setType(InboundType.NEW);

		return inbound;
	}
	
	public static ReturnDetail createReturnDetailFromInbound(Inbound inbound) {
		ReturnDetail returnDetail = new ReturnDetail();
		returnDetail.setDebitDoneQuantity(inbound.getCustomerDeditDoneQuantity());
		
		returnDetail.setInboundId(inbound.getId());
		
		returnDetail.setProductMsn(inbound.getProduct().getProductMsn());
		
		returnDetail.setProductName(inbound.getProduct().getProductName());
		
		returnDetail.setPurchasePrice(inbound.getPurchasePrice());
		
		returnDetail.setSupplierId(inbound.getSupplierId());
		
		returnDetail.setSupplierName(inbound.getSupplierName());
		
		returnDetail.setSupplierPoId(inbound.getSupplierPoId());
		
		returnDetail.setSupplierPoItemId(inbound.getSupplierPoItemId());
		
		returnDetail.setTax(inbound.getTax());
		
		returnDetail.setUom(inbound.getUom());
		
		returnDetail.setWarehouseId(inbound.getWarehouseId());
		
		returnDetail.setWarehouseName(inbound.getWarehouseName());
		
		return returnDetail;
	}
}
