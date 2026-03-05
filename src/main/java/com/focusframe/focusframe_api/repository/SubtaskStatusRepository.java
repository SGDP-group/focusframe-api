package com.focusframe.focusframe_api.repository;

import com.focusframe.focusframe_api.model.SubtaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubtaskStatusRepository extends JpaRepository<SubtaskStatus, Integer> {
    Optional<SubtaskStatus> findByName(String name);
    boolean existsByName(String name);
}
