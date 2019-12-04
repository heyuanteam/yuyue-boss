package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.AppVersion;
import com.yuyue.boss.api.domain.Banner;
import com.yuyue.boss.api.domain.VideoCategory;

import java.util.List;

public interface AppService {

    //获取版本列表
    List<AppVersion> getVersionList(String systemType, String versionNo, String appVersionId);

    //添加版本
    void insertAppVersion(AppVersion appVersion);

    //修改版本
    void updateAppVersion(String appVersionId, String versionNo, String systemType, String programDescription, String status,String number);

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

    //查询轮播图
    List<Banner> getBannerList(String id, String name, String status, int sort);

    //添加轮播图
    void insertBanner(Banner banner);

    //修改轮播图
    void updateBanner(String id, int sort, String name, String status,String url);

    //删除轮播图
    void delBanner(String id);
}
