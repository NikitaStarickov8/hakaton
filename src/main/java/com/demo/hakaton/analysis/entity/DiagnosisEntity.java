package com.demo.hakaton.analysis.entity;

import com.demo.hakaton.dictionary.entity.SicknessEntity;
import com.demo.hakaton.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Table(name = "DIAGNOSIS")
@NoArgsConstructor
@Entity
public class DiagnosisEntity extends BaseEntity {

    @Column(name = "RECEIPT_DATE")
    private LocalDate receiptDate;

    @Column(name = "DOCTOR")
    private String doctor;

    @Column(name = "STATUS")
    private String status;

    @OneToOne
    @JoinColumn(name = "SICKNESS_ID")
    private SicknessEntity sickness;

    @OneToOne
    @JoinColumn(name = "PATIENT_ID", referencedColumnName = "EMIAS_ID")
    private PatientEntity patient;

    @OneToMany(mappedBy = "diagnosis", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiagnosisExaminationLnkEntity> diagnosisExaminationLnkEntities;

}
