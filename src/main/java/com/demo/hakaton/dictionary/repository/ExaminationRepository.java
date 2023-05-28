package com.demo.hakaton.dictionary.repository;

import com.demo.hakaton.dictionary.entity.ExaminationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<ExaminationEntity, Long>, JpaSpecificationExecutor<ExaminationEntity> {

    List<ExaminationEntity> getAllByComparingGroup(Integer comparingGroup);

}
