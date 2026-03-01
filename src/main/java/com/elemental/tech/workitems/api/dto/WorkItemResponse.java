package com.elemental.tech.workitems.api.dto;

import java.time.Instant;

public record WorkItemResponse(
        long id,
        String title,
        String description,
        String status,
        String priority,
        Instant createdAt,
        Instant updatedAt
) {
}
