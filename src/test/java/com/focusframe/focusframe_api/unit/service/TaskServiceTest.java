package com.focusframe.focusframe_api.unit.service;

import com.focusframe.focusframe_api.model.Task;
import com.focusframe.focusframe_api.model.User;
import com.focusframe.focusframe_api.repository.TaskRepository;
import com.focusframe.focusframe_api.repository.UserRepository;
import com.focusframe.focusframe_api.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void updateTaskUpdatesFieldsAndUser() {
        Task existing = Task.builder().id(3).name("Old").description("Old desc").build();
        User referencedUser = User.builder().id(22).email("owner@focusframe.com").build();

        when(taskRepository.findById(3)).thenReturn(Optional.of(existing));
        when(userRepository.getReferenceById(22)).thenReturn(referencedUser);
        when(taskRepository.save(existing)).thenReturn(existing);

        Task result = taskService.updateTask(3, "New name", "New desc", 22);

        assertEquals("New name", result.getName());
        assertEquals("New desc", result.getDescription());
        assertEquals(22, result.getUser().getId());
        verify(taskRepository).save(existing);
    }

    @Test
    void updateTaskThrowsWhenMissing() {
        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> taskService.updateTask(999, "name", "desc", 1));

        assertEquals("Task not found with id: 999", ex.getMessage());
        verify(taskRepository, never()).save(org.mockito.ArgumentMatchers.any(Task.class));
    }

    @Test
    void deleteTaskRemovesExistingTask() {
        Task existing = Task.builder().id(5).name("Delete me").build();
        when(taskRepository.findById(5)).thenReturn(Optional.of(existing));

        taskService.deleteTask(5);

        verify(taskRepository).delete(existing);
    }
}
