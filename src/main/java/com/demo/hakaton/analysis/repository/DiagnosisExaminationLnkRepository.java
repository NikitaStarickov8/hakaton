package com.demo.hakaton.analysis.repository;

import com.demo.hakaton.analysis.entity.DiagnosisEntity;
import com.demo.hakaton.analysis.entity.DiagnosisExaminationLnkEntity;
import com.demo.hakaton.dictionary.entity.ExaminationDictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisExaminationLnkRepository extends JpaRepository<DiagnosisExaminationLnkEntity, Long>, JpaSpecificationExecutor<DiagnosisExaminationLnkEntity> {

    List<DiagnosisExaminationLnkEntity> getAllByDiagnosis(DiagnosisEntity diagnosis);

    Boolean existsByDiagnosisAndExamination(DiagnosisEntity diagnosis, ExaminationDictionaryEntity examinationDictionaryEntity);
}
