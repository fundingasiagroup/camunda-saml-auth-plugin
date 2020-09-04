package com.fundingsocieties.camunda.services;

import com.fundingsocieties.camunda.clients.ExternalIdentityServiceFeignClient;
import com.fundingsocieties.camunda.dto.RoleResponse;
import com.fundingsocieties.camunda.dto.UserResponse;
import com.fundingsocieties.camunda.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    @Autowired
    ExternalIdentityServiceFeignClient externalIdentityServiceFeignClient;

    public Group findById(String id) {
        Group group = new Group();
        group.setId(id);
        group.setName(id);
        return group;
    }

    public Collection<Group> findAll() {
        return externalIdentityServiceFeignClient
                .getRoles()
                .stream()
                .map(roleResponse -> fromRoleResponse(roleResponse))
                .collect(Collectors.toList());
    }

    private Group fromRoleResponse(RoleResponse roleResponse){
        Group group = new Group();
        group.setId(roleResponse.getRoleName());
        group.setName(roleResponse.getRoleName());
        group.setType("");
        return group;
    }

    public List<Group> getGroupsForUser(String userId){
        UserResponse userResponse = externalIdentityServiceFeignClient.getUserById(userId);
        return userResponse.getRoles().stream().map(role->fromString(role)).collect(Collectors.toList());
    }

    private Group fromString(String role){
        Group group = new Group();
        group.setId(role);
        group.setName(role);
        group.setType("");
        return group;
    }
}
