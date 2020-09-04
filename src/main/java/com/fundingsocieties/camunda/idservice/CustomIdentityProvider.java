package com.fundingsocieties.camunda.idservice;

import com.fundingsocieties.camunda.services.GroupService;
import com.fundingsocieties.camunda.services.UserService;
import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.identity.*;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Service
public class CustomIdentityProvider implements ReadOnlyIdentityProvider {

    private UserService userService;

    private GroupService groupService;

    @Autowired
    public CustomIdentityProvider(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }


    // User ////////////////////////////////////////////

    @Override
    public User findUserById(String userId) {
        return userService.findById(userId);
    }

    @Override
    public UserQuery createUserQuery() {
        return new CustomUserQuery(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public UserQuery createUserQuery(CommandContext commandContext) {
        return new CustomUserQuery();
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        throw new BadUserRequestException("not supported");
    }

    public long findUserCountByQueryCriteria(CustomUserQuery query) {
        return findUserByQueryCriteria(query).size();
    }

    public List<User> findUserByQueryCriteria(CustomUserQuery query) {

        Collection<com.fundingsocieties.camunda.entity.User> users = userService.findAll();
        if (query.getGroupId() != null) {
            return new ArrayList<>(userService.findByGroupId(query.getGroupId()));
        }
        if (query.getId() != null)
            users.removeIf(user -> !user.getId().equals(query.getId()));
        if (query.getFirstName() != null)
            users.removeIf(user -> !user.getFirstName().equalsIgnoreCase(query.getFirstName()));
        if (query.getLastName() != null)
            users.removeIf(user -> !user.getLastName().equals(query.getLastName()));
        if (query.getEmail() != null)
            users.removeIf(user -> !user.getEmail().equals(query.getEmail()));
        return new ArrayList<>(users);

    }


    @Override
    public boolean checkPassword(String userId, String password) {

        if (userId == null || password == null || userId.isEmpty() || password.isEmpty())
            return false;

        User user = findUserById(userId);

        if (user == null)
            return false;

        return user.getPassword().equals(password);
    }

    // Group //////////////////////////////////////////

    @Override
    public Group findGroupById(String groupId) {
        return groupService.findById(groupId);
    }

    @Override
    public GroupQuery createGroupQuery() {
        return new CustomGroupQuery(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public GroupQuery createGroupQuery(CommandContext commandContext) {
        return new CustomGroupQuery();
    }

    public long findGroupCountByQueryCriteria(CustomGroupQuery query) {
        return findGroupByQueryCriteria(query).size();
    }

    public List<Group> findGroupByQueryCriteria(CustomGroupQuery query) {
        Collection<com.fundingsocieties.camunda.entity.Group> groups = groupService.findAll();
        if (query.getUserId() != null) {
            List<com.fundingsocieties.camunda.entity.Group> userGroups = groupService.getGroupsForUser(query.getUserId());
            return new ArrayList<>(userGroups);
        } else {
            if (query.getId() != null)
                groups.removeIf(group -> !group.getId().equals(query.getId()));
            if (query.getName() != null)
                groups.removeIf(group -> !group.getName().equals(query.getName()));
            if (query.getType() != null)
                groups.removeIf(group -> !group.getType().equals(query.getType()));
        }
        return new ArrayList<>(groups);
    }

    // Tenant ////////////////////////////////////////

    @Override
    public Tenant findTenantById(String tenantId) {
        return null;
    }

    @Override
    public TenantQuery createTenantQuery() {
        return new CustomTenantQuery(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public TenantQuery createTenantQuery(CommandContext commandContext) {
        return new CustomTenantQuery();
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }
}
