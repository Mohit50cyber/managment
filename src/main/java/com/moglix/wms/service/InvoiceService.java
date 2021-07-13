package com.moglix.wms.service;

import com.google.gson.Gson;
import com.moglix.wms.config.InvoiceEngineConfig;
import com.moglix.wms.dto.InvoiceEngineResponse;
import com.moglix.wms.dto.InvoiceRequestDTO;
import com.moglix.wms.exception.WMSException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class InvoiceService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InvoiceEngineConfig invoiceEngineConfig;


    public InvoiceEngineResponse generateDeliveryChallan(InvoiceRequestDTO invoiceRequestDTO) {

        String challanUrl =
                invoiceEngineConfig.getBaseUrl() + invoiceEngineConfig.getChallanPath();
        return getInvoiceEngineResponse(invoiceRequestDTO,challanUrl);
    }

    public InvoiceEngineResponse generateInvoice(InvoiceRequestDTO invoiceRequestDTO) {
        String invoiceUrl =
                invoiceEngineConfig.getBaseUrl() + invoiceEngineConfig.getInvoicePath();
        return getInvoiceEngineResponse(invoiceRequestDTO,invoiceUrl);
    }

    private InvoiceEngineResponse getInvoiceEngineResponse(InvoiceRequestDTO invoiceRequestDTO,String invoiceEngineUrl) {
        try {


            Gson g = new Gson();
            log.error("YOGI  request+ "+ g.toJson(invoiceRequestDTO));
            ResponseEntity<InvoiceEngineResponse> responseEntity =
                    restTemplate.postForEntity(
                            invoiceEngineUrl, invoiceRequestDTO,
                            InvoiceEngineResponse.class
                    );
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null && responseEntity.getBody().isSuccess())
                return responseEntity.getBody();
            else if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null && !responseEntity.getBody().isSuccess()) {
                throw new WMSException(responseEntity.getBody().getMsg());
            }
        } catch (WMSException wex) {
            throw wex;
        } catch (Exception ex) {
            throw new WMSException("Invoice engine not avaliable try it later");
        }

        throw new WMSException("Invoice engine not avaliable try it later");
    }

}
