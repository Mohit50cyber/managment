package com.moglix.wms.service;

import com.moglix.wms.api.request.GenerateBarcodeRequest;
import com.moglix.wms.api.response.GenerateBarcodeResponse;

public interface IBarcodeService {
	public GenerateBarcodeResponse generateBarcode(GenerateBarcodeRequest request);
}
