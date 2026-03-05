package com.focusframe.focusframe_api.repository;

import com.focusframe.focusframe_api.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Integer> {
    List<Subtask> findByTaskId(Integer taskId);
    List<Subtask> findByTaskIdOrderByTaskOrderAsc(Integer taskId);
    List<Subtask> findByStatusId(Integer statusId);
    List<Subtask> findByCompleted(Boolean completed);
    List<Subtask> findByTaskIdAndCompleted(Integer taskId, Boolean completed);
}
