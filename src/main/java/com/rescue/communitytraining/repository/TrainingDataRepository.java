package com.rescue.communitytraining.repository;

import com.rescue.communitytraining.entity.TrainingData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainingDataRepository extends JpaRepository<TrainingData, Long> {
    List<TrainingData> findByDateBetweenAndDistrictId(LocalDate start, LocalDate end, Long districtId);
}