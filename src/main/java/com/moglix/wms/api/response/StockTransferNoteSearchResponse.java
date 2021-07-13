package com.moglix.wms.api.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class StockTransferNoteSearchResponse {
    Integer currentPage;
    Integer count;
    Integer totalPages;
    Long totalRecords;
    List<StockTransferNoteResponse> items;
}
