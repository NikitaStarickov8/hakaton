package com.demo.hakaton.analysis.model.list;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Reception {

    private LocalDate date;
    private String id;
    private String diagnosis;
    private String status;
}
