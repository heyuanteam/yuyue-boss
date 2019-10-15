package com.yuyue.boss.api.service;

import com.github.pagehelper.PageInfo;
import com.yuyue.boss.api.domain.*;

import java.util.List;


public interface LoginService {

    //获取系统用户个人信息以及权限
    UserVO getUser(String loginName, String password);

    //获取系统用户个人信息
    List<SystemUser> getSystemUserMsg(String loginName,String password,String id,String phone);

    //获取token
    String getToken(UserVO systemUser);

    //获取菜单列表
    List<SystemMenuVo> getMenuList(String loginName, String password);

    //获取菜单
    List<SystemMenu> getMenu(String id,Integer sort,String role,String menuName,String status);

    //插入菜单
    void insertSystemMenu(SystemMenu systemMenu);

    //修改菜单
    void updateSystemMenu(String id, int upSort,String status,String menuName);

    //插入权限
    void insertSystemPermission(String id, String systemUserId, String menuId, String menuKey, String saveKey, String removeKey);

    //删除菜单
    void delMenu(String id);

    //查询权限
    List<SystemPermission> getSystemPermission(String menuId,String systemUserId, String id);

    //删除权限
    void delSystemPermission(String id);

    //获取系统用户
    List<SystemUser> getSystemUser(String status,String systemName,String loginName,String id);

    //修改系统用户
    void updateSystemUser(String id, String loginName, String password, String systemName, String phone,String status);

    //删除系统用户
    void delSystemUser(String id);

    //获取分配系统权限详情
    List<SystemUserVO> getAppUserMsg(String loginName, String password);

    //添加系统用户
    void insertSystemUser(SystemUser user);

    //修改用户分配系统权限详情
    void updateSystemPermission(String id, String menuKey, String saveKey, String removeKey);

    //搜索字典
    List<LookupCde> getLookupCdeSystem(String status, String typeName, String id);

    //添加字典
    void insertLookupCde(LookupCde lookupCde);

    //修改字典
    void updateLookupCde(String id, String typeName, String status);

    //搜索字典下级
    List<LookupCdeConfig> getLookupCdeList(String systemId,String id);

    //添加字典下级
    void insertLookupCdeConfig(LookupCdeConfig lookupCdeConfig);

    //修改字典下级
    void updateLookupCdeList(String id, String type, String status);

    //删除字典下级
    void delLookupCdeList(String id);

    //删除字典
    void delLookupCdeSystem(String id);

    //获取版本列表
    List<AppVersion> getVersionList(String systemType, String versionNo,String appVersionId);

    //添加版本
    void insertAppVersion(AppVersion appVersion);

    //修改版本
    void updateAppVersion(String appVersionId, String versionNo, String systemType, String programDescription, String status);

    //删除版本
    void delVersion(String appVersionId);

    //获取公告列表
    List<JPush> getJPushList(String id,String msgTitle, String extras, String startTime, String endTime);

    //删除公告
    void delJPush(String id);

    //获取APP菜单
    List<VideoCategory> getAPPMenuList(String id,String category, String status);

    //添加APP菜单
    void insertVideoCategory(VideoCategory videoCategory);

    //修改APP菜单
    void updateAPPMenu(String id, int sort, String status, String category);

    //删除APP菜单
    void delAPPMenu(String id);
}
