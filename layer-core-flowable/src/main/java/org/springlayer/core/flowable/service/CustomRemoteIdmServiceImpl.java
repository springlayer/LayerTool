package org.springlayer.core.flowable.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.*;
import org.flowable.ui.common.model.RemoteGroup;
import org.flowable.ui.common.model.RemoteToken;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.NotFoundException;
import org.flowable.ui.common.service.idm.RemoteIdmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author Hzhi
 * @Date 2022/3/07 10:19
 * @description
 **/
@Primary
@Service
public class CustomRemoteIdmServiceImpl implements RemoteIdmService {


    private static Logger logger = LoggerFactory.getLogger(CustomRemoteIdmServiceImpl.class);

    @Resource
    private IdmIdentityService idmIdentityService;

    @Resource
    private IdentityService identityService;

    private static final int MAX_USER_SIZE = 100;

    public void deleteTimeOutToken() {
        Date limitDate = DateUtils.addDays(new Date(), -1);
        logger.info(" ======= 准备删除创建日期在" + limitDate.toString() + "之前的token数据 ======= ");
        List<Token> tokens = idmIdentityService.createTokenQuery().tokenDateBefore(limitDate).list();
        if (CollectionUtils.isEmpty(tokens)) {
            return;
        }
        tokens.forEach(item -> idmIdentityService.deleteToken(item.getId()));
    }

    @Override
    public RemoteUser authenticateUser(String username, String password) {
        RemoteUser remoteUser = getUser(username);
        if (password == null || !password.equals(remoteUser.getPassword())) {
            return null;
        } else {
            return remoteUser;
        }
    }

    @Override
    public RemoteToken getToken(String tokenValue) {
        Token token = idmIdentityService.createTokenQuery().tokenId(tokenValue).singleResult();
        if (token == null) {
            throw new NotFoundException();
        } else {
            RemoteToken remoteToken = new RemoteToken();
            remoteToken.setValue(token.getTokenValue());
            remoteToken.setUserId(token.getUserId());
            remoteToken.setId(token.getId());
            return remoteToken;
        }
    }

    @Override
    public RemoteUser getUser(String userId) {
        User user = identityService.createUserQuery().userId(userId).singleResult();
        if (user == null) {
            throw new NotFoundException();
        }

        RemoteUser remoteUser = getRemoteUser(user);

        List<Privilege> userPrivileges = idmIdentityService.createPrivilegeQuery().userId(userId).list();
        List<String> privilegeNames = Lists.newArrayList();
        for (Privilege userPrivilege : userPrivileges) {
            privilegeNames.add(userPrivilege.getName());
        }

        List<Group> groups = identityService.createGroupQuery().groupMember(userId).list();
        if (!CollectionUtils.isEmpty(groups)) {
            List<String> groupIds = new ArrayList<>();
            for (Group group : groups) {
                groupIds.add(group.getId());
                remoteUser.getGroups().add(getRemoteGroup(group));
            }

            List<Privilege> groupPrivileges = idmIdentityService.createPrivilegeQuery().groupIds(groupIds).list();
            for (Privilege groupPrivilege : groupPrivileges) {
                privilegeNames.add(groupPrivilege.getName());
            }
        }
        remoteUser.setPrivileges(privilegeNames);

        return remoteUser;
    }

    @Override
    public List<RemoteUser> findUsersByNameFilter(String filter) {
        List<RemoteUser> remoteUserList = Lists.newArrayList();
        UserQuery userQuery = identityService.createUserQuery();
        if (StringUtils.isNotEmpty(filter)) {
            userQuery.userFullNameLikeIgnoreCase("%" + filter + "%");
        }
        String tenantId = SecurityUtils.getCurrentUserObject().getTenantId();
        if (StringUtils.isNotBlank(tenantId)) {
            userQuery.tenantId(tenantId);
        }
        List<User> userList = userQuery.listPage(0, MAX_USER_SIZE);
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                remoteUserList.add(getRemoteUser(user));
            }
        }
        return remoteUserList;
    }

    @Override
    public List<RemoteUser> findUsersByGroup(String groupId) {
        List<User> userList = identityService.createUserQuery().memberOfGroup(groupId).list();
        List<RemoteUser> remoteUserList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                remoteUserList.add(getRemoteUser(user));
            }
        }
        return remoteUserList;
    }

    @Override
    public RemoteGroup getGroup(String groupId) {
        Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
        return getRemoteGroup(group);
    }

    @Override
    public List<RemoteGroup> findGroupsByNameFilter(String filter) {
        List<RemoteGroup> remoteGroupList = Lists.newArrayList();
        User user = SecurityUtils.getCurrentUserObject();
        String sql;
        List<Group> groupList = Lists.newArrayList();
        if (StringUtils.isNotBlank(filter)) {
            filter = "%" + filter + "%";
            sql = "select ID_,NAME_,TYPE_ from act_id_group where NAME_ like #{nameLike}";
            groupList = idmIdentityService.createNativeGroupQuery().sql(sql).parameter("nameLike", filter).listPage(0, MAX_USER_SIZE);
        }
        if (!CollectionUtils.isEmpty(groupList)) {
            for (Group group : groupList) {
                remoteGroupList.add(getRemoteGroup(group));
            }
        }
        return remoteGroupList;
    }


    private RemoteGroup getRemoteGroup(Group group) {
        RemoteGroup remoteGroup = null;
        if (group != null) {
            remoteGroup = new RemoteGroup();
            remoteGroup.setType(group.getType());
            remoteGroup.setName(group.getName());
            remoteGroup.setId(group.getId());
        }

        return remoteGroup;
    }

    private RemoteUser getRemoteUser(User user) {
        RemoteUser remoteUser = null;
        if (user != null) {
            remoteUser = new RemoteUser();
            remoteUser.setId(user.getId());
            remoteUser.setFirstName(user.getFirstName());
            remoteUser.setLastName(user.getLastName());
            remoteUser.setDisplayName(user.getDisplayName());
            remoteUser.setEmail(user.getEmail());
            remoteUser.setTenantId(user.getTenantId());
        }
        return remoteUser;
    }

}
