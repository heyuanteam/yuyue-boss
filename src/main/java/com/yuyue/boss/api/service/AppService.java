package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.AppVersion;
import com.yuyue.boss.api.domain.VideoCategory;

import java.util.List;

public interface AppService {

    //获取版本列表
    List<AppVersion> getVersionList(String systemType, String versionNo, String appVersionId);

    //添加版本
    void insertAppVersion(AppVersion appVersion);

    //修改版本
    void updateAppVersion(String appVersionId, String versionNo, String systemType, String programDescription, String status);

    //删除版本
    void delVersion(String appVersionId);

    //获取APP菜单
    List<VideoCategory> getAPPMenuList(String id, String category, String status, int number);

    //添加APP菜单
    void insertVideoCategory(VideoCategory videoCategory);

    //修改APP菜单
    void updateAPPMenu(String id, int sort, String status, String category,String url);

    //删除APP菜单
    void delAPPMenu(String id);


}
