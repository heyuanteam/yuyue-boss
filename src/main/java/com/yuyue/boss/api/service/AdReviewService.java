package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.Advertisement;

import java.util.List;

public interface AdReviewService {

    List<Advertisement> getAdReviewList(String merchantName,String phone,String status,String applicationStartTime,String applicationEndTime,int begin,int limit);

    void updateAdReviewStatus(String id,String status);
}
