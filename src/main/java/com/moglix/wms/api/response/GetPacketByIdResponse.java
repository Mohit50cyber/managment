package com.moglix.wms.api.response;

import com.moglix.wms.dto.PacketDto;

/**
 * @author pankaj on 16/5/19
 */
public class GetPacketByIdResponse extends BaseResponse {
    private static final long serialVersionUID = 3924230165438328730L;

    private PacketDto packet;

    public PacketDto getPacket() {
        return packet;
    }

    public void setPacket(PacketDto packet) {
        this.packet = packet;
    }
}
