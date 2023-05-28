package com.demo.hakaton.dictionary.entity;

import com.demo.hakaton.analysis.entity.DiagnosisExaminationLnkEntity;
import com.demo.hakaton.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@Table(name = "EXAMINATION_DICTIONARY")
@Entity

public class ExaminationDictionaryEntity extends BaseEntity {

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "examination", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DiagnosisExaminationLnkEntity> diagnosisExaminationLnkEntities;
}
