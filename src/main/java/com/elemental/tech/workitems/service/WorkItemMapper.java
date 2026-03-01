package com.elemental.tech.workitems.service;

import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.domain.WorkItem;

final class WorkItemMapper {

    private WorkItemMapper() {
    }

    static WorkItemResponse toResponse(WorkItem workItem) {
        return new WorkItemResponse(
                workItem.getId(),
                workItem.getTitle(),
                workItem.getDescription(),
                workItem.getStatus().name(),
                workItem.getPriority().name(),
                workItem.getCreatedAt(),
                workItem.getUpdatedAt()
        );
    }
}
