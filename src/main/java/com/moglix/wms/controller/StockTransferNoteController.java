package com.moglix.wms.controller;


import com.moglix.wms.api.request.CreateStockTransferNoteRequest;
import com.moglix.wms.api.request.StockTransferNoteSearchRequest;
import com.moglix.wms.api.request.StockTransferNoteUpdateRequest;
import com.moglix.wms.api.response.*;
import com.moglix.wms.dto.UpdateSTNResponse;
import com.moglix.wms.entities.SaleOrder;
import com.moglix.wms.exception.WMSException;
import com.moglix.wms.service.IStockTransferNoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/stn")
@Slf4j
public class StockTransferNoteController {


    @Autowired
    private IStockTransferNoteService iStockTransferNoteService;

    @GetMapping("/warehouse/{warehouse_id}/item_ref/{item_ref}")
    public ResponseEntity<STNItemRefStatusResponse> checkIfItemRefIsStnEligibility(@PathVariable("warehouse_id") int warehouseId,
                                                                                   @PathVariable("item_ref") String itemRef) {
        STNItemRefStatusResponse stnItemRefStatusResponse =
                new STNItemRefStatusResponse();
        SaleOrder saleOrder =null;
        boolean eligibility;
        try {
            saleOrder = iStockTransferNoteService.checkIfItemRefIsStnEligibility(
                    warehouseId, itemRef);
            eligibility = true;
        } catch (WMSException ex) {
            eligibility = false;
            stnItemRefStatusResponse.setReason(ex.getMessage());
        }
        stnItemRefStatusResponse.setItemRef(itemRef);
        stnItemRefStatusResponse.setStnEligibility(eligibility);
        if(saleOrder!=null) {
            stnItemRefStatusResponse.setProductMsn(saleOrder.getProduct().getProductMsn());
            stnItemRefStatusResponse.setQuantity(saleOrder.getAllocatedQuantity());
            stnItemRefStatusResponse.setHsnCode(iStockTransferNoteService.fetchHsnCode(itemRef));
        }
        HttpStatus status = eligibility ? HttpStatus.OK :
                HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(stnItemRefStatusResponse, status);
    }

    @PostMapping("")
    public ResponseEntity<StockTransferNoteResponse> createSTN(@Valid @RequestBody CreateStockTransferNoteRequest stnBody) {

        StockTransferNoteResponse stn = iStockTransferNoteService.createSTN(
                stnBody);
        HttpStatus status = stn.getStockTransferNoteId().equals("-1") ?
                HttpStatus.BAD_REQUEST :
                HttpStatus.CREATED;
        return new ResponseEntity<>(stn, status);
    }

    @GetMapping("/{stn_id}")
    public ResponseEntity<StockTransferNoteResponse> getStockTransferInfo(@PathVariable("stn_id") String externalId) {
        long stnId = iStockTransferNoteService.externalIdToInternalId(externalId);
        StockTransferNoteResponse stockTransferInfo =
                iStockTransferNoteService.getStockTransferInfo(
                        stnId);

        HttpStatus status = stockTransferInfo.getStockTransferNoteId().equals("-1") ?
                HttpStatus.NOT_FOUND :
                HttpStatus.OK;
        return new ResponseEntity<>(stockTransferInfo, status);
    }


    @GetMapping("/product_msn/{prouct_Msn}/warehouse/{warehouse_id}")
    public ResponseEntity<STNWarehouseEligibleResponse> checkWarehouseEligibleSTN(@PathVariable("prouct_Msn") String productMsn, @PathVariable("warehouse_id") Integer warehouseId) {
        log.info("Recieved request check eligible inbounds for product " + productMsn + " and WarehouseId " + warehouseId);
        STNWarehouseEligibleResponse response = iStockTransferNoteService.checkWarehouseEligibleSTN(productMsn, warehouseId);

        HttpStatus status = response.getStatus() ?
                HttpStatus.OK :
                HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(response, status);
    }

    @PutMapping("/{stn_id}")
    public ResponseEntity<UpdateSTNResponse> updateStn(@PathVariable("stn_id") String externalId, @RequestBody StockTransferNoteUpdateRequest stockTransferNoteUpdateRequest) {
        UpdateSTNResponse updateSTNResponse =null;
        HttpStatus status;
        long stnId = iStockTransferNoteService.externalIdToInternalId(externalId);
        try {
            updateSTNResponse=iStockTransferNoteService.updateStn(
                        stnId, stockTransferNoteUpdateRequest);
        } catch (Exception ex) {
            updateSTNResponse=UpdateSTNResponse.builder().success(false).message(ex.getMessage()).build();
        }
        status = updateSTNResponse.isSuccess() ?
                HttpStatus.OK :
                HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(updateSTNResponse, status);
    }

    @GetMapping("receiveItems/stn_id/{stn_id}")
    public ResponseEntity<BaseResponse> receiveItems(@PathVariable("stn_id") String stnid, Authentication auth) {
        long stnId = iStockTransferNoteService.externalIdToInternalId(stnid);

        BaseResponse response = iStockTransferNoteService.receiveWarehouseItems(stnId, auth.getName());
        HttpStatus status = response.getStatus() ?
                HttpStatus.OK :
                HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(response, status);
    }


    @PostMapping("/search")
    public ResponseEntity<StockTransferNoteSearchResponse> search(@Valid @RequestBody StockTransferNoteSearchRequest stockTransferNoteSearchRequest) {

        StockTransferNoteSearchResponse search = iStockTransferNoteService.searchPageable(stockTransferNoteSearchRequest);
        return new ResponseEntity<>(search, HttpStatus.OK);
    }
}
