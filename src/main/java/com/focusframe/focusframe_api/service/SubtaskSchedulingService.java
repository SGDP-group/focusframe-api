package com.focusframe.focusframe_api.service;

import com.focusframe.focusframe_api.dto.scheduleSubtaskDto.*;
import com.focusframe.focusframe_api.model.Subtask;
import com.focusframe.focusframe_api.model.Task;
import com.focusframe.focusframe_api.model.User;
import com.focusframe.focusframe_api.repository.SubtaskRepository;
import com.focusframe.focusframe_api.repository.TaskRepository;
import com.focusframe.focusframe_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SubtaskSchedulingService {

    private static final Integer MIN_HOUR = 5;  // 5 AM
    private static final Integer MAX_HOUR = 23; // 11 PM
    private static final Integer BUFFER_MINUTES = 30;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private SubtaskRepository subtaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Setters for testing
    public void setSubtaskRepository(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public ScheduleSubtaskResponse scheduleSubtasks(ScheduleSubtaskRequest request) {
        try {
            // Validate user exists
            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ScheduleSubtaskResponse.builder()
                    .success(false)
                    .message("User with ID " + request.getUserId() + " not found")
                    .build();
            }

            // Validate request has subtasks
            if (request.getSubtasks() == null || request.getSubtasks().isEmpty()) {
                return ScheduleSubtaskResponse.builder()
                    .success(false)
                    .message("Request must contain at least one subtask")
                    .build();
            }

            // Get all existing subtasks for the user
            List<Subtask> existingSubtasks = subtaskRepository.findAllUserSubtasksByStartTimeAsc(request.getUserId());

            // Calculate the latest end time and corresponding subtask on the request date
            LocalDate requestDate;
            try {
                requestDate = LocalDate.parse(request.getSubtasks().get(0).getPreferredDate(), DATE_FORMATTER);
            } catch (java.time.format.DateTimeParseException e) {
                return ScheduleSubtaskResponse.builder()
                    .success(false)
                    .message("Invalid date format in first subtask. Expected YYYY-MM-DD, got: " + request.getSubtasks().get(0).getPreferredDate())
                    .build();
            }

            Subtask latestExistingSubtask = existingSubtasks.stream()
                .filter(s -> s.getEndTime() != null && s.getEndTime().toLocalDate().equals(requestDate))
            .max(Comparator.comparing(Subtask::getEndTime))
            .orElse(null);
        
        LocalDateTime latestExistingEndTime = latestExistingSubtask != null ? latestExistingSubtask.getEndTime() : null;

        List<ScheduledSubtaskItem> scheduledSubtasks = new ArrayList<>();
        List<ConflictSummary> conflictSummaries = new ArrayList<>();
        LocalDateTime lastProcessedEndTime = null;  // Track the end time of the previous task in this batch
        String lastProcessedDescription = null;    // Track the description of the previous task in this batch
        boolean isFirstSubtaskInBatch = true;      // Track if this is the first subtask in the batch
        int subtaskIndex = 0;

        // Process each requested subtask (schedule only, do not save)
        for (ScheduleSubtaskRequestItem requestItem : request.getSubtasks()) {
            ScheduleResult result = scheduleSubtask(
                requestItem,
                existingSubtasks,
                lastProcessedEndTime,  // Pass the end time of the previous task
                lastProcessedDescription,  // Pass the description of the previous task
                isFirstSubtaskInBatch ? latestExistingSubtask : null  // Pass latest existing subtask only for first subtask
            );

            scheduledSubtasks.add(result.scheduledItem);
            
            // Build conflict summary if times were rescheduled (preferred != scheduled)
            LocalDate preferredDate = LocalDate.parse(requestItem.getPreferredDate(), DATE_FORMATTER);
            LocalDateTime originalTime = preferredDate.atStartOfDay();
            if (requestItem.getPreferredStartTime() != null && !requestItem.getPreferredStartTime().trim().isEmpty()) {
                LocalTime preferredTime = LocalTime.parse(requestItem.getPreferredStartTime().trim(), TIME_FORMATTER);
                originalTime = LocalDateTime.of(preferredDate, preferredTime);
            }
            
            if (!originalTime.equals(result.scheduledItem.getStartTime())) {
                String timeShift = calculateTimeShift(requestItem.getPreferredStartTime(), result.scheduledItem.getStartTime());
                
                ConflictSummary conflictSummary = ConflictSummary.builder()
                    .subtaskIndex(subtaskIndex)
                    .subtaskDescription(requestItem.getDescription())
                    .originalTime(originalTime)
                    .rescheduledTime(result.scheduledItem.getStartTime())
                    .conflictWith(result.scheduledItem.getConflictWith())  // May be null for batch sequencing
                    .timeShift(timeShift)
                    .build();
                conflictSummaries.add(conflictSummary);
            }

            // Update the last processed end time and description for the next iteration
            if (result.scheduledItem.getEndTime() != null) {
                lastProcessedEndTime = result.scheduledItem.getEndTime();
                lastProcessedDescription = result.scheduledItem.getDescription();
            }
            
            isFirstSubtaskInBatch = false;
            subtaskIndex++;
        }

        // Build summary statistics
        ScheduleSummary summary = ScheduleSummary.builder()
            .totalRequested(request.getSubtasks().size())
            .totalScheduled(scheduledSubtasks.size())
            .conflictsDetected(conflictSummaries.size())
            .resolvedConflicts(conflictSummaries.size()) 
            .failedToResolve(0)
            .build();

        return ScheduleSubtaskResponse.builder()
            .success(true)
            .message("Subtasks scheduled successfully" + (conflictSummaries.isEmpty() ? "" : " with conflict resolution"))
            .summary(summary)
            .scheduledSubtasks(scheduledSubtasks)
            .conflictSummary(conflictSummaries)
            .build();
        } catch (IllegalArgumentException e) {
            return ScheduleSubtaskResponse.builder()
                .success(false)
                .message(e.getMessage())
                .build();
        } catch (Exception e) {
            return ScheduleSubtaskResponse.builder()
                .success(false)
                .message("Failed to schedule subtasks: " + e.getMessage())
                .build();
        }
    }

    private ScheduleResult scheduleSubtask(
        ScheduleSubtaskRequestItem requestItem,
        List<Subtask> existingSubtasks,
        LocalDateTime lastBatchTaskEndTime,
        String lastBatchTaskDescription,
        Subtask latestExistingSubtask
    ) {
        try {
            if (requestItem.getEstimatedTime() == null || requestItem.getEstimatedTime() <= 0) {
                throw new IllegalArgumentException("Estimated time must be greater than 0");
            }

            LocalDate preferredDate;
            try {
                preferredDate = LocalDate.parse(requestItem.getPreferredDate(), DATE_FORMATTER);
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid preferred date format. Expected YYYY-MM-DD, got: " + requestItem.getPreferredDate(), e);
            }

            LocalDateTime proposedStartTime;
            LocalDateTime preferredStartTime;
            if (requestItem.getPreferredStartTime() != null && !requestItem.getPreferredStartTime().trim().isEmpty()) {
                try {
                    String timeStr = requestItem.getPreferredStartTime().trim();
                    LocalTime preferredTime = LocalTime.parse(timeStr, TIME_FORMATTER);
                    proposedStartTime = LocalDateTime.of(preferredDate, preferredTime);
                    preferredStartTime = proposedStartTime;
                } catch (java.time.format.DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid preferred start time format. Expected HH:mm (e.g., 06:27), got: " + requestItem.getPreferredStartTime(), e);
                }
            } else {
                if (lastBatchTaskEndTime != null && lastBatchTaskEndTime.toLocalDate().equals(preferredDate)) {
                    proposedStartTime = lastBatchTaskEndTime.plusMinutes(BUFFER_MINUTES);
                } else {
                    proposedStartTime = preferredDate.atTime(LocalTime.of(9, 0));
                }
                preferredStartTime = proposedStartTime;
            }

            LocalDateTime proposedEndTime = proposedStartTime.plusMinutes(requestItem.getEstimatedTime());

            ScheduleResult result = resolveConflicts(
                proposedStartTime,
                proposedEndTime,
                requestItem,
                existingSubtasks,
                lastBatchTaskEndTime,
                lastBatchTaskDescription,
                new HashSet<>(),
                preferredStartTime,
                false,
                latestExistingSubtask
            );

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to schedule subtask: " + e.getMessage(), e);
        }
    }

    private LocalDateTime calculateDefaultStartTime(LocalDate preferredDate, List<LocalDateTime> processedTimes) {
        LocalDateTime baseTime = preferredDate.atStartOfDay();

        // Find the latest time from processed subtasks on this date
        LocalDateTime latestTime = processedTimes.stream()
            .filter(t -> t.toLocalDate().equals(preferredDate))
            .max(LocalDateTime::compareTo)
            .orElse(baseTime.withHour(MIN_HOUR).withMinute(0));

        // Add buffer to latest time
        LocalDateTime proposedTime = latestTime.plusMinutes(BUFFER_MINUTES);

        // If no processed times or all from different dates, start at 9 AM
        if (processedTimes.isEmpty() || proposedTime.toLocalDate().isAfter(preferredDate)) {
            proposedTime = baseTime.withHour(9).withMinute(0);
        }

        return proposedTime;
    }



    private ScheduleResult resolveConflicts(
        LocalDateTime startTime,
        LocalDateTime endTime,
        ScheduleSubtaskRequestItem requestItem,
        List<Subtask> existingSubtasks,
        LocalDateTime lastBatchTaskEndTime,
        String lastBatchTaskDescription,
        Set<String> visitedConflicts,
        LocalDateTime preferredStartTime,
        boolean wasRescheduled,
        Subtask latestExistingSubtask
    ) {
        // Check for conflicts with existing subtasks
        Subtask conflictingSubtask = findConflictingSubtask(startTime, endTime, existingSubtasks);

        if (conflictingSubtask != null) {
            // Create conflict key to prevent infinite recursion
            String conflictKey = conflictingSubtask.getId() + "|" + startTime;
            if (visitedConflicts.contains(conflictKey)) {
                // Already tried to resolve this, return with conflict info
                return createScheduleResult(
                    requestItem,
                    startTime,
                    endTime,
                    conflictingSubtask,
                    true,
                    preferredStartTime,
                    true,
                    lastBatchTaskDescription
                );
            }
            visitedConflicts.add(conflictKey);

            // Reschedule to 30 minutes after conflict ends
            LocalDateTime newStartTime = conflictingSubtask.getEndTime().plusMinutes(BUFFER_MINUTES);
            LocalDateTime newEndTime = newStartTime.plusMinutes(requestItem.getEstimatedTime());

            // Validate new time window
            if (newStartTime.getHour() >= MAX_HOUR) {
                // Move to next day
                newStartTime = newStartTime.toLocalDate().plusDays(1)
                    .atTime(LocalTime.of(MIN_HOUR, 0));
                newEndTime = newStartTime.plusMinutes(requestItem.getEstimatedTime());
            }

            // Recursively check for conflicts with new time (mark as rescheduled due to ACTUAL conflict)
            return resolveConflicts(
                newStartTime,
                newEndTime,
                requestItem,
                existingSubtasks,
                lastBatchTaskEndTime,
                lastBatchTaskDescription,
                visitedConflicts,
                preferredStartTime,
                true,
                latestExistingSubtask
            );
        }

        // Check if this task starts before the previous batch task ends
        // Batch tasks should be sequential with NO buffer between them (separate from database tasks)
        if (lastBatchTaskEndTime != null && 
            startTime.isBefore(lastBatchTaskEndTime) &&
            startTime.toLocalDate().equals(lastBatchTaskEndTime.toLocalDate())) {
            // Overlap with previous task - reschedule immediately after it (no buffer for batch tasks)
            LocalDateTime newStartTime = lastBatchTaskEndTime;
            LocalDateTime newEndTime = newStartTime.plusMinutes(requestItem.getEstimatedTime());
            
            return resolveConflicts(
                newStartTime,
                newEndTime,
                requestItem,
                existingSubtasks,
                lastBatchTaskEndTime,
                lastBatchTaskDescription,
                visitedConflicts,
                preferredStartTime,
                true,  // Mark as rescheduled since time changed (batch sequencing is still a reschedule)
                latestExistingSubtask
            );
        }

        // Ensure first batch task respects buffer from latest existing subtask
        if (latestExistingSubtask != null && 
            lastBatchTaskEndTime == null &&  // This is the first task in batch
            startTime.isBefore(latestExistingSubtask.getEndTime().plusMinutes(BUFFER_MINUTES)) &&
            startTime.toLocalDate().equals(latestExistingSubtask.getEndTime().toLocalDate())) {
            // Move to after the buffer time from latest existing subtask
            LocalDateTime newStartTime = latestExistingSubtask.getEndTime().plusMinutes(BUFFER_MINUTES);
            LocalDateTime newEndTime = newStartTime.plusMinutes(requestItem.getEstimatedTime());
            
            return resolveConflicts(
                newStartTime,
                newEndTime,
                requestItem,
                existingSubtasks,
                lastBatchTaskEndTime,
                lastBatchTaskDescription,
                visitedConflicts,
                preferredStartTime,
                true,  // This IS a real conflict with existing subtask - mark as rescheduled
                latestExistingSubtask
            );
        }

        // No conflicts with existing subtasks or previous batch tasks found
        // Pass latestExistingSubtask to createScheduleResult if wasRescheduled is true
        return createScheduleResult(
            requestItem,
            startTime,
            endTime,
            wasRescheduled && latestExistingSubtask != null ? latestExistingSubtask : null,
            wasRescheduled,
            preferredStartTime,
            wasRescheduled,
            lastBatchTaskDescription
        );
    }

    private Subtask findConflictingSubtask(LocalDateTime startTime, LocalDateTime endTime, List<Subtask> subtasks) {
        return subtasks.stream()
            .filter(s -> hasTimeOverlap(s.getStartTime(), s.getEndTime(), startTime, endTime))
            .findFirst()
            .orElse(null);
    }

    private Subtask findProcessedConflict(LocalDateTime startTime, LocalDateTime endTime, List<LocalDateTime> processedTimes) {
        // Check if any processed times create conflicts
        for (LocalDateTime processedTime : processedTimes) {
            if (startTime.equals(processedTime) || 
                (startTime.isBefore(processedTime.plusMinutes(30)) && 
                 startTime.isAfter(processedTime.minusMinutes(30)))) {
                // Return a dummy subtask for conflict tracking
                return Subtask.builder()
                    .endTime(processedTime.plusMinutes(30))
                    .build();
            }
        }
        return null;
    }

    private boolean hasTimeOverlap(LocalDateTime existingStart, LocalDateTime existingEnd,
                                  LocalDateTime newStart, LocalDateTime newEnd) {
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    private ScheduleResult createScheduleResult(
        ScheduleSubtaskRequestItem requestItem,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Subtask conflictingSubtask,
        boolean conflictDetected,
        LocalDateTime preferredStartTime,
        boolean wasRescheduled,
        String lastBatchTaskDescription
    ) {
        ScheduledSubtaskItem item = ScheduledSubtaskItem.builder()
            .description(requestItem.getDescription())
            .estimatedTime(requestItem.getEstimatedTime())
            .preferredStartTime(preferredStartTime)
            .startTime(startTime)
            .endTime(endTime)
            .conflictDetected(conflictDetected || wasRescheduled)
            .conflictWith(
                conflictingSubtask != null
                    ? buildConflictInfo(conflictingSubtask)
                    : null
            )
            .build();

        return new ScheduleResult(item, null);  // No longer building ConflictResolution
    }

    private ConflictInfo buildConflictInfo(Subtask conflictingSubtask) {
        return ConflictInfo.builder()
            .type("SUBTASK")
            .id(conflictingSubtask.getId())
            .taskId(conflictingSubtask.getTask() != null ? conflictingSubtask.getTask().getId() : null)
            .taskName(conflictingSubtask.getTask() != null ? conflictingSubtask.getTask().getName() : "Unknown Task")
            .subtaskDescription(conflictingSubtask.getDescription() != null ? 
                               conflictingSubtask.getDescription() : conflictingSubtask.getName())
            .endTime(conflictingSubtask.getEndTime())
            .resolution("Rescheduled 30 minutes after conflict ends")
            .build();
    }

    private String calculateTimeShift(String preferredStartTimeStr, LocalDateTime rescheduledTime) {
        try {
            LocalDateTime preferredTime = LocalDateTime.parse(preferredStartTimeStr);
            java.time.Duration duration = java.time.Duration.between(preferredTime, rescheduledTime);
            
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            
            if (hours == 0) {
                return minutes + " minutes later";
            } else if (minutes == 0) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " later";
            } else {
                return hours + " hour" + (hours > 1 ? "s" : "") + " " + minutes + " minutes later";
            }
        } catch (Exception e) {
            return "Time rescheduled";
        }
    }

    private static class ScheduleResult {
        ScheduledSubtaskItem scheduledItem;

        ScheduleResult(ScheduledSubtaskItem scheduledItem, Void unused) {
            this.scheduledItem = scheduledItem;
        }
    }
}
