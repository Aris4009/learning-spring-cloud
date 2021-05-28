package com.example.git;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource(value = "classpath:git.properties", ignoreResourceNotFound = true)
@Data
public class GitInfoConfig {
	@Value("${git.commit.id:null}")
	private String commitId;
}
