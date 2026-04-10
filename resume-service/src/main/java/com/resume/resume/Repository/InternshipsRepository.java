package com.resume.resume.Repository;

import com.resume.resume.model.Internships;
import com.resume.resume.model.Resume;
import com.resume.resume.projections.InternshipProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InternshipsRepository extends JpaRepository<Internships,Long> {
    @Query(value = """
            SELECT internship_title AS internshipTitle,internship_description AS internshipDescription FROM INTERNSHIPS where email_id = :emailId""", nativeQuery = true)
    InternshipProjection  getAllByEmail(String emailId);

    Internships findByResume(Resume resume);
}

