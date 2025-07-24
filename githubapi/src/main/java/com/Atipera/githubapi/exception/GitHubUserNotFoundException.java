package com.Atipera.githubapi.exception;

public class GitHubUserNotFoundException extends RuntimeException {
    public GitHubUserNotFoundException(String message) {
        super(message);
    }
}
