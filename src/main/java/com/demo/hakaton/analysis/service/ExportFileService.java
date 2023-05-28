package com.demo.hakaton.analysis.service;

import com.demo.hakaton.analysis.entity.DiagnosisEntity;
import com.demo.hakaton.analysis.entity.DiagnosisExaminationLnkEntity;
import com.demo.hakaton.analysis.model.enums.DiagnosisStatusEnum;
import com.demo.hakaton.analysis.model.enums.ExaminationStatusEnum;
import com.demo.hakaton.analysis.model.enums.SpecializationEnum;
import com.demo.hakaton.analysis.repository.DiagnosisRepository;
import com.demo.hakaton.dictionary.entity.ExaminationDictionaryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExportFileService {

    private final DiagnosisRepository diagnosisRepository;

    public byte[] exportFile(String section, Date dateFrom, Date dateTo, List<String> specialization) {
        log.info("CHECK");
        var from = convertToLocalDate(dateFrom);
        var to = convertToLocalDate(dateTo);

        if (Objects.isNull(specialization)) {
            specialization = Arrays.stream(SpecializationEnum.values()).map(SpecializationEnum::getValue).collect(Collectors.toList());
        }

        var allByFilter = diagnosisRepository.getAllByDoctorAndDate(specialization, from, to);

        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчет");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        writeData(allByFilter, sheet, headerStyle);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            workbook.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private void writeData(List<DiagnosisEntity> allByFilter, Sheet sheet, CellStyle headerStyle) {
        String[] headers = new String[]{"Дата приема", "Id приема", "Заболевание", "Код заболевания",
                "Статус приема", "Специализация", "ФИО Врача", "Id врача", "Код исследования", "Наименование исследования", "Статус назначения"};


        // Записываем заголовки
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Записываем данные
        for (int i = 0; i < allByFilter.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            DiagnosisEntity diagnosis = allByFilter.get(i);

            Cell receptDate = dataRow.createCell(0);
            receptDate.setCellValue(diagnosis.getReceiptDate().toString());

            Cell idRecept = dataRow.createCell(1);
            idRecept.setCellValue(diagnosis.getId().toString());

            Cell sickness = dataRow.createCell(2);
            sickness.setCellValue(diagnosis.getSickness().getName());

            Cell sicknessCode = dataRow.createCell(3);
            sicknessCode.setCellValue(diagnosis.getSickness().getCode());

            Cell receptStatus = dataRow.createCell(4);
            receptStatus.setCellValue(DiagnosisStatusEnum.fromStatusCode(diagnosis.getStatus()).getDescription());

            Cell doctor = dataRow.createCell(5);
            doctor.setCellValue(SpecializationEnum.getSpecializationFromCode(diagnosis.getDoctor()).getRussianName());

            Cell fioDoctor = dataRow.createCell(6);
            fioDoctor.setCellValue("Тараканчиков Димасик Долгопупчикович");

            Cell idDoctor = dataRow.createCell(7);
            idDoctor.setCellValue("123");

            var diagnosisExaminationLnkEntities = diagnosis.getDiagnosisExaminationLnkEntities();
            var codes = diagnosisExaminationLnkEntities.stream().map(DiagnosisExaminationLnkEntity::getExamination).map(ExaminationDictionaryEntity::getCode).collect(Collectors.joining("\n"));
            var names = diagnosisExaminationLnkEntities.stream().map(DiagnosisExaminationLnkEntity::getExamination).map(ExaminationDictionaryEntity::getName).collect(Collectors.joining("\n"));
            var statuses = diagnosisExaminationLnkEntities.stream().map(DiagnosisExaminationLnkEntity::getStatus).map(ExaminationStatusEnum::getSpecializationFromCode).map(ExaminationStatusEnum::getValue).collect(Collectors.joining("\n"));

            Cell ageCell = dataRow.createCell(8);
            ageCell.setCellValue(codes);

            Cell nameCell = dataRow.createCell(9);
            nameCell.setCellValue(names);

            Cell dobCell = dataRow.createCell(10);
            dobCell.setCellValue(statuses);

        }
    }


    private LocalDate convertToLocalDate(Date dateToConvert) {
        if (Objects.nonNull(dateToConvert)) {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }
}
