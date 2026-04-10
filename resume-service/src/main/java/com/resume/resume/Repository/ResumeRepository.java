package com.resume.resume.Repository;

import com.resume.resume.model.Resume;
import com.resume.resume.projections.LanguageProjection;
import com.resume.resume.projections.SkillSetProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume,Long> {

    @Query(value = """
            SELECT skill_set FROM RESUME_SKILL_SET rss join RESUME r on r.id = rss.resume_id where email_Id=:emailId
            """, nativeQuery = true)
    List<SkillSetProjection> getAllSkillsByEmail(String emailId);
    @Query(value = """
            SELECT languages_known AS languages FROM RESUME_LANGUAGES_KNOWN rlk join RESUME r on r.id = rlk.resume_id where email_Id=:emailId
            """, nativeQuery = true)
    List<LanguageProjection> getAllLanguagesByEmail(String emailId);

    Resume findByEmailId(String email);
}
