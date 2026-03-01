package com.elemental.tech.workitems.domain;

import java.time.Instant;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "work_item")
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Priority priority;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;


    public void setTitle(String title) {
        this.title = title;
        touch();
    }

    public void setDescription(String description) {
        this.description = description;
        touch();
    }

    public void transitionTo(Status newStatus) {
        if (!this.status.canTransitionTo(newStatus))
            throw new IllegalStateException("Invalid transaction " + this.status + " a " + newStatus);

        this.status = newStatus;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
    public enum Status {
        DONE(Set.of()),
        CANCELLED(Set.of()),
        IN_PROGRESS(Set.of(DONE, CANCELLED)),
        OPEN(Set.of(IN_PROGRESS, CANCELLED));

        private final Set<Status> allowed;

        Status(Set<Status> allowed) { this.allowed = allowed; }

        public boolean canTransitionTo(Status target) {
            return allowed.contains(target);
        }
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public WorkItem(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = Status.OPEN;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
}
