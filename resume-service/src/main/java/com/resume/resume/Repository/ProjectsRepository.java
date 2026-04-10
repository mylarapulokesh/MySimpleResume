package com.resume.resume.Repository;

import com.resume.resume.model.Projects;
import com.resume.resume.model.Resume;
import com.resume.resume.projections.ProjectsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectsRepository extends JpaRepository<Projects,Long> {
    @Query(value = """
            SELECT project_title AS projectTitle,project_description AS projectDescription FROM PROJECTS where email_id = :emailId""", nativeQuery = true)
    ProjectsProjection getAllByEmail(String emailId);

    Projects findByResume(Resume resume);
}
