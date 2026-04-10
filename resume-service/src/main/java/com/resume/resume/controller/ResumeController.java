package com.resume.resume.controller;

import com.resume.resume.DTO.ResumeRequestDTO;
import com.resume.resume.DTO.TemplateRequestDTO;
import com.resume.resume.DTO.TemplatesListDTO;
import com.resume.resume.model.Templates;
import com.resume.resume.service.ResumeService;
import jakarta.ws.rs.Path;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;


@RestController
public class ResumeController {

    @Value("${puppeteer.url}")
    private String puppeteerURL;

    @Autowired
    private ResumeService resumeService;

    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/save/information")
    public ResponseEntity<?> saveInfo(@RequestBody ResumeRequestDTO request){
        ResumeRequestDTO resume =resumeService.saveInfo(request);
        return new ResponseEntity<>(resume, HttpStatus.OK);
    }

    @PostMapping("/get/resume")
    public ResponseEntity<byte[]> generateResume(@RequestBody ResumeRequestDTO request){

        String puppeteerUrl =  puppeteerURL +  "resume/pdf";

        HashMap<String, Object> payload = new HashMap<>();

        payload.put("resume", request);
        payload.put("template", request.getPuppeteerTemplatePath());
        System.out.println(payload);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);



        HttpEntity<HashMap<String, Object>> requestEntity =
                new HttpEntity<>(payload, headers);

        ResponseEntity<byte[]> response =restTemplate.exchange(
                puppeteerUrl,
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        return response;
    }
    @PostMapping("/save/template")
    public ResponseEntity<String> saveTemplate(@RequestBody Templates templateInfo){
        return resumeService.saveTemplateInfo(templateInfo);
    }
    @GetMapping("/get/template/{templateName}")
    public ResponseEntity<TemplateRequestDTO> getTemplateByName(@PathVariable String templateName){
        return resumeService.getTemplateByName(templateName);
    }
    @GetMapping("/get/all/templates")
    public ResponseEntity<TemplatesListDTO> getAllTemplates(){
        return resumeService.getAllTemplates();

    }

}
