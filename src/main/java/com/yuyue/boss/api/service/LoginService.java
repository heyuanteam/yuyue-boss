package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemPermission;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.UserVO;

import java.util.List;


public interface LoginService {

    //获取系统用户个人信息以及权限
    UserVO getUser(String loginName, String password);

    //获取系统用户个人信息
    SystemUser getSystemUserMsg(String loginName,String password,String id,String phone);

    //获取token
    String getToken(UserVO systemUser);

    //获取菜单列表
    List<SystemMenu> getMenuList(String loginName, String password);

    //获取菜单
    List<SystemMenu> getMenu(String id,Integer sort,String role,String menuName,String status);

    //插入菜单
    void insertSystemMenu(SystemMenu systemMenu);

    //修改菜单
    void updateSystemMenu(String id, int upSort,String status,String menuName);

    //插入权限
    void insertSystemPermission(String id, String permissionName, String permissionKey, String parentId, String permissionCode);

    //删除菜单
    void delMenu(String id);

    //查询权限
    List<SystemPermission> getSystemPermission(String parentId, String permissionCode);

    //删除权限
    void delSystemPermission(String id);
}
