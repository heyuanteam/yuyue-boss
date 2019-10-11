package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.mapper.YuYueSiteMapper;
import com.yuyue.boss.api.service.YuYueSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
@Service("YuYueSiteService")
public class YuYueSiteServiceImpl implements YuYueSiteService {


    @Autowired
    private YuYueSiteMapper yuYueSiteMapper;

    /**
     * 获取现场信息(id可为空查所有；不为空查详情)
     * @param id;begin;limit
     *
     */
    @Override
    public List<YuYueSite> getYuYueSiteInfo(String id) {
        return yuYueSiteMapper.getYuYueSiteInfo(id);
    }
    /**
     * 通过搜索信息获取现场
     * @param siteAddr,status,jPushStatus,mainPerson,startTime,endTime,begin,limit
     */
    @Override
    public List<YuYueSite> searchYuYueSiteInfo(String siteAddr, String status, String jPushStatus, String mainPerson, String startTime, String endTime) {
        return yuYueSiteMapper.searchYuYueSiteInfo(siteAddr,status,jPushStatus,mainPerson,startTime,endTime);
    }

    /**
     * 将生成的二维码地址 存入数据库中
     * @param id
     * @param qrCodePath
     */
    @Override
    public void insertQRCodePath(String id, String qrCodePath) {
        yuYueSiteMapper.insertQRCodePath(id,qrCodePath);
    }

    /**
     * 添加新娱悦现场
     * @param yuYueSite
     */
    @Override
    public void insertYuYueSite(YuYueSite yuYueSite) {
        yuYueSiteMapper.insertYuYueSite(yuYueSite);
    }

    /**
     * 更新娱悦现场
     * @param yuYueSite
     */
    @Override
    public void updateYuYueSite(YuYueSite yuYueSite) {
        yuYueSiteMapper.updateYuYueSite(yuYueSite);
    }

    /**
     * 删除娱悦现场
     * @param id
     */
    @Override
    public void deleteYuYueSiteById(String id) {
        yuYueSiteMapper.deleteYuYueSiteById(id);
    }




}
