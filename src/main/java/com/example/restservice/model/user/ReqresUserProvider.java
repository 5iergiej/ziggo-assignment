package com.example.restservice.model.user;

import com.example.restservice.model.user.exceptions.UserException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
@Repository
public class ReqresUserProvider {

    private static final String USERS_URI = "https://reqres.in/api/users";
    private static final RestTemplate restTemplate = new RestTemplateBuilder().build();

    private List<User> users = null;

    public List<User> getUsers() {
        try {
            if (Objects.isNull(users)) {
                users = Objects.requireNonNull(restTemplate.getForObject(USERS_URI, ReqresUsersResponse.class))
                    .data.stream().map(user -> new User(user.email(), user.firstName(), user.lastName()))
                    .toList();
            }

            return users;
        } catch (Exception e) {
            throw new UserException("Failed to fetch users information");
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return getUsers().stream().filter(u -> u.email().equals(email)).findFirst();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ReqresUser(
        @JsonProperty("id")
        Integer id,

        @JsonProperty("email")
        String email,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName,

        @JsonProperty("avatar")
        URI avatar
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ReqresUsersResponse(
        Integer page,
        Integer perPage,
        Integer total,
        Integer totalPages,
        List<ReqresUser> data
    ) {
    }
}

