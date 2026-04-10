package com.resume.resume_service_registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ResumeServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeServiceRegistryApplication.class, args);
	}

}
