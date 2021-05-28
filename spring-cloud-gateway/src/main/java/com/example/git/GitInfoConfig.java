package com.example.git;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

//@Configuration
//@PropertySource(value = "classpath:git.properties")
@Data
public class GitInfoConfig {
	@Value("${git.commit.id}")
	private String commitId;
}
