package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.AdPrice;
import com.yuyue.boss.api.domain.Commodity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommodityService {
    void deleteCommodity(String id);

    Commodity getCommodityInfoById(String id);

    List<Commodity> getCommodityInfo( String commodityId,String commodityName, String category,String status,
                                     String startTime, String endTime);

    void updateCommodityInfo(Commodity commodity);

    List<Commodity> getReleaseCommodity(String videoId);

    public void insertCommodity(Commodity commodity);
}
