package com.demo.hakaton.analysis.model.list;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReceptionListResponse {

    private List<ReceptionListItem> items;
    private Integer totalSize;
    private Integer limit;
    private Integer offset;
}
