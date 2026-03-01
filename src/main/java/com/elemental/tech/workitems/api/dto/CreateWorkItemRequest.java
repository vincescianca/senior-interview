package com.elemental.tech.workitems.api.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWorkItemRequest(
        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 2000)
        String description,

        @NotNull
        Priority priority
) {

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
