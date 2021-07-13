package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 17/5/19
 */
public class InventoriseInboundRequest extends BaseRequest {
    private static final long serialVersionUID = -4650187757876306739L;

    @NotNull
    private Integer inboundId;

    public Integer getInboundId() {
        return inboundId;
    }

    public void setInboundId(Integer inboundId) {
        this.inboundId = inboundId;
    }

    @Override
    public String toString() {
        return "InventoriseInboundRequest{" +
                "inboundId=" + inboundId +
                '}';
    }
}
