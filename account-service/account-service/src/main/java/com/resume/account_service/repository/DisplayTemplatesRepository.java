package com.resume.account_service.repository;

import com.resume.account_service.model.DisplayTemplates;
import com.resume.account_service.projections.ShowCaseTemplatesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DisplayTemplatesRepository extends JpaRepository<DisplayTemplates,Long> {
    @Query(value = """
            SELECT template_names AS templateNames FROM DISPLAY_TEMPLATES_TEMPLATE_NAMES dttn JOIN DISPLAY_TEMPLATES dt ON dttn.DISPLAY_TEMPLATES_ID = dt.ID """, nativeQuery = true)
    List<ShowCaseTemplatesProjection> getAllSavedTemplates();
}
