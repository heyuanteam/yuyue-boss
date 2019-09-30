package com.yuyue.boss.api.domain;

import com.yuyue.boss.api.domain.SystemUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Create by lujun.chen on 2018/09/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUserVO implements Serializable {
    private static final long serialVersionUID = 1L;
//    用户ID
    private String systemUserId;
//    登录名
    private String loginName;
//    真实姓名
    private String systemName;
//    最后登录时间
    private String createTime;
//    手机号
    private String phone;
//    用户是否启用
    private String systemUserStatus;
//    密码
    private String password;
//    权限ID
    private String systemRoleId;
//    权限状态
    private String systemRoleStatus;
//    菜单ID
    private String systemMeuId;
//    菜单是否启用
    private String systemMenuStatus;
//    菜单路径
    private String menuAction;
//    菜单Code
    private String menuCode;
//    菜单上级ID
    private String systemMeuPantId;
//    菜单名称
    private String menuName;
//    权限内容ID
    private String systemPermissionId;
//    权限内容名称
    private String permissionName;
//    权限内容Key
    private String permissionKey;
//    权限内容上级ID
    private String systemMeuPermissionPantId;
//    权限内容Code
    private String permissionCode;
//    权限内容路径
    private String permissionAction;
//    权限内容状态
    private String systemPermissionStatus;

}
