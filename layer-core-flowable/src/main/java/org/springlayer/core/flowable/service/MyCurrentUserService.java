package org.springlayer.core.flowable.service;

import org.flowable.idm.api.*;
import org.flowable.ui.common.model.GroupRepresentation;
import org.flowable.ui.common.model.RemoteGroup;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.ui.common.security.CookieConstants;
import org.flowable.ui.common.security.FlowableAppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * @Author Hzhi
 * @Date 2022/3/08 11:11
 * @description 自定义当前用户服务
 **/
@Service
public class MyCurrentUserService {
    private static Logger logger = LoggerFactory.getLogger(MyCurrentUserService.class);

    @Resource
    private IdmIdentityService idmIdentityService;


    @Resource
    private PersistentTokenService persistentTokenService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserRepresentation initLoginUser(HttpServletRequest request, HttpServletResponse response) {
        UserRepresentation userRepresentation;
        User adminUser = idmIdentityService.createUserQuery().userId("admin").singleResult();
        if (adminUser != null) {
            userRepresentation = new UserRepresentation(adminUser);
            List<Privilege> privs = idmIdentityService.createPrivilegeQuery().userId("admin").list();
            if (!CollectionUtils.isEmpty(privs)) {
                for (Privilege item : privs) {
                    userRepresentation.getPrivileges().add(item.getName());
                }
            }
            List<Group> groups = idmIdentityService.createGroupQuery().groupMember("admin").list();
            if (!CollectionUtils.isEmpty(groups)) {
                for (Group item : groups) {
                    userRepresentation.getGroups().add(new GroupRepresentation(item));
                }
            }
        } else {
            User user = idmIdentityService.newUser("admin");
            user.setFirstName("Hz");
            user.setLastName("Administrator");
            user.setDisplayName("Administrator");
            user.setEmail("admin@flowable.org");
            user.setPassword("test");
            idmIdentityService.saveUser(user);

            List<String> privs = Arrays.asList("access-idm", "access-admin", "access-modeler", "access-task", "access-rest-api");
            privs.forEach(item -> {
                Privilege priv = idmIdentityService.createPrivilege(item);
                idmIdentityService.addUserPrivilegeMapping(priv.getId(), user.getId());
            });

            userRepresentation = new UserRepresentation(user);
            userRepresentation.setPrivileges(privs);
        }

        // 登录操作
        login(userRepresentation, request, response);

        return userRepresentation;
    }

    protected void login(UserRepresentation user, HttpServletRequest request, HttpServletResponse response) {
        // 转换成 flowable中的用户并且登录
        RemoteUser remoteUser = new RemoteUser();
        remoteUser.setId(user.getId());
        remoteUser.setFirstName(user.getFirstName());
        remoteUser.setLastName(user.getLastName());
        remoteUser.setFullName(user.getFullName());
        remoteUser.setDisplayName(user.getLastName());
        remoteUser.setEmail(user.getEmail());
        remoteUser.setTenantId(user.getTenantId());
        remoteUser.setPrivileges(user.getPrivileges());

        List<GroupRepresentation> groupRepresentations = user.getGroups();
        List<RemoteGroup> remoteGroups = new ArrayList<>(2);
        for (GroupRepresentation group : groupRepresentations) {
            RemoteGroup remoteGroup = new RemoteGroup();
            remoteGroup.setId(group.getId());
            remoteGroup.setName(group.getName());
            remoteGroup.setType(group.getType());
            remoteGroups.add(remoteGroup);
        }
        remoteUser.setGroups(remoteGroups);

        Token token = persistentTokenService.createToken(remoteUser, request.getRemoteAddr(), request.getHeader("User-Agent"));
        addCookie(token, request, response);

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String privilege : remoteUser.getPrivileges()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(privilege));
        }
        // put account into security context (for controllers to use)
        FlowableAppUser appUser = new FlowableAppUser(remoteUser, remoteUser.getId(), grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(new RememberMeAuthenticationToken(token.getId(),
                appUser, appUser.getAuthorities()));
        logger.info("flowable当前操作用户 {} 登录成功...", user.getId());
    }

    protected void addCookie(Token token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[]{token.getId(), token.getTokenValue()}, request, response);
    }

    protected void setCookie(String[] tokens, HttpServletRequest request, HttpServletResponse response) {
        String https = "https";
        String cookieValue = encodeCookie(tokens);
        Cookie cookie = new Cookie(CookieConstants.COOKIE_NAME, cookieValue);
        cookie.setMaxAge(2678400);
        cookie.setPath("/");

        String xForwardedProtoHeader = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProtoHeader != null) {
            cookie.setSecure(xForwardedProtoHeader.equals(https) || request.isSecure());
        } else {
            cookie.setSecure(request.isSecure());
        }

        Method setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class, "setHttpOnly", boolean.class);
        if (setHttpOnlyMethod != null) {
            ReflectionUtils.invokeMethod(setHttpOnlyMethod, cookie, Boolean.TRUE);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Note: Cookie will not be marked as HttpOnly because you are not using Servlet 3.0 (Cookie#setHttpOnly(boolean) was not found).");
        }

        response.addCookie(cookie);
    }

    /**
     * Inverse operation of decodeCookie.
     *
     * @param cookieTokens the tokens to be encoded.
     * @return base64 encoding of the tokens concatenated with the ":" delimiter.
     */
    protected String encodeCookie(String[] cookieTokens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cookieTokens.length; i++) {
            try {
                sb.append(URLEncoder.encode(cookieTokens[i], StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }

            if (i < cookieTokens.length - 1) {
                sb.append(":");
            }
        }

        String value = sb.toString();

        sb = new StringBuilder(new String(Base64.getEncoder().encode(value.getBytes())));
        char dh = '=';
        while (sb.charAt(sb.length() - 1) == dh) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
