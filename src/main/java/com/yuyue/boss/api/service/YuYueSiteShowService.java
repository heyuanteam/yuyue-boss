package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.SiteShow;
import com.yuyue.boss.api.domain.YuYueSite;

import java.util.List;


public interface YuYueSiteShowService {

    List<SiteShow> getSiteShowList(String siteId);


    void updateSiteShow(SiteShow siteShow);


    void insertSiteShow(SiteShow siteShow);


    void deleteSiteShow(String id);

}
