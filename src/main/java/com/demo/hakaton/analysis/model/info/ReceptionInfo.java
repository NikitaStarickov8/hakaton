package com.demo.hakaton.analysis.model.info;

import com.demo.hakaton.analysis.model.list.Doctor;
import com.demo.hakaton.analysis.model.list.Reception;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class ReceptionInfo {

    private Reception reception;
    private Doctor doctor;
    private List<Research> research;
}
