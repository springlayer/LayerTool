package org.springlayer.core.boot.context;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Hzhi
 * @Date 2022/3/16 9:25
 * @description
 **/
@Component
public class CurrentUserHolder {

    /**
     * 获取当前用户
     *
     * @return CurrentUser
     */
    public static CurrentUser getCurrentUser() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String userStr = request.getHeader("user");
        JSONObject userJsonObject = new JSONObject(userStr);
        CurrentUser currentUser = new CurrentUser();
        currentUser.setUserId(Convert.toLong(userJsonObject.get("id")));
        currentUser.setUsername(Convert.toStr(userJsonObject.get("username")));
        currentUser.setLoginName(Convert.toStr(userJsonObject.get("loginName")));
        return currentUser;
    }
}