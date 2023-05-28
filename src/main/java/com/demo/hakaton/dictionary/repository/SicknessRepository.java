package com.demo.hakaton.dictionary.repository;

import com.demo.hakaton.dictionary.entity.SicknessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SicknessRepository extends JpaRepository<SicknessEntity, Long>, JpaSpecificationExecutor<SicknessEntity> {

    SicknessEntity getByCode(String code);
}
