package com.moglix.wms.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.moglix.wms.api.request.CreateBatchRequest;
import com.moglix.wms.api.request.DeleteBatchRequest;
import com.moglix.wms.api.request.RollbackBatchRequest;
import com.moglix.wms.api.request.SupplierCNCancelRequest;
import com.moglix.wms.api.response.CancelSupplierCNResponse;
import com.moglix.wms.api.response.CreateBatchResponse;
import com.moglix.wms.api.response.DeleteBatchResponse;
import com.moglix.wms.api.response.FileUploadResponse;
import com.moglix.wms.api.response.RollbackBatchResponse;
import com.moglix.wms.constants.BatchType;
import com.moglix.wms.entities.Batch;

public interface IBatchService {
	CreateBatchResponse createBatch(CreateBatchRequest data);
	RollbackBatchResponse rollbackBatch(RollbackBatchRequest request);
	DeleteBatchResponse deleteBatch(DeleteBatchRequest request);
	List<Batch> getReturnedBatches(Integer emsReturnId);
	Batch findByRefNoAndBatchType(String refNo, BatchType type);
	Optional<Batch> findTop1ByEmsReturnIdAndSupplierIdOrderByCreatedDesc(Integer emsReturnId, Integer supplierId);
	DeleteBatchResponse checkIfBatchIsDeletable(String refNo, BatchType batchType);
	List<Batch> findByEmsReturnId(Integer emsReturnId);
	List<Batch> findByEmsReturnIdAndBatchType(Integer emsReturnId, BatchType customerReturn);
	FileUploadResponse fileUpload(MultipartFile file) throws IOException;
	CancelSupplierCNResponse cancelsupplierCN(SupplierCNCancelRequest request);
}
