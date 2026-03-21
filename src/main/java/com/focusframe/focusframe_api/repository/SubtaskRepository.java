package com.focusframe.focusframe_api.repository;

import com.focusframe.focusframe_api.dto.subTasksDto.SubTaskDto;
import com.focusframe.focusframe_api.model.Subtask;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Integer> {
    List<Subtask> findByTask_Id(Integer taskId);
    List<SubTaskDto> findByTask_IdOrderByTaskOrderAsc(Integer taskId);
    List<Subtask> findByStatus_Id(Integer statusId);
    List<Subtask> findByCompleted(Boolean completed);
    List<Subtask> findByTask_IdAndCompleted(Integer taskId, Boolean completed);

    List<Subtask> findByStartTimeBetweenAndCompletedFalse(LocalDateTime startTimeDateDay, LocalDateTime endTimeDateDay, Sort sort);
    List<Subtask> findByTask_IdAndStartTimeBetweenAndCompletedFalse(Integer taskId, LocalDateTime startTimeDateDay, LocalDateTime endTimeDateDay, Sort sort);

        @Query("""
            SELECT s FROM Subtask s
            WHERE s.completed = false
              AND s.startTime >= :startOfDay
              AND s.startTime <= :endOfDay
              AND (
                s.startTime >= :now
                OR (s.startTime <= :now AND s.endTime >= :now)
              )
            """)
        List<Subtask> findDueTodayUpcomingOrOngoing(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("now") LocalDateTime now,
            Sort sort);
}
