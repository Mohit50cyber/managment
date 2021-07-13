package com.moglix.wms.api.response;

import java.util.ArrayList;
import java.util.List;

import com.moglix.wms.dto.ProductInventoryDetailsDTO;

/**
 * @author pankaj on 15/5/19
 */
public class SearchProductInventoryResponse extends PaginationResponse {
    private static final long serialVersionUID = -5826305362969425110L;

    public SearchProductInventoryResponse(String message, boolean status, int code) {
        super(message, status, code);
    }

    private List<ProductInventoryDetailsDTO> inventoryList = new ArrayList<>();

    public List<ProductInventoryDetailsDTO> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<ProductInventoryDetailsDTO> inventoryList) {
        this.inventoryList = inventoryList;
    }
}
