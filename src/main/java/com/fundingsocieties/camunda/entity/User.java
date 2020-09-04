package com.fundingsocieties.camunda.entity;

import lombok.Data;

import java.util.List;

@Data
public class User implements org.camunda.bpm.engine.identity.User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Group> group;
}
