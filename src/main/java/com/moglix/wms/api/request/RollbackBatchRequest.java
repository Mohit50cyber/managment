package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 29/4/19
 */
public class RollbackBatchRequest extends BaseRequest {
    private static final long serialVersionUID = -2773964869176193985L;


    @NotNull
    private Integer batchId;

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }


    @Override
    public String toString() {
        return "RollbackBatchRequest{" +
                "batchId=" + batchId +
                '}';
    }
}
