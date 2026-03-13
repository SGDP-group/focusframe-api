package com.focusframe.focusframe_api.repository;

import com.focusframe.focusframe_api.dto.subTasks.SubTaskDto;
import com.focusframe.focusframe_api.model.Subtask;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Integer> {
    List<Subtask> findByTaskId(Integer taskId);
    List<Subtask> findByTaskIdOrderByTaskOrderAsc(Integer taskId);
    List<Subtask> findByStatusId(Integer statusId);
    List<Subtask> findByCompleted(Boolean completed);
    List<Subtask> findByTaskIdAndCompleted(Integer taskId, Boolean completed);

    List<SubTaskDto> findByStartTimeBetweenAndCompletedFalse(LocalDateTime startTimeDateDay, LocalDateTime endTimeDateDay, Sort sort);
}
