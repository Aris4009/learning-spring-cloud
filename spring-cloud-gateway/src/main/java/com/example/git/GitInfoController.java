package com.example.git;

import org.springframework.web.bind.annotation.RequestMapping;

import com.example.response.entity.Response;

//@RestController
//@RequestMapping("/api/v1/git")
public class GitInfoController {

	private final GitInfoConfig gitInfoConfig;

	public GitInfoController(GitInfoConfig gitInfoConfig) {
		this.gitInfoConfig = gitInfoConfig;
	}

	@RequestMapping("/info")
	public Response<String> gitInfo() {
		return Response.ok(this.gitInfoConfig.getCommitId());
	}
}
