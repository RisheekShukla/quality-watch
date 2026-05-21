package com.qualitywatch.api.project;

import com.qualitywatch.dto.response.ProjectResponse;
import com.qualitywatch.service.analytics.ProjectQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectQueryService projectQueryService;

    public ProjectController(ProjectQueryService projectQueryService) {
        this.projectQueryService = projectQueryService;
    }

    @GetMapping
    public List<ProjectResponse> listProjects() {
        return projectQueryService.listProjects();
    }
}
