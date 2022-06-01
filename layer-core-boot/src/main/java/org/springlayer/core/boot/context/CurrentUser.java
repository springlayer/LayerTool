package org.springlayer.core.boot.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

/**
 * @Author Hzhi
 * @Date 2022/3/16 9:19
 * @description
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class CurrentUser {
    private Long userId;
    private String username;
    private String loginName;
    //判断是否为管理员1
    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }
}
