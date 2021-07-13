package com.moglix.wms.service.impl;

import com.moglix.ems.entities.EnterpriseOrderItem;
import com.moglix.ems.repository.EnterpriseOrderItemRepository;
import com.moglix.wms.api.request.CreateStockTransferNoteRequest;
import com.moglix.wms.api.request.StockTransferNoteSearchRequest;
import com.moglix.wms.api.request.StockTransferNoteUpdateRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.api.response.STNWarehouseEligibleResponse;
import com.moglix.wms.api.response.StockTransferNoteResponse;
import com.moglix.wms.api.response.StockTransferNoteSearchResponse;
import com.moglix.wms.constants.*;
import com.moglix.wms.dto.*;
import com.moglix.wms.entities.*;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.repository.*;
import com.moglix.wms.service.*;
import com.moglix.wms.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class StockTransferNoteService implements IStockTransferNoteService {

    @Autowired
    private StockTransferNoteRepository stockTransferNoteRepository;

    @Autowired
    private ISaleOrderService saleOrderService;

    @Autowired
    private ISaleOrderAllocationService saleOrderAllocationService;

    @Autowired
    private StockTransferService stockTransferService;

    @Autowired
    private InboundRepository inboundRepo;

    @Autowired
    private ProductsRepository prodoctRepo;

    @Autowired
    private InboundStorageRepository inboundStorageRepo;

    @Autowired
    private StockTransferRepository stockTransferRepo;

    @Autowired
    private IWarehouseService iWarehouseService;

    @Autowired
    private InvoiceService invoiceService;


    @Autowired
    private IProductService iProductService;

    @Autowired
    private IProductInventoryService productInventoryService;

    @Autowired
    private ProductInventoryRepository productInventoryRepo;

    @Autowired
    private BatchRepository batchRepo;

    @Autowired
    private IInventoryService inventoryService;


    @Autowired
    private StockTransferInboundService stockTransferInboundService;
    @Autowired
    private EnterpriseOrderItemRepository enterpriseOrderItemRepository;

    @Autowired
    @Qualifier("inboundStorageServiceImpl")
    private IInboundStorageService inboundStorageServiceImpl;

    List<StockTransferNoteStatus> itemsReadyStatus = Arrays.asList(StockTransferNoteStatus.STN_CHALLANED, StockTransferNoteStatus.STN_INVOICED);


    private String externalIdPrefix = "STN";

    private Map<StockTransferNoteStatus, StockTransferNoteState> statusMapping = Stream.of(
            new AbstractMap.SimpleEntry<>(StockTransferNoteStatus.STN_GENERATED, StockTransferNoteState.STN_GENERATE_INVOICE),
            new AbstractMap.SimpleEntry<>(StockTransferNoteStatus.STN_INVOICED, StockTransferNoteState.STN_MARK_SHIP),
            new AbstractMap.SimpleEntry<>(StockTransferNoteStatus.STN_CHALLANED, StockTransferNoteState.STN_MARK_SHIP),
            new AbstractMap.SimpleEntry<>(StockTransferNoteStatus.STN_SHIPPED, StockTransferNoteState.STN_RECEIVE),
            new AbstractMap.SimpleEntry<>(StockTransferNoteStatus.STN_FAILED, StockTransferNoteState.STN_FAILED))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


    public long externalIdToInternalId(String externalId) {

        if (externalId != null && !externalId.trim().equals("") && externalId.startsWith(externalIdPrefix)) {
            try {
                return Long.parseLong(externalId.substring(externalIdPrefix.length()));
            } catch (NumberFormatException ex) {
                log.error("invalid stn-id {}", externalId);
                return -1L;
            }
        }
        return -1L;
    }

    private String internalIdToExternalId(long internalId) {
        if (internalId != -1L) {
            return externalIdPrefix + internalId;
        } else
            return "-1";
    }

    @Override
    public SaleOrder checkIfItemRefIsStnEligibility(int warehouseId,
                                                    String itemRef) {


        boolean eligibility = false;
        SaleOrder saleOrder = saleOrderService.getByItemRef(itemRef);
        if (saleOrder == null || saleOrder.getWarehouse()
                .getId() != warehouseId) {
            throw new WMSException("Item_ref is not present in specified " +
                    "warehouse");
        } else if(saleOrder.getWarehouse()
                .getId() == warehouseId && saleOrder.getStnAssoication()) {
            List<StockTransferNote> byItemRefAndWarehouseFrom = stockTransferNoteRepository.findByItemRefAndWarehouseFrom(itemRef, warehouseId);
            throw new WMSException("[" + internalIdToExternalId(byItemRefAndWarehouseFrom.get(0).getStockTransferNoteId()) +"]  already in progress for this sale order");
        }
        else if (saleOrder.getAllocatedQuantity()
                .equals(saleOrder.getOrderedQuantity()) && saleOrder.getPackedQuantity() == 0) {
            eligibility = true;
        } else {
            throw new WMSException("Item_ref is not eligible for Stock " +
                    "transfer  allocated Quantity [" + saleOrder.getAllocatedQuantity() + "] , Packed Quantity [" + saleOrder.getPackedQuantity() + "] ,Ordered Quantity ["
                    + saleOrder.getOrderedQuantity() + "]");
        }
        return saleOrder;
    }

    @Override
    public StockTransferNoteResponse createSTN(CreateStockTransferNoteRequest stockTransferNoteRequest) {
        StockTransferNoteResponse stockTransferNoteResponse = null;
        String message = null;
        if (stockTransferNoteRequest.getStockTransferNoteType() == StockTransferNoteType.WAREHOUSE_TO_WAREHOUSE) {
            stockTransferNoteResponse = createWarehouseSTN(stockTransferNoteRequest);
            return stockTransferNoteResponse;
        }
        if (!stockTransferNoteRepository.findByItemRefAndWarehouseFrom(
                stockTransferNoteRequest.getItemRef(),
                stockTransferNoteRequest.getWarehouseIdFrom()
        )
                .isEmpty()) {
            message = "Stock TransferRequest Note Already present";
        } else if (stockTransferNoteRequest.getStockTransferNoteType() ==
                StockTransferNoteType.CUSTOMER_ORDER) {
            return createStockTransferNodeForCustomerType(
                    stockTransferNoteRequest);
        } else {
            message = "Stock Transfer Note not supported";
        }
        return StockTransferNoteResponse.builder(internalIdToExternalId(-1L))
                .message(
                        message)
                .build();
    }

    @Override
    public StockTransferNoteResponse getStockTransferInfo(long stnId) {
        Optional<StockTransferNote> stn = stockTransferNoteRepository.findById(
                stnId);
        if (stn.isPresent()) {
            return convertToResponse(stn.get());
        }
        return StockTransferNoteResponse.builder(internalIdToExternalId(-1L))
                .message("stn Not found")
                .build();

    }

    @Override
    public UpdateSTNResponse updateStn(long stn_Id,
                                       StockTransferNoteUpdateRequest stockTransferNoteUpdateRequest) {

        UpdateSTNResponse response;
        try {
            switch (stockTransferNoteUpdateRequest.getStockTransferNoteState()) {
                case STN_GENERATE_INVOICE:
                    response = generateInvoice(stn_Id, stockTransferNoteUpdateRequest.getProductHsnInfoList());
                    break;
                case STN_MARK_SHIP:
                    response = markSTNShipped(stn_Id);
                    break;
                default:
                    response = UpdateSTNResponse.builder()
                            .success(false)
                            .message("invalid STN status change request request").build();

            }
        } catch (WMSException ex) {
            ex.printStackTrace();
            response= UpdateSTNResponse.builder()
                    .success(false)
                    .message(ex.getMessage()).build();
        }
        return response;
    }

    @Transactional
    public UpdateSTNResponse generateInvoice(long stnId, List<ProductHsnInfo> productHsnInfoList) {
        UpdateSTNResponse updateSTNResponse = new UpdateSTNResponse();

        Optional<StockTransferNote> stnOptional = stockTransferNoteRepository.findById(
                stnId);
        if (stnOptional.isPresent() && stnOptional.get().getStnStatus().equals(StockTransferNoteStatus.STN_GENERATED)) {
            StockTransferNote stockTransferNote = stnOptional.get();

            try {
                InvoiceRequestDTO invoiceRequestDTO =
                        generateInvoiceRequest(stockTransferNote,productHsnInfoList);
                if (stockTransferNote.getStockTransferType().equals(StockTransferType.INTER_STATE)) {
                    InvoiceEngineResponse response =
                            invoiceService.generateInvoice(invoiceRequestDTO);
                    if (response.isSuccess()) {
                        StockTransferNote stn = stnOptional.get();
                        stn.setStnStatus(StockTransferNoteStatus.STN_INVOICED);
                        stn.setInvoiceOrChallanUrl(response.getInvoice().get(0).getInvoiceURL());
                        stn.setInvoiceOrChallanNumber(response.getInvoice().get(0).getInvoiceNo());
                        stockTransferNoteRepository.save(stn);

                        updateSTNResponse.setInvoiceOrChallanUrl(stn.getInvoiceOrChallanUrl());
                        updateSTNResponse.setMessage(response.getMsg());
                        updateSTNResponse.setSuccess(response.isSuccess());
                        updateSTNResponse.setInvoiceOrChallanNumber(stn.getInvoiceOrChallanNumber());
                    } else {
                        updateSTNResponse.setMessage(response.getMsg());
                        updateSTNResponse.setSuccess(response.isSuccess());
                    }
                } else if (stockTransferNote.getStockTransferType().equals(StockTransferType.INTRA_STATE)) {
                    InvoiceEngineResponse response =
                            invoiceService.generateDeliveryChallan(invoiceRequestDTO);
                    if (response.isSuccess()) {
                        StockTransferNote stn = stnOptional.get();
                        stn.setStnStatus(StockTransferNoteStatus.STN_CHALLANED);
                        stn.setInvoiceOrChallanUrl(response.getInvoice().get(0).getDeliveryChallanUrl());
                        stn.setInvoiceOrChallanNumber(response.getInvoice().get(0).getDeliveryChallanNo());
                        stockTransferNoteRepository.save(stn);

                        updateSTNResponse.setInvoiceOrChallanUrl(stn.getInvoiceOrChallanUrl());
                        updateSTNResponse.setMessage(response.getMsg());
                        updateSTNResponse.setSuccess(response.isSuccess());
                        updateSTNResponse.setInvoiceOrChallanNumber(stn.getInvoiceOrChallanNumber());
                    } else {
                        updateSTNResponse.setMessage(response.getMsg());
                        updateSTNResponse.setSuccess(response.isSuccess());
                    }
                }
            } catch (WMSException ex) {
                updateSTNResponse.setSuccess(false);
                updateSTNResponse.setMessage(ex.getMessage());
            }
        } else if (stnOptional.isPresent() && itemsReadyStatus.contains(stnOptional.get().getStnStatus())) {
            updateSTNResponse.setInvoiceOrChallanUrl(stnOptional.get().getInvoiceOrChallanUrl());
            updateSTNResponse.setMessage("Already Invoiced or challaned");
            updateSTNResponse.setSuccess(true);
            updateSTNResponse.setInvoiceOrChallanNumber(stnOptional.get().getInvoiceOrChallanNumber());
        } else {
            updateSTNResponse.setSuccess(false);
            updateSTNResponse.setMessage("Stock transfer Note Not present " + internalIdToExternalId(stnId));
        }
        if(stnOptional.isPresent()) {
            updateSTNResponse.setNextStockTransferNoteState(statusMapping.get(stnOptional.get().getStnStatus()));
            updateSTNResponse.setStatus(stnOptional.get().getStnStatus());

        }
        return updateSTNResponse;
    }

     @Transactional
    public UpdateSTNResponse  markSTNShipped(long stnId) {
        UpdateSTNResponse response;
        Optional<StockTransferNote> stockTransferNoteOpt = stockTransferNoteRepository.findById(stnId);

        if (stockTransferNoteOpt.isPresent() && itemsReadyStatus.contains(stockTransferNoteOpt.get().getStnStatus())) {

            if (stockTransferNoteOpt.get().getStnType() == StockTransferNoteType.CUSTOMER_ORDER) {
                inboundStorageServiceImpl.deductInboundStorage(stockTransferNoteOpt.get());
            }
            stockTransferNoteOpt.get().setStnStatus(StockTransferNoteStatus.STN_SHIPPED);
            stockTransferNoteRepository.save(stockTransferNoteOpt.get());
            response = UpdateSTNResponse.builder().status(stockTransferNoteOpt.get().getStnStatus()).success(true)
                    .invoiceOrChallanNumber(stockTransferNoteOpt.get().getInvoiceOrChallanNumber())
                    .invoiceOrChallanUrl(stockTransferNoteOpt.get().getInvoiceOrChallanUrl())
                    .build();
        } else if (stockTransferNoteOpt.isPresent() && !itemsReadyStatus.contains(stockTransferNoteOpt.get().getStnStatus())) {
            response = UpdateSTNResponse.builder()
                    .status(stockTransferNoteOpt.get().getStnStatus())
                    .message(stockTransferNoteOpt.get().getStnStatus()==StockTransferNoteStatus.STN_SHIPPED?"cannot ship the item already shipped":"Cannot ship until stock Transfer is challaned or invoiced")
                    .success(false)
                    .build();
        } else {
            response = UpdateSTNResponse.builder()
                    .message(" Stock transferd note Note found")
                    .success(false)
                    .build();
        }
         response.setNextStockTransferNoteState(statusMapping.get(response.getStatus()));
        return response;
    }


    private InvoiceRequestDTO generateInvoiceRequest(StockTransferNote stockTransferNote,List<ProductHsnInfo> productHsnInfoList) {
        final Map<String, String> hsnMapping = productHsnInfoList!=null?productHsnInfoList.stream().collect((Collectors.toMap(ProductHsnInfo::getProductMsn, ProductHsnInfo::getHsnCode))):
                Collections.emptyMap();
        InvoiceRequestDTO invoiceRequestDTO = new InvoiceRequestDTO();
        if (stockTransferNote.getStockTransferType().equals(StockTransferType.INTRA_STATE)) {
            invoiceRequestDTO.setInvoiceType(150); // BUSINESS_DELIVERY_CHALLAN (150)
            invoiceRequestDTO.setInvoiceSource(20);
        }
        if (stockTransferNote.getStockTransferType().equals(StockTransferType.INTER_STATE)) {
            invoiceRequestDTO.setInvoiceType(40); //BUSINESS_INVOICE(40) in case of STN
            invoiceRequestDTO.setInvoiceSource(30);
        }
        Warehouse sourceWarehouse =
                iWarehouseService.getById(stockTransferNote.getWarehouseFrom());
        Warehouse targetWarehouse =
                iWarehouseService.getById(stockTransferNote.getWarehouseTo());

        invoiceRequestDTO.setCustomerGstin(targetWarehouse.getGstin());
        invoiceRequestDTO.setCustomerName(targetWarehouse.getName());

        invoiceRequestDTO.setCustomerOrderRef(internalIdToExternalId(stockTransferNote.getStockTransferNoteId()));
        invoiceRequestDTO.setConsigneeAddressShipping(getBuild(targetWarehouse));
        invoiceRequestDTO.setConsigneeAddressBilling(getBuild(targetWarehouse));
        invoiceRequestDTO.setConsignorAddress(getBuild(sourceWarehouse));
        invoiceRequestDTO.setConsignorAddressShipping(getBuild(sourceWarehouse));
        invoiceRequestDTO.setWarehouseId(Integer.toString(sourceWarehouse.getId()));
        invoiceRequestDTO.setSellerGSTIN(sourceWarehouse.getGstin());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:m:s");
        invoiceRequestDTO.setInvoiceDate(sdf.format(new Date()));
        if (stockTransferNote.getStockTransferType().equals(StockTransferType.INTRA_STATE))
            invoiceRequestDTO.setStockChallanId(internalIdToExternalId(stockTransferNote.getStockTransferNoteId()));


        invoiceRequestDTO.setInvoiceItems(mapToInvoiceItemsItem(stockTransferNote, hsnMapping));
        if (stockTransferNote.getStockTransferType().equals(StockTransferType.INTRA_STATE))
            updateFieldsForChallan(invoiceRequestDTO);
        if (stockTransferNote.getFreightCharge() != null)
            invoiceRequestDTO.setFreightCharge(Double.toString(stockTransferNote.getFreightCharge()));
        if (stockTransferNote.getMiscCharges() != null)
            invoiceRequestDTO.setMiscCharges(Double.toString(stockTransferNote.getMiscCharges()));
        invoiceRequestDTO.setAddedFields(AddedFields.builder()
                .grNo("")
                .transport("")
                .vehicleNo("")
                .station("")
                .customerPoc("")
                .vendorCode("").build());
        invoiceRequestDTO.setRemarks(stockTransferNote.getRemarks());
        invoiceRequestDTO.setSez(false);
        invoiceRequestDTO.setIgst(true);
        invoiceRequestDTO.setTaxComponentIncludedInSezInvoice(false);
        return invoiceRequestDTO;
    }

    private void updateFieldsForChallan( InvoiceRequestDTO invoiceRequestDTO ){
        List<InvoiceItemsItem> invoiceItems = invoiceRequestDTO.getInvoiceItems();
        invoiceItems.forEach(item-> {
            item.setNetAmount(Double.parseDouble(item.getQuantity())*Double.parseDouble(item.getPrice()));
            item.setCgstAmount((Double.parseDouble(item.getTaxRateApplicable())/(2*100))* item.getNetAmount());
            item.setSgstAmount(item.getCgstAmount());
        });
        double cgstAmount=invoiceItems.stream().mapToDouble(InvoiceItemsItem::getCgstAmount).reduce(0,Double::sum);
        double taxAbleAmt=invoiceItems.stream().mapToDouble(InvoiceItemsItem::getNetAmount).reduce(0,Double::sum);
        double cgstRate=invoiceItems.stream().mapToDouble(item ->Double.parseDouble(item.getTaxRateApplicable()))
                .reduce(0,Double::sum)/(invoiceItems.size()*2);
        invoiceRequestDTO.setCgstAmount(cgstAmount);
        invoiceRequestDTO.setSgstAmount(cgstAmount);
        invoiceRequestDTO.setCgstRate(cgstRate);
        invoiceRequestDTO.setSgstRate(cgstRate);
        invoiceRequestDTO.setTaxableValue(taxAbleAmt);
        invoiceRequestDTO.setInvoiceValue(taxAbleAmt+(cgstAmount*2));
    }

    public String fetchHsnCode(String itemRef) {
        Optional<EnterpriseOrderItem> enterpriseOrderItemOptional = enterpriseOrderItemRepository.findByItemRef(itemRef);
        if (enterpriseOrderItemOptional.isPresent()) {
            EnterpriseOrderItem enterpriseOrderItem = enterpriseOrderItemOptional.get();
            if (enterpriseOrderItem.getIsPushedToWms() == 1 && enterpriseOrderItem.getHsnCode() != null) {
                return enterpriseOrderItem.getHsnCode();
            }
        }
        return null;
    }

    private List<InvoiceItemsItem> mapToInvoiceItemsItem(StockTransferNote stockTransferNote, final Map<String, String> hsnMapping) {
        List<InvoiceItemsItem> invoiceItemsItemlist = new ArrayList<>();
        for (StockTransfer stockTransfer : stockTransferNote.getStockTransferList()) {
            if ((stockTransfer.getHsnCode() == null || stockTransfer.getHsnCode().trim().equals("")) && !hsnMapping.containsKey(stockTransfer.getProduct().getProductMsn()))
                throw new WMSException("hsn Not present for " + stockTransfer.getProduct().getProductMsn());
            String hsnCode = (stockTransfer.getHsnCode() == null || stockTransfer.getHsnCode().trim().equals("")) ? hsnMapping.get(stockTransfer.getProduct().getProductMsn())
                    : stockTransfer.getHsnCode();
            InvoiceItemsItem invoiceItemsItem = InvoiceItemsItem.builder()
                    .productName(
                            stockTransfer.getProduct()
                                    .getProductName())
                    .brandName(stockTransfer.getProduct().getProductBrand())
                    .productRef(
                            stockTransfer.getProduct()
                                    .getProductMsn())
                    .hsnCode(
                            hsnCode)
                    .quantity(
                            Double.toString(
                                    stockTransfer.getQuantity()))

                    .uom("OTH")
                    .addedFields(AddedFields.builder()
                            .grNo("")
                            .productUOQ("OTH")
                            .transport("")
                            .vehicleNo("")
                            .customerPoc("")
                            .vendorCode("")
                            .station("")
                            .build())
                    .transferPrice(
                            Double.toString(
                                    stockTransfer.getPurchasePrice()))
                    .taxRateApplicable(
                            Double.toString(
                                    stockTransfer.getTaxPercentage()))
                    .build();
             if (stockTransferNote.getStockTransferType().equals(StockTransferType.INTRA_STATE)) {
                invoiceItemsItem.setPrice(Double.toString(stockTransfer.getPurchasePrice()));
             }
            invoiceItemsItemlist.add(invoiceItemsItem);
        }
        return invoiceItemsItemlist;
    }

    private Address getBuild(Warehouse warehouse) {
        return Address.builder()
                .firstName(warehouse.getName())
                .lastName(warehouse.getName())
                .address1(warehouse.getAddress1())
                .address2(
                        warehouse.getAddress2())
                .city(warehouse.getCity()
                        .getName())
                .state(warehouse.getCity()
                        .getState()
                        .getName())
                .country(warehouse.getCity()
                        .getState()
                        .getCountry()
                        .getName())
                .pincode(warehouse.getPincode())
                .email(warehouse.getEmail())
                .phone(warehouse.getPhone())
                .bussinessName(
                        warehouse.getName())
                .stateCode(warehouse.getGstin()!=null?warehouse.getGstin().substring(0,2):null)
                .gstin(warehouse.getGstin())
                .build();
    }

    private StockTransferType getStockTransferType(int warehouseIdFrom,
                                                   int warehouseIdTo) {
        StockTransferType stockTransferType = StockTransferType.INTRA_STATE;
        Warehouse sourceWarehouse =
                iWarehouseService.getById(warehouseIdFrom);
        Warehouse targetWarehouse =
                iWarehouseService.getById(warehouseIdTo);

        if (!Objects.equals(sourceWarehouse.getCity()
                .getState()
                .getId(), targetWarehouse.getCity()
                .getState()
                .getId())) {
            stockTransferType = StockTransferType.INTER_STATE;
        }
        return stockTransferType;


    }


    private StockTransferNoteResponse createStockTransferNodeForCustomerType(CreateStockTransferNoteRequest stockTransferNoteRequest) {
        String message = null;
        long stnId = -1L;
        SaleOrder saleOrder = saleOrderService.getByItemRef(
                stockTransferNoteRequest.getItemRef());

        if (saleOrder == null || saleOrder.getWarehouse()
                .getId() != stockTransferNoteRequest.getWarehouseIdFrom()) {
            message = String.format("no valid sale order found " +
                            "itemRef [%s],  " +
                            "warehouse from [%s] or hsnCode is absent",
                    stockTransferNoteRequest.getItemRef(),
                    stockTransferNoteRequest.getWarehouseIdFrom()
            );
            log.error(message);
        } else if (stockTransferNoteRequest.getWarehouseStock() == null || stockTransferNoteRequest.getWarehouseStock().size() != 1 || stockTransferNoteRequest.getWarehouseStock().get(0).getHsnCode() == null) {
            message = "hsnCode is missing is payload ";
            log.error(message);
        } else {
            String hsnCode = (stockTransferNoteRequest.getWarehouseStock().get(0).getHsnCode()==null)?fetchHsnCode(saleOrder.getItemRef()):stockTransferNoteRequest.getWarehouseStock().get(0).getHsnCode().trim();
            saleOrder.setStnAssoication(true);
            List<SaleOrderAllocation> saleOrderAllocation =
                    saleOrderAllocationService.getSaleOrderAllocationBySaleOrderIdAndStatus(
                            saleOrder.getId(),
                            SaleOrderAllocationStatus.ALLOCATED
                    );
            Map<Integer, Double> allocatedQuantity = new HashMap<>();
            Map<Integer, InboundStorage> inboundStorageMap = new HashMap<>();
            List<Inbound> inboundList
                    =
                    saleOrderAllocation
                            .stream()
                            .map(soa -> {
                                allocatedQuantity.put(
                                        soa.getInboundStorage()
                                                .getInbound()
                                                .getId(),
                                        soa.getAllocatedQuantity()
                                );
                                inboundStorageMap.put(soa.getInboundStorage()
                                                .getInbound()
                                                .getId(),
                                        soa.getInboundStorage()
                                );
                                return soa.getInboundStorage()
                                        .getInbound();
                            })
                            .collect(
                                    Collectors.toList());


            StockTransferNote stn =
                    StockTransferNote.builder()
                            .stockTransferType(getStockTransferType(stockTransferNoteRequest.getWarehouseIdFrom(), stockTransferNoteRequest.getWarehouseIdTo()))
                            .warehouseFrom(
                                    stockTransferNoteRequest.getWarehouseIdFrom())
                            .warehouseTo(
                                    stockTransferNoteRequest.getWarehouseIdTo())
                            .stnType(
                                    stockTransferNoteRequest.getStockTransferNoteType())
                            .itemRef(
                                    stockTransferNoteRequest.getItemRef())
                            .pickupDate(
                                    stockTransferNoteRequest.getPickUpDate())
                            .stnStatus(
                                    StockTransferNoteStatus.STN_GENERATED)
                            .remarks(
                                    stockTransferNoteRequest.getRemarks())
                            .miscCharges(
                                    stockTransferNoteRequest.getMiscCharges())
                            .freightCharge(
                                    stockTransferNoteRequest.getFreightCharge())
                            .build();

            if (!inboundList.isEmpty()) {
                List<StockTransfer> collect = inboundList.stream()
                        .map(inbound -> StockTransfer.builder()
                                .stockTransferNote(
                                        stn)
                                .quantity(
                                        allocatedQuantity.get(
                                                inbound
                                                        .getId()))
                                .inboundId(
                                        inbound.getId())
                                .supplierId(
                                        inbound.getSupplierId())
                                .hsnSource(hsnCode == null ? HsnSource.MANUAL : HsnSource.EMS)
                                .hsnCode(hsnCode)
                                .supplierPoId(
                                        inbound.getSupplierPoId())
                                .supplierPoItemId(
                                        inbound.getSupplierPoItemId())
                                .product(
                                        inbound.getProduct()
                                )
                                .purchasePrice(
                                        inbound.getPurchasePrice())
                                .taxPercentage(
                                        inbound.getTax())
                                .storageLocation(
                                        inboundStorageMap.get(
                                                inbound.getId())
                                                .getStorageLocation())
                                .inboundStorage(inboundStorageMap.get(
                                        inbound.getId()))
                                .build())
                        .collect(
                                Collectors.toList());

                double taxPercentage = collect.get(0).getTaxPercentage();
                boolean allTaxSame = collect.stream().allMatch(item -> item.getTaxPercentage().equals(taxPercentage));

                stn.setStockTransferList(collect);

                Double totalQuantity = 0d;
                for (SaleOrderAllocation st : saleOrderAllocation)
                    totalQuantity += st.getAllocatedQuantity();
                stn.setQuantity(totalQuantity);
                stn.setStnStatus(allTaxSame ? StockTransferNoteStatus.STN_GENERATED : StockTransferNoteStatus.STN_FAILED);
                if (!allTaxSame) {
                    stn.setFailureReason("tax percentage is different");
                    message = stn.getFailureReason();
                }
                stockTransferNoteRepository.save(stn);
                stockTransferService.saveStockTransfers(collect);

                stnId = stn.getStockTransferNoteId();
            } else {
                message = String.format(
                        "No Allocation present for " +
                                "item_ref %s ",
                        stockTransferNoteRequest.getItemRef()
                );
                log.error(message);
            }
            saleOrderService.upsert(saleOrder);
        }
        return StockTransferNoteResponse.builder(internalIdToExternalId(stnId))
                .message(message)
                .build();
    }


    private StockTransferNoteResponse convertToResponse(StockTransferNote stockTransferNote) {

        List<StockTransferItem> items =
                stockTransferNote.getStockTransferList()
                        .stream()
                        .map(item ->
                                StockTransferItem.builder()
                                        .stockTransferItemId(item.getStockTransferId())
                                        .productMsn(
                                                item.getProduct()
                                                        .getProductMsn())
                                        .productName(item.getProduct().getProductName())
                                        .quantity(
                                                item.getQuantity())
                                        .inboundStorageId(
                                                item.getInboundStorage().getId()
                                        )
                                        .hsnCode(item.getHsnCode())
                                        .hsnRequired(item.getHsnCode() == null)
                                        .purchasePrice(
                                                item.getPurchasePrice())
                                        .zoneName(item.getStorageLocation()
                                                .getZone()
                                                .getName())
                                        .binName(
                                                item.getStorageLocation()
                                                        .getName())
                                        .supplierId(
                                                item.getSupplierId())
                                        .supplierPoId(
                                                item.getSupplierPoId())
                                        .taxPercentage(item.getTaxPercentage())
                                        .supplierPoItemId(
                                                item.getSupplierPoItemId())
                                        .build()


                        )
                        .collect(Collectors.toList());


        return StockTransferNoteResponse.builder(internalIdToExternalId(
                stockTransferNote.getStockTransferNoteId()))
                .stockTransferNoteType(stockTransferNote.getStnType())
                .stockTransferNoteStatus(stockTransferNote.getStnStatus())
                .quantity(stockTransferNote.getQuantity())
                .itemRef(stockTransferNote.getItemRef())
                .pickupDate(stockTransferNote.getPickupDate())
                .warehouseSource(stockTransferNote.getWarehouseFrom())
                .warehouseTarget(stockTransferNote.getWarehouseTo())
                .message(stockTransferNote.getFailureReason())
                .freightCharge(stockTransferNote.getFreightCharge())
                .miscCharges(stockTransferNote.getMiscCharges())
                .remarks(stockTransferNote.getRemarks())
                .stockTransferItem(items)
                .build();
    }


    public StockTransferNoteResponse createWarehouseSTN(CreateStockTransferNoteRequest request) {

        log.info("createWarehouseSTN service Started from :: " + request.getWarehouseIdFrom());

        if (request.getWarehouseStock().isEmpty()) {

            log.error("Empty List . Not possible to generate Warehouse to Warehouse STN ");
            return StockTransferNoteResponse.builder("-1")
                    .message("Empty List . Not possible to generate Warehouse to Warehouse STN ")
                    .build();
        }

        if (request.getItemRef() != null) {

            log.error("Item ref not empty . Not possible to generate Warehouse to Warehouse STN ");
            return StockTransferNoteResponse.builder("-1")
                    .message("Item ref not empty . Not possible to generate Warehouse to Warehouse STN ")
                    .build();
        }

        if (!(request.getStockTransferNoteType().toString().equalsIgnoreCase("WAREHOUSE_TO_WAREHOUSE"))) {
            log.error("Wrong STN Type selected . Not possible to generate Warehouse to Warehouse STN ");
            return StockTransferNoteResponse.builder("-1")
                    .message("Wrong STN Type selected . Not possible to generate Warehouse to Warehouse STN ")
                    .build();
        }

        //MSN,SupplierPO,SupplierPOitemid
        List<StockWarehouseTransfer> stockItems = request.getWarehouseStock();
        Product product = null;
        Double totalQuantityCheck = 0d;
        HashMap<String, Double> msnQuantitycheckduplicate = new HashMap<String, Double>();
        log.info("Cheking for Quantity Available");
        //for (String State : map.keySet())
        for (StockWarehouseTransfer stockItem : stockItems) {
            log.info("Got Entry Key :: " + stockItem.getProductMSN());
            //System.out.println("Key = " + entry.getKey() +", Value = " + entry.getValue());
            product = prodoctRepo.getUniqueByProductMsn(stockItem.getProductMSN());
            if (product == null) {
                log.info("No Such MSN Found :: " + stockItem.getProductMSN());
                return StockTransferNoteResponse.builder("-1")
                        .message("No Such MSN found " + stockItem.getProductMSN())
                        .build();
            }

            List<Inbound> inbounds = inboundRepo.getValidInbound(product.getId(), request.getWarehouseIdFrom());
            if (inbounds.isEmpty()) {
                log.info("No Inbound found :: " + stockItem.getProductMSN());
                return StockTransferNoteResponse.builder("-1")
                        .message("No Inbound found for " + stockItem.getProductMSN())
                        .build();
            }

            if (msnQuantitycheckduplicate.containsKey(stockItem.getProductMSN())) {
                log.info("Duplicate Key Found :: " + stockItem.getProductMSN());
                return StockTransferNoteResponse.builder("-1")
                        .message("Duplicate MSN " + stockItem.getProductMSN())
                        .build();
            } else {
                msnQuantitycheckduplicate.put(stockItem.getProductMSN(), 1d);
            }

            Double totalInboundQuantitycheck = 0d;
            for (Inbound inbound : inbounds) {
                Double availableQuantity = inboundStorageRepo.availableQuantityByInbound(inbound.getId());
                totalInboundQuantitycheck += availableQuantity;
            }
            if (stockItem.getQuantity() > totalInboundQuantitycheck) {
                log.info("Less Quantity :: " + stockItem.getProductMSN());
                return StockTransferNoteResponse.builder("-1")
                        .message("Quantity Not Available for product :: " + stockItem.getProductMSN())
                        .build();

            }
            totalQuantityCheck += stockItem.getQuantity();
        }

        log.info("Creating STN Started ");
        StockTransferNote stn = new StockTransferNote();
        stn.setWarehouseFrom(request.getWarehouseIdFrom());
        stn.setWarehouseTo(request.getWarehouseIdTo());
        stn.setStnType(request.getStockTransferNoteType());
        stn.setPickupDate(request.getPickUpDate());
        stn.setStnStatus(StockTransferNoteStatus.STN_GENERATED);
        stn.setRemarks(request.getRemarks());
        stn.setMiscCharges(request.getMiscCharges());
        stn.setFreightCharge(request.getFreightCharge());
        stn.setQuantity(totalQuantityCheck);
        stn.setStockTransferType(StockTransferType.INTRA_STATE);
        stockTransferNoteRepository.save(stn);
        log.info("Stn Created with type " + stn.getStnType());
        log.info("STN Items Started for " + stn.getStockTransferNoteId());
        HashMap<String, Double> msnQuantity = new HashMap<String, Double>();
        List<StockTransfer> stocktransferlist = new ArrayList<>();
        for (StockWarehouseTransfer stockItem : stockItems) {
            log.info("STN Items Started for " + stockItem.getProductMSN());
            Double totalAllocatedQuantity = 0d;
            product = prodoctRepo.getUniqueByProductMsn(stockItem.getProductMSN());
            List<Inbound> inbounds = inboundRepo.getValidInbound(product.getId(), request.getWarehouseIdFrom());
            for (Inbound inbound : inbounds) {
                if (totalAllocatedQuantity >= stockItem.getQuantity()) {
                    break;
                }
                List<InboundStorage> inboundStorages = inboundStorageRepo.findByInbound(inbound.getId());
                for (InboundStorage inboundStorage : inboundStorages) {
                    Double currentQuantity = 0d;
                    if (inboundStorage.getAvailableQuantity() <= stockItem.getQuantity() - totalAllocatedQuantity) {
                        currentQuantity = inboundStorage.getAvailableQuantity();
                        totalAllocatedQuantity = totalAllocatedQuantity + currentQuantity;
                        if (msnQuantity.containsKey(stockItem.getProductMSN())) {
                            Double prevQuant = msnQuantity.get(stockItem.getProductMSN());
                            msnQuantity.put(stockItem.getProductMSN(), prevQuant + currentQuantity);
                        } else {
                            msnQuantity.put(stockItem.getProductMSN(), currentQuantity);
                        }

                    } else {
                        currentQuantity = stockItem.getQuantity() - totalAllocatedQuantity;
                        totalAllocatedQuantity = totalAllocatedQuantity + currentQuantity;
                        if (msnQuantity.containsKey(stockItem.getProductMSN())) {
                            Double prevQuant = msnQuantity.get(stockItem.getProductMSN());
                            msnQuantity.put(stockItem.getProductMSN(), prevQuant + currentQuantity);
                        } else {
                            msnQuantity.put(stockItem.getProductMSN(), currentQuantity);
                        }
                    }
                    inboundStorage.setAvailableQuantity(inboundStorage.getAvailableQuantity() - currentQuantity);
                    inboundStorage.setQuantity(inboundStorage.getAvailableQuantity() + inboundStorage.getAllocatedQuantity());

                    log.info("STN Items creation for inbound id :: " + inbound.getId());
                    StockTransfer stockTransferItems = new StockTransfer();
                    stockTransferItems.setStockTransferNote(stn);
                    stockTransferItems.setInboundId(inbound.getId());
                    stockTransferItems.setPurchasePrice(inbound.getPurchasePrice());
                    stockTransferItems.setSupplierId(inbound.getSupplierId());
                    stockTransferItems.setSupplierPoId(inbound.getSupplierPoId());
                    stockTransferItems.setSupplierPoItemId(inbound.getSupplierPoItemId());
                    stockTransferItems.setTaxPercentage(inbound.getTax());
                    stockTransferItems.setProduct(product);
                    stockTransferItems.setStorageLocation(inboundStorage.getStorageLocation());
                    stockTransferItems.setQuantity(currentQuantity);
                    stockTransferItems.setInboundStorage(inboundStorage);
                    stocktransferlist.add(stockTransferItems);
                    log.info("STN Items creayed for inbound id :: " + inbound.getId());
                    if (totalAllocatedQuantity >= stockItem.getQuantity()) {
                        break;
                    }

                }//Inbound Storages End
            }//Inbounds End
        }//While iterator

        Set<Map.Entry<String, Double>> msnQuantitylist = msnQuantity.entrySet();

        for (Map.Entry<String, Double> deductionList : msnQuantitylist) {
            log.info(deductionList.getKey() + " : " + deductionList.getValue());
            Product productnew = prodoctRepo.getUniqueByProductMsn(deductionList.getKey());
            productnew.setAvailableQuantity(Math.max(productnew.getAvailableQuantity() - deductionList.getValue(), 0));
            productnew.setCurrentQuantity(productnew.getAvailableQuantity() + productnew.getAllocatedQuantity());
            ProductInventory productinventory = productInventoryRepo.findByWarehouseIdAndProductId(request.getWarehouseIdFrom(), productnew.getId());
            productinventory.setAvailableQuantity(Math.max(productinventory.getAvailableQuantity() - deductionList.getValue(), 0));
            productinventory.setCurrentQuantity(productinventory.getAvailableQuantity() + productinventory.getAllocatedQuantity());
        }
        stockTransferNoteRepository.save(stn);
        stockTransferRepo.saveAll(stocktransferlist);

        log.info("STN WAREHOUSE SERVICE ENDED");
        return StockTransferNoteResponse.builder(internalIdToExternalId(stn.getStockTransferNoteId()))
                .message("STN Formed ")
                .build();
    }

    @Override
    public STNWarehouseEligibleResponse checkWarehouseEligibleSTN(String productMsn, Integer warehouseId) {

        STNWarehouseEligibleResponse response = new STNWarehouseEligibleResponse();

        Product product = prodoctRepo.getUniqueByProductMsn(productMsn);
        if (product == null) {
            response.setMessage("No such product Found");
            response.setStatus(false);
            return response;
        }

        Double eligibleItems = stockTransferRepo.getEligibleItemsByWarehouseandProduct(warehouseId, product.getId());

        if (eligibleItems == 0) {
            response.setMessage("No Elible Products found for MSN :: " + productMsn);
            response.setStatus(false);
            return response;
        }

        response.setEligibleItemsQuantity(eligibleItems);
        response.setMessage("Eligible Items Found :: " + eligibleItems);
        response.setStatus(true);
        return response;
    }

    @Override
    public BaseResponse receiveWarehouseItems(Long stn_id, String user) {

        log.trace("Received material for STN id " + stn_id);

        Optional<StockTransferNote> stncheck = stockTransferNoteRepository.findById(stn_id);
        if (!stncheck.isPresent()) {
            return new BaseResponse("NO SUCH STN FOUND ", false, 200);
        } else {
            StockTransferNote stn = stncheck.get();
            if (stn.getStnStatus().equals(StockTransferNoteStatus.STN_RECEIVED)) {
                return new BaseResponse("Items Already Received", false, 200);
            }
            if (stn.getStnStatus().equals(StockTransferNoteStatus.STN_GENERATED)) {
                return new BaseResponse("STN Have Not Been Invoiced", false, 200);
            }
            Batch batch = new Batch();
            batch.setRefNo(internalIdToExternalId(stn_id));
            batch.setBatchType(BatchType.INBOUND);
            batch.setWarehouseId(stn.getWarehouseTo());
            batch.setWarehouseName(iWarehouseService.getById(stn.getWarehouseTo()).getName());
            batch.setPurchaseDate(stn.getCreatedDate());
            batch.setInboundedBy(user);
            batch.setSupplierId(0);
            batch.setSupplierName(iWarehouseService.getById(stn.getWarehouseFrom()).getName() + "_STN");
            List<Inbound> inbounds = new ArrayList<>();
            List<StockTransferInbound> stockTransferInbounds = new ArrayList<>();
            List<StockTransfer> stocktransfers = stockTransferRepo.findAllByStockTransferNoteId(stn.getStockTransferNoteId());
            if (stocktransfers.isEmpty()) {
                return new BaseResponse("NO Items Found In Stock Transfer Items", false, 200);
            } else {

                for (StockTransfer stocktransfer : stocktransfers) {
                    Inbound inboundOrignal = inboundRepo.findById(stocktransfer.getInboundId()).get();
                    Inbound inbound = new Inbound();
                    inbound.setCreditDoneQuantity(0d);
                    inbound.setInventorisableQuantity(0d);
                    inbound.setInventorize(true);
                    inbound.setProductName(inboundOrignal.getProductName());
                    inbound.setPurchaseDate(stn.getCreatedDate());
                    inbound.setPurchasePrice(inboundOrignal.getPurchasePrice());
                    inbound.setQuantity(stocktransfer.getQuantity());
                    inbound.setStatus(InboundStatusType.STARTED);
                    inbound.setSupplierId(inboundOrignal.getSupplierId());
                    inbound.setSupplierName(inboundOrignal.getSupplierName());
                    inbound.setSupplierPoId(inboundOrignal.getSupplierPoId());
                    inbound.setSupplierPoItemId(inboundOrignal.getSupplierPoItemId());
                    inbound.setTax(inboundOrignal.getTax());
                    inbound.setType(inboundOrignal.getType());
                    inbound.setUom(inboundOrignal.getUom());
                    inbound.setWarehouseId(stn.getWarehouseTo());
                    inbound.setWarehouseName(iWarehouseService.getById(stn.getWarehouseTo()).getName());
                    inbound.setBatch(batch);
                    inbound.setProduct(inboundOrignal.getProduct());
                    inbounds.add(inbound);
                    stockTransferInbounds.add(StockTransferInbound.builder()
                            .stockTransferNote(stn)
                            .inbound(inbound)
                            .stockTransfer(stocktransfer).build());
                }
            }

            batchRepo.save(batch);
            inboundRepo.saveAll(inbounds);
            stn.setStnStatus(StockTransferNoteStatus.STN_RECEIVED);

            stockTransferNoteRepository.save(stn);
            stockTransferInboundService.saveAll(stockTransferInbounds);
            return new BaseResponse("Items Received", true, 200);
        }

	}

    private void deductInboundStorage(StockTransferNote stockTransferNote) {
        Warehouse sourceWarehouse =
                iWarehouseService.getById(stockTransferNote.getWarehouseFrom());
        List<StockTransfer> stItems = stockTransferNote.getStockTransferList();
        List<InboundStorage> inboundStorages = new ArrayList<>();
        Map<String, Double> prevProductQuantityMap = new HashMap<>();
        Map<String, Double> currentProductQuantityMap = new HashMap<>();
        for (StockTransfer stItem : stItems) {
            InboundStorage storage = stItem.getInboundStorage();
            storage.setQuantity(storage.getQuantity() - stItem.getQuantity());
            storage.setAllocatedQuantity(storage.getAllocatedQuantity() - stItem.getQuantity());
            inboundStorages.add(storage);
            String productMsn = stItem.getProduct().getProductMsn();
            //TODO: NEED to check
            prevProductQuantityMap.compute(productMsn,
                    (k, v) -> v == null ? productInventoryService
                            .getByWarehouseIdAndProductId(sourceWarehouse.getId(), stItem.getProduct().getId())
                            .getCurrentQuantity() : v + productInventoryService
                            .getByWarehouseIdAndProductId(sourceWarehouse.getId(), stItem.getProduct().getId())
                            .getCurrentQuantity());
            currentProductQuantityMap.compute(productMsn,
                    (k, v) -> v == null ? stItem.getQuantity() : NumberUtil.round4(v + stItem.getQuantity()));
        }
        for (Map.Entry<String, Double> entry : prevProductQuantityMap.entrySet()) {
            inventoryService.saveInventoryHistory(sourceWarehouse, entry.getKey(), InventoryTransactionType.STN_PACKED,
                    InventoryMovementType.INVENTORY_OUT, String.valueOf(stockTransferNote.getStockTransferNoteId()), entry.getValue(),
                    (NumberUtil.round4(entry.getValue() - currentProductQuantityMap.get(entry.getKey()))));
        }


        inboundStorageServiceImpl.saveAll(inboundStorages);
        Map<Integer, Double> stnQuantity = stItems.stream().collect(Collectors.groupingBy(stockTransfer -> stockTransfer.getProduct().getId(), Collectors.summingDouble(StockTransfer::getQuantity)));
        stnQuantity.forEach((key, value) -> inventoryService.deductAllocatedInventory(sourceWarehouse.getId(),
                key, value));
        //TODO: entries in SaleOrderAllocation and SaleOrderAllocationHistory
        SaleOrder so = saleOrderService.getByItemRef(stockTransferNote.getItemRef());
        saleOrderAllocationService.updateStatus(so.getId(), SaleOrderAllocationStatus.STN_TRANSFERRED);
        so.setStatus(SaleOrderStatus.ORDER_TRANSFERED);
        so.setAllocatedQuantity(0d);
        saleOrderService.upsert(so);

    }

    @Override
    public StockTransferNoteSearchResponse searchPageable(StockTransferNoteSearchRequest request) {
        int pageSize = request.getPageSize();
        int pageNumber = request.getPageNumber();
        List<StockTransferNote> stockTransferNotes = new ArrayList<>();
        Page<StockTransferNote> pages=null;
        int totalPage=1;
        int currentPage=0;
        long totalRecords=0;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "createdDate");
        if (request.getStnId() != null) {
            StockTransferNote stockTransferNote = stockTransferNoteRepository.findAllByStockTransferNoteId(externalIdToInternalId(request.getStnId()));
            if (stockTransferNote != null) {
                if (request.getWarehouseFrom() != null && request.getWarehouseTo() != null
                        && request.getWarehouseTo() == stockTransferNote.getWarehouseTo()
                        && request.getWarehouseFrom() == stockTransferNote.getWarehouseFrom()
                ) {
                    stockTransferNotes.add(stockTransferNote);
                    totalRecords=1;
                    totalPage=1;
                } else if (request.getWarehouseFrom() != null
                        && request.getWarehouseFrom() == stockTransferNote.getWarehouseFrom()
                ) {
                    stockTransferNotes.add(stockTransferNote);
                    totalRecords=1;
                    totalPage=1;
                } else if (request.getWarehouseTo() != null
                        && request.getWarehouseTo() == stockTransferNote.getWarehouseTo()
                ) {
                    stockTransferNotes.add(stockTransferNote);
                    totalRecords=1;
                    totalPage=1;
                } else {
                    totalRecords = 0;
                    totalPage = 0;
                }
            } else {
                totalRecords=0;
                totalPage=0;
            }
        } else {
            if (request.getWarehouseFrom() != null && request.getWarehouseTo() != null) {
                pages = stockTransferNoteRepository.findAllByWarehouseFromAndWarehouseTo(request.getWarehouseFrom(), request.getWarehouseTo(), pageRequest);

            } else if (request.getWarehouseFrom() != null && request.getStnId() == null) {
                pages = stockTransferNoteRepository.findAllByWarehouseFrom(request.getWarehouseFrom(), pageRequest);
            } else if (request.getWarehouseTo() != null && request.getStnId() == null) {
                pages = stockTransferNoteRepository.findAllByWarehouseTo(request.getWarehouseTo(), pageRequest);
            }
            if(pages!=null) {
                stockTransferNotes = pages.getContent();
                totalPage = pages.getTotalPages();
                currentPage = pages.getNumber();
                totalRecords=pages.getTotalElements();
            }
        }
        final List<StockTransferNoteResponse> collect = stockTransferNotes.stream().map(st ->
                StockTransferNoteResponse.builder(internalIdToExternalId(st.getStockTransferNoteId()))
                        .nextStockTransferNoteState(statusMapping.get(st.getStnStatus()))
                        .warehouseSource(st.getWarehouseFrom())
                        .invoiceOrChallanUrl(st.getInvoiceOrChallanUrl())
                        .invoiceOrChallanNumber(st.getInvoiceOrChallanNumber())
                        .warehouseTarget(st.getWarehouseTo())
                        .quantity(st.getQuantity())
                        .stockTransferNoteType(st.getStnType())
                        .stockTransferNoteStatus(st.getStnStatus())
                        .build()

        ).collect(Collectors.toList());
        return StockTransferNoteSearchResponse.builder().totalPages(totalPage).items(collect)
                .totalRecords(totalRecords)
                .count(collect.size()).currentPage(currentPage).build();
    }


}
