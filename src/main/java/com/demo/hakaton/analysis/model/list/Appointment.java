package com.demo.hakaton.analysis.model.list;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Appointment {

    private List<String> research;
    private Integer count;
}
