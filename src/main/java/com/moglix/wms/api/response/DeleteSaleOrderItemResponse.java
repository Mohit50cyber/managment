package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 6/5/19
 */
public class DeleteSaleOrderItemResponse extends BaseResponse {
    private static final long serialVersionUID = -96264398270209557L;

    private List<DeleteItemResponse> elements = new ArrayList<>();

    public static class DeleteItemResponse {
        private Integer emsOrderItemId;
        private boolean deleted;
        private String message;

        public DeleteItemResponse() {
        }

        public DeleteItemResponse(Integer emsOrderItemId, boolean deleted, String message) {
            this.emsOrderItemId = emsOrderItemId;
            this.deleted = deleted;
            this.message = message;
        }

        public Integer getEmsOrderItemId() {
            return emsOrderItemId;
        }

        public void setEmsOrderItemId(Integer emsOrderItemId) {
            this.emsOrderItemId = emsOrderItemId;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public List<DeleteItemResponse> getElements() {
        return elements;
    }

    public void setElements(List<DeleteItemResponse> elements) {
        this.elements = elements;
    }
}
