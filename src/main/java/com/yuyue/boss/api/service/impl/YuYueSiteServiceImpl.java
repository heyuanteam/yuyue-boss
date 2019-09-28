package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.mapper.YuYueSiteMapper;
import com.yuyue.boss.api.service.YuYueSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("YuYueSiteService")
public class YuYueSiteServiceImpl implements YuYueSiteService {


    @Autowired
    private YuYueSiteMapper yuYueSiteMapper;

    @Override
    public List<YuYueSite> getYuYueSiteInfo(String id,int begin,int limit) {
        return yuYueSiteMapper.getYuYueSiteInfo(id,begin,limit);
    }

    @Override
    public List<YuYueSite> searchYuYueSiteInfo(String siteAddr, String status, String jPushStatus, String mainPerson, String startTime, String endTime,int begin,int limit) {
        return yuYueSiteMapper.searchYuYueSiteInfo(siteAddr,status,jPushStatus,mainPerson,startTime,endTime,begin,limit);
    }

    @Override
    public void insertQRCodePath(String id, String qrCodePath) {
        yuYueSiteMapper.insertQRCodePath(id,qrCodePath);
    }
}
