package com.demo.hakaton.analysis.model.file;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
@NoArgsConstructor
public class AnalysisReportRequestModel implements Serializable {

    @ExcelRow
    private int rowIndex;

    @ExcelCellName("Пол пациента")
    private String sex;

    @ExcelCellName("Дата рождения пациента")
    private String dateOfBirth;

    @ExcelCellName("ID пациента")
    private int patientId;

    @ExcelCellName("Код МКБ-10")
    private String sicknessCode;

    @ExcelCellName("Диагноз")
    private String diagnosis;

    @ExcelCellName("Дата оказания услуги")
    private String dateOfVisit;

    @ExcelCellName("Должность")
    private String specialization;

    @ExcelCellName("Назначения")
    private String appointment;
}
