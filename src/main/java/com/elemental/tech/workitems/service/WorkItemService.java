package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.domain.WorkItem;
import com.elemental.tech.workitems.persistence.WorkItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkItemService {

    private final WorkItemRepository repository;

    public WorkItemService(WorkItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WorkItemResponse create(CreateWorkItemRequest request) {
        WorkItem workItem = new WorkItem(
                request.title(),
                request.description(),
                WorkItem.Priority.valueOf(request.priority().name())
        );

        WorkItem saved = repository.save(workItem);
        return WorkItemMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkItemResponse> list() {
        return repository.findAll()
                .stream()
                .map(WorkItemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkItemResponse get(long id) {
        WorkItem workItem = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("work item not found: " + id));
        return WorkItemMapper.toResponse(workItem);
    }

    @Transactional
    public WorkItemResponse updateStatus(long id, String status, Long version) {
        WorkItem workItem = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("work item not found: " + id));
        checkVersionConflict(workItem, version);
        WorkItem.Status newStatus = parseStatus(status);
        workItem.transitionTo(newStatus);
        WorkItem saved = repository.save(workItem);
        return WorkItemMapper.toResponse(saved);
    }

    public Page<WorkItem> findByFilter(WorkItem filter, Pageable pageable) {
        return repository.findByStatusPriorityTitle(
                filter.getStatus(),
                filter.getPriority(),
                filter.getTitle(),
                pageable
        );
    }

    private void checkVersionConflict(WorkItem workItem, Long version) {
        if (!workItem.getVersion().equals(version)) {
            throw new IllegalStateException("Version conflict: expected " + version + ", found " + workItem.getVersion());
        }
    }

    private WorkItem.Status parseStatus(String status) {
        try {
            return WorkItem.Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }
}
