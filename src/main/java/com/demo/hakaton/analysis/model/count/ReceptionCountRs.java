package com.demo.hakaton.analysis.model.count;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReceptionCountRs {

    private List<ReceptionItem> items;
}
