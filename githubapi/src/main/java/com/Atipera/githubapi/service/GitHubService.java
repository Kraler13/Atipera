package com.Atipera.githubapi.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.Atipera.githubapi.model.RepositoryDTO;
import com.Atipera.githubapi.model.Repository;
import com.Atipera.githubapi.model.BranchDTO;
import com.Atipera.githubapi.model.Branch;
import com.Atipera.githubapi.exception.GitHubUserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private static final String GITHUB_API_URL = "https://api.github.com/users/{username}/repos";

    private final RestTemplate restTemplate;

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryDTO> getUserRepositories(String username) {
        try {
            String url = GITHUB_API_URL;
            List<Repository> repositories = restTemplate.getForObject(url, List.class, username);

            // Check if the response is empty or null
            if (repositories == null || repositories.isEmpty()) {
                throw new GitHubUserNotFoundException("No repositories found for user: " + username);
            }

            // Mapping repositories and filtering forks
            return repositories.stream()
                    .filter(repo -> !repo.isFork()) // Exclude forked repositories
                    .map(repo -> new RepositoryDTO(
                            repo.getName(),
                            repo.getOwner().getLogin(),
                            fetchBranches(repo.getName(), username)))
                    .collect(Collectors.toList());

        } catch (HttpClientErrorException.NotFound e) {
            throw new GitHubUserNotFoundException("User not found: " + username);
        }
    }

    private List<BranchDTO> fetchBranches(String repoName, String username) {
        String branchUrl = "https://api.github.com/repos/{username}/{repoName}/branches";

        // Użyj ResponseEntity, aby sprawdzić, czy odpowiedź jest null
        ResponseEntity<List<Branch>> response = restTemplate.exchange(
                branchUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Branch>>() {
                });

        // Jeżeli odpowiedź jest null, zwróć pustą listę
        List<Branch> branches = response.getBody();
        if (branches == null) {
            branches = new ArrayList<>(); // Zwracamy pustą listę, aby uniknąć NullPointerException
        }

        return branches.stream()
                .map(branch -> new BranchDTO(branch.getName(), branch.getCommit().getSha()))
                .collect(Collectors.toList());
    }

}