package com.db.clm.exercise.repository;

import com.db.clm.exercise.model.Nace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaceRepository extends JpaRepository<Nace, Integer> {

    Page<Nace> findAll(Pageable pageable);
}
