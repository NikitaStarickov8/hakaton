package com.demo.hakaton.analysis.service;

import com.demo.hakaton.analysis.entity.DiagnosisEntity;
import com.demo.hakaton.analysis.entity.DiagnosisExaminationLnkEntity;
import com.demo.hakaton.analysis.model.enums.DiagnosisStatusEnum;
import com.demo.hakaton.analysis.model.enums.SpecializationEnum;
import com.demo.hakaton.analysis.model.list.*;
import com.demo.hakaton.analysis.repository.DiagnosisExaminationLnkRepository;
import com.demo.hakaton.analysis.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceptionListService {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisExaminationLnkRepository diagnosisExaminationLnkRepository;


    //Формирование таблицы
    public ReceptionListResponse getReceptionsList(Integer limit, Integer offset,
                                                   Date dateFrom, Date dateTo,
                                                   List<String> specialization, Integer clientId) {
        var from = convertToLocalDate(dateFrom);
        var to = convertToLocalDate(dateTo);

        if (Objects.isNull(specialization)) {
            specialization = Arrays.stream(SpecializationEnum.values()).map(SpecializationEnum::getValue).collect(Collectors.toList());
        }

        var rs = new ReceptionListResponse();
        List<ReceptionListItem> items = new ArrayList<>();
        var pageRq = createPageRq(limit, offset, specialization, clientId, from, to);
        var allByFilters = pageRq.stream().toList();
        for (DiagnosisEntity diagnosisEntity : allByFilters) {
            var responseItem = new ReceptionListItem();
            var reception = new Reception();
            var doctor = new Doctor();
            var appointment = new Appointment();

            reception.setDate(diagnosisEntity.getReceiptDate());
            reception.setId(String.valueOf(diagnosisEntity.getId()));
            reception.setDiagnosis(diagnosisEntity.getSickness().getCode() + " " + diagnosisEntity.getSickness().getName());
            reception.setStatus(DiagnosisStatusEnum.fromStatusCode(diagnosisEntity.getStatus()).getValue());

            doctor.setSpecialization(SpecializationEnum.getSpecializationFromCode(diagnosisEntity.getDoctor()).getRussianName());
            doctor.setName("Тараканчиков Димасик Бобинчиков");
            doctor.setId(String.valueOf(123L));

            var allLinksByDiagnosis = diagnosisExaminationLnkRepository.getAllByDiagnosis(diagnosisEntity);

            appointment.setCount(allLinksByDiagnosis.size());
            appointment.setResearch(allLinksByDiagnosis.stream().map(DiagnosisExaminationLnkEntity::getStatus).collect(Collectors.toList()));

            if (Objects.isNull(appointment.getResearch())) {
                appointment.setResearch(new ArrayList<>());
            }

            responseItem.setReception(reception);
            responseItem.setDoctor(doctor);
            responseItem.setAppointment(appointment);
            items.add(responseItem);
        }

        rs.setItems(items);
        rs.setLimit(limit);
        rs.setOffset(offset);
        rs.setTotalSize((int) pageRq.getTotalElements());
        return rs;
    }


    //Запрос в бд для пагинации
    private Page<DiagnosisEntity> createPageRq(Integer limit, Integer offset, List<String> specialization, Integer patientId, LocalDate from, LocalDate to) {
        Pageable requestPage = PageRequest.of(offset / limit, limit);
        return diagnosisRepository.findAllByFilters(specialization, patientId, from, to, requestPage);
    }

    //Конверт фильтра даты до LocalDate для работы с БД
    private LocalDate convertToLocalDate(Date dateToConvert) {
        if (Objects.nonNull(dateToConvert)) {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }
}
