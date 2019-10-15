package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.SiteShow;
import com.yuyue.boss.api.domain.YuYueSite;


import java.util.List;


public interface YuYueSiteService {

    List<YuYueSite> getYuYueSiteInfo(String id);

    List<YuYueSite> searchYuYueSiteInfo(String siteAddr, String status,String jPushStatus,
                                        String mainPerson,String startTime,String endTime);
    //List<SiteShow> getSiteShowList(String siteId);

    void insertQRCodePath(String id ,String qrCodePath);

    void insertYuYueSite(YuYueSite yuYueSite);

    void updateYuYueSite(YuYueSite yuYueSite);

    void deleteYuYueSiteById(String id);

    void insertSiteShow(SiteShow siteShow);

    void updateSiteShow(SiteShow siteShow);
}
