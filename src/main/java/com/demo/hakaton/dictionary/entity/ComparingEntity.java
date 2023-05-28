package com.demo.hakaton.dictionary.entity;

import com.demo.hakaton.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Table(name = "COMPARING")
@Entity
public class ComparingEntity extends BaseEntity {

    @Column(name = "SICKNESS_GROUP_CODE")
    private Integer sicknessGroupCode;

    @Column(name = "EXAMINATION_GROUP_CODE")
    private Integer examinationGroupCode;
}
