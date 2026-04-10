package com.resume.resume.service;

import com.resume.resume.DTO.ResumeRequestDTO;
import com.resume.resume.DTO.TemplateRequestDTO;
import com.resume.resume.DTO.TemplatesListDTO;
import com.resume.resume.Repository.*;
import com.resume.resume.client.AccountClient;
import com.resume.resume.model.*;
import com.resume.resume.projections.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private PersonalDetailsRepository personalDetailsRepository;
    @Autowired
    private InternshipsRepository internshipsRepository;
    @Autowired
    private EducationDetailsRepository educationDetailsRepository;
    @Autowired
    private ProjectsRepository projectsRepository;
    @Autowired
    private SocialMediaRepository socialMediaRepo;
    @Autowired
    private TemplateRepository templateRepo;

    @Autowired
    private AccountClient accountClient;

    public ResumeRequestDTO saveInfo(ResumeRequestDTO request) {
        String email = request.getPersonalDetails().getGmail();

        Resume existingResume = resumeRepository.findByEmailId(email);
        Resume resume = (existingResume != null) ? existingResume : new Resume();

        resume.setPersonName(request.getPersonalDetails().getName());
        resume.setSkillSet(request.getSkillSet());
        resume.setLanguagesKnown(request.getLanguagesKnown());
        resume.setEmailId(email);
        resumeRepository.save(resume);

        SocialMediaUrls existingSocialMedia = socialMediaRepo.findByResume(resume);
        SocialMediaUrls socialMediaUrls = (existingSocialMedia != null) ? existingSocialMedia : new SocialMediaUrls();
        socialMediaUrls.setLinkedInUrl(request.getSocialMediaURLs().getLinkedInUrl());
        socialMediaUrls.setGitHubUrl(request.getSocialMediaURLs().getGitHubUrl());
        socialMediaUrls.setPortfolioUrl(request.getSocialMediaURLs().getPortfolioUrl());
        socialMediaUrls.setResume(resume);
        socialMediaRepo.save(socialMediaUrls);

        Internships existingInternships = internshipsRepository.findByResume(resume);
        Internships internships = (existingInternships != null) ? existingInternships : new Internships();
        internships.setInternshipTitle(request.getInternships().getInternshipNameAndDescription().keySet().toString());
        internships.setInternshipDescription(request.getInternships().getInternshipNameAndDescription().values().toString());
        internships.setResume(resume);
        internshipsRepository.save(internships);

        Projects existingProjects = projectsRepository.findByResume(resume);
        Projects projects = (existingProjects != null) ? existingProjects : new Projects();
        projects.setProjectTitle(request.getProjects().getProjectNameAndDescription().keySet().toString());
        projects.setProjectDescription(request.getProjects().getProjectNameAndDescription().values().toString());
        projects.setResume(resume);
        projectsRepository.save(projects);

        PersonalDetails existingPersonalDetails = personalDetailsRepository.findByResume(resume);
        PersonalDetails personalDetails = (existingPersonalDetails != null) ? existingPersonalDetails : new PersonalDetails();
        personalDetails.setAddress(request.getPersonalDetails().getAddress());
        personalDetails.setName(request.getPersonalDetails().getName());
        personalDetails.setGmail(email);
        personalDetails.setPhoneNumber(request.getPersonalDetails().getPhoneNumber());
        personalDetails.setSummary(request.getPersonalDetails().getSummary());
        personalDetails.setResume(resume);
        personalDetailsRepository.save(personalDetails);

        educationDetailsRepository.deleteByResume(resume);
        Set<String> keySet = request.getEducationDetails().getEducationDetails().keySet();
        for (String key : keySet) {
            ResumeRequestDTO.EducationDetails.Description description = request.getEducationDetails().getEducationDetails().get(key);
            EducationDetails educationDetails = new EducationDetails();
            educationDetails.setCourseName(description.getCourseName());
            educationDetails.setCourseStartYear(description.getCourseStartYear());
            educationDetails.setCourseEndYear(description.getCourseEndYear());
            educationDetails.setInstituteName(description.getInstituteName());
            educationDetails.setEducationType(key);
            educationDetails.setResume(resume);
            educationDetailsRepository.save(educationDetails);
        }

        return request;
    }

    public ResponseEntity<String> saveTemplateInfo(Templates templateInfo) {

        Templates templateEntity = new Templates();
        templateEntity.setTemplateName(templateInfo.getTemplateName());
        String puppeteerTemplatePath = "/resume/" + templateEntity.getTemplateName() + ".html";
        templateEntity.setPuppeteerTemplatePath(puppeteerTemplatePath);
        templateEntity.setCategory(templateInfo.getCategory());
        templateEntity.setDescription(templateInfo.getDescription());
        templateEntity.setTags(templateInfo.getTags());
        templateEntity.setPreviewImage(templateInfo.getPreviewImage());
        templateEntity.setDisplayName(templateInfo.getDisplayName());
        templateRepo.save(templateEntity);

        return ResponseEntity.ok("Template Saved Successfully");
    }

    public ResponseEntity<TemplateRequestDTO> getTemplateByName(String templateName) {

        List<TemplateTagsProjection> templateTags = templateRepo.getTagsByTemplateName(templateName);
        List<String> Tags = new ArrayList<>();
        for (TemplateTagsProjection tag: templateTags){
            Tags.add(tag.getTags());
        }

        Templates templateEntity = templateRepo.getByTemplateName(templateName);

        TemplateRequestDTO templateResponse = new TemplateRequestDTO();

        templateResponse.setDisplayName(templateEntity.getDisplayName());
        templateResponse.setPuppeteerTemplatePath(templateEntity.getPuppeteerTemplatePath());
        templateResponse.setTemplateName(templateEntity.getTemplateName());
        templateResponse.setCategory(templateEntity.getCategory());
        templateResponse.setDescription(templateEntity.getDescription());
        templateResponse.setPreviewImage(templateEntity.getPreviewImage());
        templateResponse.setTags(Tags);


        return ResponseEntity.ok(templateResponse);


    }


    public ResponseEntity<TemplatesListDTO> getAllTemplates() {

        List<TemplatesProjection> rows = templateRepo.getAllTemplates();
        Map<String, TemplateRequestDTO> map = new LinkedHashMap<>();

        for (TemplatesProjection row : rows) {

            TemplateRequestDTO dto = map.computeIfAbsent(row.getTemplateName(), key -> {
                TemplateRequestDTO temp = new TemplateRequestDTO();
                temp.setTemplateName(row.getTemplateName());
                temp.setPuppeteerTemplatePath(row.getPuppeteerTemplatePath());
                temp.setDisplayName(row.getDisplayName());
                temp.setCategory(row.getCategory());
                temp.setDescription(row.getDescription());
                temp.setPreviewImage(row.getPreviewImage());
                temp.setTags(new ArrayList<>());
                return temp;
            });
            if (row.getTags() != null && !dto.getTags().contains(row.getTags())) {
                dto.getTags().add(row.getTags());
            }
        }
        return ResponseEntity.ok(new TemplatesListDTO(new ArrayList<>(map.values())));
    }

    public ResponseEntity<ResumeRequestDTO> getUserInfo(String emailId) {

        ResumeRequestDTO response = new ResumeRequestDTO();
        Boolean userExists = Boolean.valueOf(accountClient.verifyUser(emailId));
        if (userExists.equals(false)){
            return ResponseEntity.ok(response);
        }
        try{
            //personal Details
            ResumeRequestDTO.PersonalDetails personalDetails = new ResumeRequestDTO.PersonalDetails();
            PersonProjection entityPersonDetails = personalDetailsRepository.getAllByEmail(emailId);
            personalDetails.setName(entityPersonDetails.getPersonName());
            personalDetails.setGmail(entityPersonDetails.getPersonGmail());
            personalDetails.setAddress(entityPersonDetails.getPersonAddress());
            personalDetails.setSummary(entityPersonDetails.getPersonSummary());
            personalDetails.setPhoneNumber(entityPersonDetails.getPersonPhone());
            response.setPersonalDetails(personalDetails);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }

        try{
            //education Details
            ResumeRequestDTO.EducationDetails educationDetails = new ResumeRequestDTO.EducationDetails();
            List<EducationProjection> entityEduDetailsList = educationDetailsRepository.getAllByEmail(emailId);
            Map<String, ResumeRequestDTO.EducationDetails.Description> eduMap = new HashMap<>();
            for (EducationProjection p : entityEduDetailsList) {
                ResumeRequestDTO.EducationDetails.Description d = new ResumeRequestDTO.EducationDetails.Description();
                d.setCourseName(p.getCourseName());
                d.setInstituteName(p.getInstituteName());
                d.setCourseStartYear(p.getCourseStartYear());
                d.setCourseEndYear(p.getCourseEndYear());
                eduMap.put(p.getEducationType(), d);
            }
            educationDetails.setEducationDetails(eduMap);
            response.setEducationDetails(educationDetails);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }

        try{
            //project Details
            ProjectsProjection entityProjects = projectsRepository.getAllByEmail(emailId);
            List<String> titles = parseBracketList(entityProjects.getProjectTitle());
            List<String> descs  = parseBracketList(entityProjects.getProjectDescription());
            int n = Math.min(titles.size(), descs.size());
            Map<String,String> projectTitleAndDesc = new HashMap<>();
            for (int i = 0; i < n; i++) {
                String desc = descs.get(i);
                projectTitleAndDesc.put(titles.get(i),desc);
            }
            ResumeRequestDTO.Projects p = new ResumeRequestDTO.Projects();
            p.setProjectNameAndDescription(projectTitleAndDesc);
            response.setProjects(p);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }

        try{
            //internship Details
            InternshipProjection entityInternships = internshipsRepository.getAllByEmail(emailId);
            List<String> internshipTitles = parseBracketList(entityInternships.getInternshipTitle());
            List<String> internshipDescs  = parseBracketList(entityInternships.getInternshipDescription());
            int size = Math.min(internshipTitles.size(), internshipDescs.size());
            Map<String,String> internshipsTitleAndDesc = new HashMap<>();
            for (int i = 0; i < size; i++) {
                String desc = internshipDescs.get(i);
                internshipsTitleAndDesc.put(internshipTitles.get(i),desc);
            }
            ResumeRequestDTO.Internships i = new ResumeRequestDTO.Internships();
            i.setInternshipNameAndDescription(internshipsTitleAndDesc);
            response.setInternships(i);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
        try{
            //SkillSet Details
            List<SkillSetProjection> skillSet = resumeRepository.getAllSkillsByEmail(emailId);
            List<String> skillSetList = new ArrayList<>();
            for (SkillSetProjection s : skillSet){
                skillSetList.add(s.getSkillSet());
            }
            response.setSkillSet(skillSetList);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
        try{
            //Languages Known
            List<LanguageProjection> languages = resumeRepository.getAllLanguagesByEmail(emailId);
            List<String> languagesKnown = new ArrayList<>();
            for(LanguageProjection l : languages){
                languagesKnown.add(l.getLanguages());
            }
            response.setLanguagesKnown(languagesKnown);

        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }

        try{
            //Social MediaURLs
            SocialMediaProjection socialMediaProjection = socialMediaRepo.getAllByEmail(emailId);
            ResumeRequestDTO.SocialMediaURLs socialMediaURLs = new ResumeRequestDTO.SocialMediaURLs();
            socialMediaURLs.setGitHubUrl(socialMediaProjection.getGitHubUrl());
            socialMediaURLs.setLinkedInUrl(socialMediaProjection.getLinkedInUrl());
            socialMediaURLs.setPortfolioUrl(socialMediaProjection.getPortfolioUrl());
            response.setSocialMediaURLs(socialMediaURLs);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
        return ResponseEntity.ok(response);
    }
    private List<String> parseBracketList(String s) {
        if (s == null) return List.of();
        s = s.trim();
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1); // remove [ ]
        }
        if (s.isBlank()) return List.of();

        return Arrays.stream(s.split("\\s*,\\s*")) // split by comma
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .toList();
    }
}
