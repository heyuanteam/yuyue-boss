package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.Advertisement;
import com.yuyue.boss.api.mapper.AdReviewMapper;
import com.yuyue.boss.api.service.AdReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "AdReviewService")
public class AdReviewServiceImpl implements AdReviewService {

    @Autowired
    private AdReviewMapper adReviewMapper;
    @Override
    public List<Advertisement> getAdReviewList(String merchantName, String phone, String status, String applicationStartTime,String applicationEndTime, int begin, int limit) {
        return adReviewMapper.getAdReviewList(merchantName,phone,status, applicationStartTime,applicationEndTime,begin,limit);
    }

    @Override
    public void updateAdReviewStatus(String id, String status) {
        adReviewMapper.updateAdReviewStatus(id,status);
    }
}
