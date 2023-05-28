package com.demo.hakaton.analysis.model.list;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceptionListItem {

    private Reception reception;
    private Doctor doctor;
    private Appointment appointment;
}
