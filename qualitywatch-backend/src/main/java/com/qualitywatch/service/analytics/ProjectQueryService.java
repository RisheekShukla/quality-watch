package com.qualitywatch.service.analytics;

import com.qualitywatch.domain.common.Project;
import com.qualitywatch.dto.response.ProjectResponse;
import com.qualitywatch.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ProjectQueryService {

    private final ProjectRepository projectRepository;

    public ProjectQueryService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectResponse> listProjects() {
        return projectRepository.findAll().stream()
                .sorted(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER))
                .map(p -> new ProjectResponse(p.getId(), p.getName()))
                .toList();
    }
}
