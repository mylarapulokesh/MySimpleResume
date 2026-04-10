package com.resume.resume.Repository;

import com.resume.resume.model.PersonalDetails;
import com.resume.resume.model.Resume;
import com.resume.resume.projections.PersonProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails,Long> {
    @Query(value = """
            SELECT address AS personAddress,gmail AS personGmail,name AS personName,phone_number AS personPhone,summary AS personSummary FROM personal_details WHERE email_id = :emailId""", nativeQuery = true)
    PersonProjection getAllByEmail(@Param("emailId") String email);

    PersonalDetails findByResume(Resume resume);
}
