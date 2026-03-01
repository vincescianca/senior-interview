package com.elemental.tech.workitems.api;

import com.elemental.tech.workitems.api.dto.CreateWorkItemRequest;
import com.elemental.tech.workitems.api.dto.WorkItemResponse;
import com.elemental.tech.workitems.service.WorkItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-items")
public class WorkItemController {

    private final WorkItemService service;

    public WorkItemController(WorkItemService service) {
        this.service = service;
    }

    /**
     * Create a new WorkItem
     * @param request the work item creation request
     * @return the created WorkItem
     */
    @PostMapping
    public ResponseEntity<WorkItemResponse> create (@Valid @RequestBody CreateWorkItemRequest request) {
        WorkItemResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * List all WorkItems
     * @return list of WorkItemResponse
     */
    @GetMapping
    public ResponseEntity<List<WorkItemResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    /**
     * Get a WorkItem by id
     * @param id the work item id
     * @return the WorkItemResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkItemResponse> get(@PathVariable long id) {
        return ResponseEntity.ok(service.get(id));
    }

    /**
     * Update the status of a WorkItem
     * @param id the work item id
     * @param status the new status
     * @param version the expected version for concurrency control
     * @return the updated WorkItemResponse
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkItemResponse> updateStatus(
            @PathVariable long id,
            @RequestParam("status") String status,
            @RequestParam("version") Long version
    ) {
        return ResponseEntity.ok(service.updateStatus(id, status, version));
    }
}
