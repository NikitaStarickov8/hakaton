package com.demo.hakaton.analysis.service;

import com.demo.hakaton.analysis.entity.DiagnosisExaminationLnkEntity;
import com.demo.hakaton.analysis.model.enums.DiagnosisStatusEnum;
import com.demo.hakaton.analysis.model.enums.SpecializationEnum;
import com.demo.hakaton.analysis.model.info.ReceptionInfo;
import com.demo.hakaton.analysis.model.info.Research;
import com.demo.hakaton.analysis.model.list.Doctor;
import com.demo.hakaton.analysis.model.list.Reception;
import com.demo.hakaton.analysis.repository.DiagnosisExaminationLnkRepository;
import com.demo.hakaton.analysis.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionInfoService {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisExaminationLnkRepository diagnosisExaminationLnkRepository;


    //Получение детальной инфы по приему
    public ReceptionInfo getInfo(Long id) {
        var diagnosisEntity = diagnosisRepository.getById(id);
        var response = new ReceptionInfo();
        var reception = new Reception();
        var doctor = new Doctor();
        List<Research> researchList = new ArrayList<>();

        reception.setDate(diagnosisEntity.getReceiptDate());
        reception.setId(String.valueOf(diagnosisEntity.getId()));
        reception.setDiagnosis(diagnosisEntity.getSickness().getCode() + " " + diagnosisEntity.getSickness().getName());
        reception.setStatus(DiagnosisStatusEnum.fromStatusCode(diagnosisEntity.getStatus()).getValue());

        doctor.setSpecialization(SpecializationEnum.getSpecializationFromCode(diagnosisEntity.getDoctor()).getRussianName());
        doctor.setName("Тараканчиков Димасик Бобинчиков");
        doctor.setId(String.valueOf(123L));

        var allLinksByDiagnosis = diagnosisExaminationLnkRepository.getAllByDiagnosis(diagnosisEntity);

        for (DiagnosisExaminationLnkEntity linksByDiagnosis : allLinksByDiagnosis) {
            var examination = linksByDiagnosis.getExamination();

            var research = new Research();
            research.setId(examination.getId());
            research.setStatus(linksByDiagnosis.getStatus());
            research.setIsNecessary(false);
            if (linksByDiagnosis.getStatus().equals("standard") || linksByDiagnosis.getStatus().equals("missing")) {
                research.setIsNecessary(true);
            }
            research.setName(examination.getCode());
            researchList.add(research);
        }
        response.setResearch(researchList);
        response.setDoctor(doctor);
        response.setReception(reception);

        return response;
    }
}
