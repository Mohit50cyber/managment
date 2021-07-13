package com.moglix.wms.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moglix.wms.api.response.AppVersionHistoryResponse;
import com.moglix.wms.entities.AppVersionHistory;

import io.swagger.annotations.Api;


@RestController
@RequestMapping("/app/version/")
public class OpenController {

	Logger logger = LogManager.getLogger(OpenController.class);
	
	@GetMapping("/getAppVersion")
	public AppVersionHistoryResponse getAppVersion() {
		logger.info("Request recieved to get App version");
		AppVersionHistory appVersion=new AppVersionHistory();
		appVersion.setActive(true);
		appVersion.setAndroidVersion("1.0");
		appVersion.setId(1);
		appVersion.setAndroidVersion("1.0");
		appVersion.setIosVersion("1.0");
		AppVersionHistoryResponse response = new AppVersionHistoryResponse();
		
			response.setStatus(true);
			response.setAppVersionHistory(appVersion);
			return response;
		
	}
}
