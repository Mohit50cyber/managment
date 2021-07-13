package com.moglix.wms.api.response;

import com.moglix.wms.dto.PacketDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankaj on 14/5/19
 */
public class SearchPacketForPickupResponse extends PaginationResponse {
    private static final long serialVersionUID = 27051726312953652L;

    public SearchPacketForPickupResponse(String message, boolean status, int code) {
        super(message, status, code);
    }

    private List<PacketDto> packets = new ArrayList<>();

    public List<PacketDto> getPackets() {
        return packets;
    }

    public void setPackets(List<PacketDto> packets) {
        this.packets = packets;
    }
}
