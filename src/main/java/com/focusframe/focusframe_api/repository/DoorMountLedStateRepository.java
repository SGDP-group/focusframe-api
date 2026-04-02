package com.focusframe.focusframe_api.repository;

import com.focusframe.focusframe_api.model.DoorMountLedState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoorMountLedStateRepository extends JpaRepository<DoorMountLedState, Long> {
    Optional<DoorMountLedState> findByUser_Id(Integer userId);
}
