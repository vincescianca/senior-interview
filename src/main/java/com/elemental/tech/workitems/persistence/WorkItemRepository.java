package com.elemental.tech.workitems.persistence;


import com.elemental.tech.workitems.domain.WorkItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    Page<WorkItem> findAll(Pageable pageable);

    @Query("SELECT w FROM WorkItem w WHERE " +
            "(:status IS NULL OR w.status = :status) AND " +
            "(:priority IS NULL OR w.priority = :priority) AND " +
            "(:title IS NULL OR w.title LIKE %:title%)")
    Page<WorkItem> findByStatusPriorityTitle(@Param("status") WorkItem.Status status,
                                @Param("priority") WorkItem.Priority priority,
                                @Param("title") String title,
                                Pageable pageable);

}
