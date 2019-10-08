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
//    手机号
    private String phone;
//    用户是否启用
    private String systemUserStatus;
//    权限ID
    private String systemPermissionId;
//    权限状态
    private String systemPermissionStatus;
//    查看权限
    private String menuKey;
//    保存权限
    private String saveKey;
//    删除权限
    private String removeKey;
    private String createTime;
    private String updateTime;
//    菜单ID
    private String systemMeuId;
//    菜单是否启用
    private String systemMenuStatus;
//    菜单路径
    private String menuAction;
//    菜单上级ID
    private String systemMeuPantId;
//    菜单名称
    private String menuName;

}
