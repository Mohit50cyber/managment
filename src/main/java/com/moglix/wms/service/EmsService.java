package com.moglix.wms.service;

import com.moglix.wms.api.request.EMSPackableQuantityRequest;
import com.moglix.wms.api.response.BaseResponse;
import com.moglix.wms.config.EmsEngineConfig;
import com.moglix.wms.exception.WMSException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class EmsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmsEngineConfig emsEngineConfig;


    public Boolean updateEmsPortal(EMSPackableQuantityRequest emsRequest) {

        String updatePackageQuantityUrl =
                emsEngineConfig.getBaseUrl()+emsEngineConfig.getPackableQuantityUpdatePath();
        return updateEmsResponse(updatePackageQuantityUrl,emsRequest);
    }

    private Boolean updateEmsResponse(String updatePackageQuantityUrl,EMSPackableQuantityRequest emsRequest) {
        try {

            ResponseEntity<BaseResponse> responseEntity =
                    restTemplate.postForEntity(
                            updatePackageQuantityUrl, emsRequest,
                            BaseResponse.class
                    );
            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null && responseEntity.getBody().getStatus()) {
                log.info("invoice success");
                return true;
            }

        } catch (WMSException wex) {
            throw wex;
        } catch (Exception ex) {
            throw new WMSException("Ems engine not avaliable try it later");
        }
        log.error("sending error as ems is down");
        throw new WMSException("Ems engine not avaliable try it later");
    }

}
