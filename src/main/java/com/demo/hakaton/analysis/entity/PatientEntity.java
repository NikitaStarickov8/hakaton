package com.demo.hakaton.analysis.entity;

import com.demo.hakaton.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Table(name = "PATIENT")
@NoArgsConstructor
@Entity
public class PatientEntity extends BaseEntity {

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "EMIAS_ID")
    private Integer emiasId;

    @Column(name = "SEX")
    private String sex;
}
