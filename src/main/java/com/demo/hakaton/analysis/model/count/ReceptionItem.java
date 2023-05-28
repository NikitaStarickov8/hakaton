package com.demo.hakaton.analysis.model.count;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceptionItem {

    private String name;
    private Integer standard;
    private Integer poorQuality;
    private Integer suboptimal;
    private Integer unverifiable;
    private String id;
}
