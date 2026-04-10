package com.resume.resume.Repository;

import com.resume.resume.model.EducationDetails;
import com.resume.resume.model.Resume;
import com.resume.resume.projections.EducationProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface EducationDetailsRepository extends JpaRepository<EducationDetails,Long> {
    @Query(value = """
        SELECT education_type AS educationType,
               institute_name AS instituteName,
               course_name AS courseName,
               course_start_year AS courseStartYear,
               course_end_year AS courseEndYear
        FROM education_details
        WHERE email_id = :emailId
        """, nativeQuery = true)
    List<EducationProjection> getAllByEmail(@Param("emailId") String emailId);

    void deleteByResume(Resume resume);

}
