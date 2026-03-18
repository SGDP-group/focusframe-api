package com.focusframe.focusframe_api.repository;

import com.focusframe.focusframe_api.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByUser_Id(Integer userId);

    @Query("SELECT DISTINCT t FROM Task t JOIN FETCH t.user JOIN t.subTasks s WHERE t.user.id = :userId AND s.completed = false AND s.startTime <= :today")
    List<Task> findTasksWithIncompleteSubtasksBeforeOrOn(@Param("userId") Integer userId, @Param("today") LocalDateTime today);
}
