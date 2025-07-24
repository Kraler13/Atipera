package com.Atipera.githubapi;

import com.Atipera.githubapi.exception.GitHubUserNotFoundException;
import com.Atipera.githubapi.model.Repository;
import com.Atipera.githubapi.model.RepositoryDTO;
import com.Atipera.githubapi.model.Branch;
import com.Atipera.githubapi.model.BranchDTO;
import com.Atipera.githubapi.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubService gitHubService;

    @Test
    public void testGetUserRepositories() {
        // Mockowanie odpowiedzi z GitHub API
        Repository mockRepo = new Repository();
        mockRepo.setName("repoName");
        Repository.Owner owner = new Repository.Owner();
        owner.setLogin("validUser");
        mockRepo.setOwner(owner);

        Branch mockBranch = new Branch();
        mockBranch.setName("main");
        mockBranch.setCommit(new Branch.Commit());
        mockBranch.getCommit().setSha("abc123");

        List<Repository> mockRepositories = List.of(mockRepo);
        List<Branch> mockBranches = List.of(mockBranch);

        // Mockowanie odpowiedzi z restTemplate dla repozytoriów
        when(restTemplate.getForObject(anyString(), eq(List.class), eq("validUser"))).thenReturn(mockRepositories);

        // Mockowanie odpowiedzi z restTemplate dla gałęzi
        ResponseEntity<List<Branch>> mockBranchResponse = ResponseEntity.ok(mockBranches);
        when(restTemplate.exchange(anyString(), eq(org.springframework.http.HttpMethod.GET), eq(null),
                eq(new org.springframework.core.ParameterizedTypeReference<List<Branch>>() {})))
            .thenReturn(mockBranchResponse);

        // Testowanie
        List<RepositoryDTO> repositories = gitHubService.getUserRepositories("validUser");

        // Assercje
        assertNotNull(repositories);
        assertEquals(1, repositories.size());
        assertEquals("validUser", repositories.get(0).getOwnerLogin());
        assertEquals("main", repositories.get(0).getBranches().get(0).getName());
    }

    @Test
    public void testGetUserRepositories_UserNotFound() {
        when(restTemplate.getForObject(anyString(), eq(List.class), eq("invalidUser")))
                .thenThrow(new GitHubUserNotFoundException("User not found"));

        assertThrows(GitHubUserNotFoundException.class, () -> {
            gitHubService.getUserRepositories("invalidUser");
        });
    }
}
