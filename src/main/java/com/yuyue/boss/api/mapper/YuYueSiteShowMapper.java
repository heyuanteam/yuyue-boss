package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.Advertisement;
import com.yuyue.boss.api.domain.SiteShow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface YuYueSiteShowMapper extends MyBaseMapper<SiteShow> {

    List<SiteShow> getSiteShowList(@Param(value = "siteId") String siteId);


    void updateSiteShow(SiteShow siteShow);


    void insertSiteShow(SiteShow siteShow);


    void deleteSiteShow(String siteId);
}
