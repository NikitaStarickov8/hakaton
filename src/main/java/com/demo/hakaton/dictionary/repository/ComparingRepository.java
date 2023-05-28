package com.demo.hakaton.dictionary.repository;

import com.demo.hakaton.dictionary.entity.ComparingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ComparingRepository extends JpaRepository<ComparingEntity, Long>, JpaSpecificationExecutor<ComparingEntity> {

    ComparingEntity getBySicknessGroupCode(Integer sicknessGroupCode);
}
