package com.resume.resume.DTO;


import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ResumeRequestDTO {

    private PersonalDetails personalDetails;
    private Projects projects;
    private Internships internships;
    private EducationDetails educationDetails;
    private List<String> skillSet;
    private List<String> languagesKnown;
    private SocialMediaURLs socialMediaURLs;
    private String puppeteerTemplatePath;
    private String userName;

    @Data
    public static class PersonalDetails {
        private String name;
        private String address;
        private String phoneNumber;
        private String summary;
        private String gmail;
    }
    @Data
    public static class SocialMediaURLs {
        private String linkedInUrl;
        private String gitHubUrl;
        private String portfolioUrl;
    }

    @Data
    public static class Projects {
        Map<String,String> projectNameAndDescription = new HashMap<>();
    }


    @Data
    public static class EducationDetails {
        Map<String, Description> educationDetails = new HashMap<>();
        public static class Description {
            private String instituteName;
            private String courseName;
            private String courseStartYear;
            private String courseEndYear;

            public String getInstituteName() {
                return instituteName;
            }

            public void setInstituteName(String instituteName) {
                this.instituteName = instituteName;
            }

            public String getCourseName() {
                return courseName;
            }

            public void setCourseName(String courseName) {
                this.courseName = courseName;
            }

            public String getCourseStartYear() {
                return courseStartYear;
            }

            public void setCourseStartYear(String courseStartYear) {
                this.courseStartYear = courseStartYear;
            }

            public String getCourseEndYear() {
                return courseEndYear;
            }

            public void setCourseEndYear(String courseEndYear) {
                this.courseEndYear = courseEndYear;
            }

            @Override
            public String toString() {
                return "Description{" +
                        "instituteName='" + instituteName + '\'' +
                        ", courseName='" + courseName + '\'' +
                        ", courseStartYear='" + courseStartYear + '\'' +
                        ", courseEndYear='" + courseEndYear + '\'' +
                        '}';
            }
        }
    }
    @Data
    public static class Internships {
        Map<String,String> internshipNameAndDescription = new HashMap<>();
    }
}
