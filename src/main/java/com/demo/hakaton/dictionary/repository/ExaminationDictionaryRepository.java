package com.demo.hakaton.dictionary.repository;

import com.demo.hakaton.dictionary.entity.ExaminationDictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExaminationDictionaryRepository extends JpaRepository<ExaminationDictionaryEntity, Long>, JpaSpecificationExecutor<ExaminationDictionaryEntity> {

    ExaminationDictionaryEntity getFirstByNameContainingIgnoreCase(String name);

    ExaminationDictionaryEntity getFirstByNameIgnoreCase(String name);

    ExaminationDictionaryEntity getFirstByCodeIgnoreCase(String code);
}
