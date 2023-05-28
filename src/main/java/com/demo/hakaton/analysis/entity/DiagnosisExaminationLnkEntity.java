package com.demo.hakaton.analysis.entity;

import com.demo.hakaton.dictionary.entity.ExaminationDictionaryEntity;
import com.demo.hakaton.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Table(name = "DIAGNOSIS_EXAMINATION_LNK")
@NoArgsConstructor
@Entity
public class DiagnosisExaminationLnkEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "DIAGNOSIS_ID")
    private DiagnosisEntity diagnosis;

    @ManyToOne
    @JoinColumn(name = "EXAMINATION_DICTIONARY_ID")
    private ExaminationDictionaryEntity examination;

    @Column(name = "STATUS")
    private String status;

}
