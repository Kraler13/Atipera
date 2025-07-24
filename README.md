\# GitHub Repositories API



This is a Spring Boot application that allows API consumers to list all non-fork GitHub repositories of a given user.



\## Endpoints



\### GET /api/github/repositories/{username}



Fetches a list of non-fork repositories for a given GitHub user. The response includes the repository name, owner login, and for each branch, the branch name and last commit SHA.



\#### Response:

\- 200 OK: A list of repositories

\- 404 Not Found: If the user does not exist



\### Example



\*\*Request:\*\*



GET /api/github/repositories/octocat



\*\*Response:\*\*



```json

\[

&nbsp;   {

&nbsp;       "name": "Hello-World",

&nbsp;       "ownerLogin": "octocat",

&nbsp;       "branches": \[

&nbsp;           {

&nbsp;               "name": "main",

&nbsp;               "lastCommitSha": "e5a1f5d"

&nbsp;           }

&nbsp;       ]

&nbsp;   }

]

