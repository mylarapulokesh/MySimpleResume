package com.resume.resume.Repository;

import com.resume.resume.model.Resume;
import com.resume.resume.model.SocialMediaUrls;
import com.resume.resume.projections.SocialMediaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SocialMediaRepository extends JpaRepository<SocialMediaUrls,Long> {
    @Query(value = """
            SELECT git_hub_url AS gitHubUrl,
             linked_in_url AS linkedInUrl,
             portfolio_url AS portfolioUrl FROM SOCIAL_MEDIA_URLS WHERE email_id=:emailId""",nativeQuery = true)
    SocialMediaProjection getAllByEmail(String emailId);

    SocialMediaUrls findByResume(Resume resume);
}
