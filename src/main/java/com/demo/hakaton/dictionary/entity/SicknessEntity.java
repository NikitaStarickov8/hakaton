package com.demo.hakaton.dictionary.entity;

import com.demo.hakaton.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Table(name = "SICKNESS")
@Entity
public class SicknessEntity extends BaseEntity {

    @Column(name = "CODE")
    private String code;

    @Column(name = "COMPARING_GROUP")
    private Integer comparingGroup;

    @Column(name = "NAME")
    private String name;
}
