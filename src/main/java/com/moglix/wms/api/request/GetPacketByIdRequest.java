package com.moglix.wms.api.request;

import javax.validation.constraints.NotNull;

/**
 * @author pankaj on 16/5/19
 */
public class GetPacketByIdRequest extends BaseRequest {
    private static final long serialVersionUID = 931211689021571343L;

    @NotNull
    private Integer packetId;

    public GetPacketByIdRequest(@NotNull Integer packetId) {
        this.packetId = packetId;
    }

    public Integer getPacketId() {
        return packetId;
    }

    public void setPacketId(Integer packetId) {
        this.packetId = packetId;
    }

    @Override
    public String toString() {
        return "GetPacketByIdRequest{" +
                "packetId=" + packetId +
                '}';
    }
}
