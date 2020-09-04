package com.fundingsocieties.camunda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String username;
    private String firstName;
    @JsonProperty("default_role")
    private String defaultRole;
    @JsonProperty("current_role")
    private String currentRole;
    private String country;
    private List<String> roles;
}
