package com.demo.hakaton.analysis.repository;

import com.demo.hakaton.analysis.entity.DiagnosisEntity;
import com.demo.hakaton.analysis.entity.PatientEntity;
import com.demo.hakaton.dictionary.entity.SicknessEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, Long>, JpaSpecificationExecutor<DiagnosisEntity> {

    Boolean existsByPatientAndSicknessAndReceiptDate(PatientEntity patient, SicknessEntity sickness, LocalDate localDate);

    @Query(nativeQuery = true, value = "SELECT * FROM DIAGNOSIS WHERE doctor IN :doctor AND receipt_date BETWEEN coalesce(:start,receipt_date) AND coalesce(:end,receipt_date)")
    List<DiagnosisEntity> getAllByDoctorAndDate(@Param("doctor") List<String> doctor,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);


    List<DiagnosisEntity> findAllByReceiptDateBetween(LocalDate start, LocalDate end);

    @Query(nativeQuery = true, value = "SELECT * FROM DIAGNOSIS WHERE doctor IN :doctor AND patient_id = coalesce(:patientId,patient_id) AND receipt_date BETWEEN coalesce(:start,receipt_date) AND coalesce(:end,receipt_date)",
            countQuery = "SELECT count(*) FROM DIAGNOSIS WHERE doctor IN :doctor AND patient_id = coalesce(:patientId,patient_id) AND receipt_date BETWEEN coalesce(:start,receipt_date) AND coalesce(:end,receipt_date)")
    Page<DiagnosisEntity> findAllByFilters(@Param("doctor") List<String> doctor,
                                           @Param("patientId") Integer patientId,
                                           @Param("start") LocalDate start,
                                           @Param("end") LocalDate end,
                                           Pageable pageable);

    DiagnosisEntity getById(Long id);
}
