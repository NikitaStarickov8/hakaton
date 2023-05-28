package com.demo.hakaton.analysis.model.info;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Research {

    private Long id;
    private String name;
    private Boolean isNecessary;
    private String status;
}
