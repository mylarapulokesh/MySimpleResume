package com.resume.resume.Repository;

import com.resume.resume.model.Templates;
import com.resume.resume.projections.TemplateTagsProjection;
import com.resume.resume.projections.TemplatesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Templates,String> {
    @Query(value = """
            SELECT template_name,puppeteer_template_path,category,description,display_name,preview_image FROM TEMPLATES WHERE template_name=:templateName""",nativeQuery = true)
    Templates getByTemplateName(String templateName);

    @Query(value = """
            SELECT tags As tags FROM TEMPLATES t JOIN TEMPLATES_TAGS tt ON t.template_name = tt.templates_template_name WHERE template_name=:templateName""",nativeQuery = true)
    List<TemplateTagsProjection> getTagsByTemplateName(String templateName);

    @Query(value = """
        SELECT 
            t.template_name AS templateName,
            t.category AS category,
            t.description AS description,
            t.display_name AS displayName,
            t.preview_image AS previewImage,
            t.puppeteer_template_path AS puppeteerTemplatePath,
            tt.tags AS tags
        FROM templates t
        JOIN templates_tags tt ON t.template_name = tt.templates_template_name
        """, nativeQuery = true)
    List<TemplatesProjection> getAllTemplates();
}
