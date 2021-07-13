package com.moglix.wms.api.request;

import com.moglix.wms.constants.BatchType;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 24/5/19
 */
public class DeleteBatchRequest extends BaseRequest {
    private static final long serialVersionUID = -2773964869176193985L;

    @NotNull
    private String refNo;

    @NotNull
    private BatchType batchType;
    
    public BatchType getBatchType() {
        return batchType;
    }

    public void setBatchType(BatchType batchType) {
        this.batchType = batchType;
    }

    public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	@Override
    public String toString() {
        return "DeleteBatchRequest{" +
                "refNo=" + refNo +
                ", batchType=" + batchType +
                '}';
    }
}
