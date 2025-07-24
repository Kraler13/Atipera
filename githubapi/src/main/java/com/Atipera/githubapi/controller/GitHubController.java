package com.Atipera.githubapi.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.Atipera.githubapi.errors.ErrorResponse;
import com.Atipera.githubapi.exception.GitHubUserNotFoundException;
import com.Atipera.githubapi.service.GitHubService;

@RestController
@RequestMapping("/api/github")
public class GitHubController {
        private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/repositories/{username}")
    public ResponseEntity<?> getRepositories(@PathVariable String username) {
        try {
            return ResponseEntity.ok(gitHubService.getUserRepositories(username));
        } catch (GitHubUserNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, "User not found"));
        }
    }
}