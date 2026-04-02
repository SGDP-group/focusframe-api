package com.focusframe.focusframe_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doormount_led_states")
public class DoorMountLedState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "session_id", length = 128)
    private String sessionId;

    @Column(name = "green_locked", nullable = false)
    @Builder.Default
    private Boolean greenLocked = false;

    @Column(name = "last_status_id")
    private Integer lastStatusId;

    @Column(name = "signal_type", nullable = false, length = 32)
    @Builder.Default
    private String signalType = "idle";

    @Column(nullable = false)
    @Builder.Default
    private Integer red = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer green = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer blue = 0;

    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
