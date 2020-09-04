package com.fundingsocieties.camunda.clients;

import com.fundingsocieties.camunda.dto.RoleResponse;
import com.fundingsocieties.camunda.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This is a feign client to get users and groups data from your own identity store service
 * you can change it as per your requirement.
 */
@FeignClient(value = "EXTENAL-IDENTITY-SERVICE", url = "http://127.0.0.1:8080/")
public interface ExternalIdentityServiceFeignClient {

    @RequestMapping(path = "/users/all", method = RequestMethod.GET)
    List<UserResponse> getUsers();

    @RequestMapping(path = "/roles/all", method = RequestMethod.GET)
    List<RoleResponse> getRoles();

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    UserResponse getUserById(@RequestHeader("username") String userId);

    @RequestMapping(path = "/roles/{role_name}/all_users", method = RequestMethod.GET)
    List<UserResponse> getUsersForRole(@PathVariable("role_name") String groupId);
}
