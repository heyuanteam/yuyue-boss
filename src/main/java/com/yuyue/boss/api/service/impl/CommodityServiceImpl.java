package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.Commodity;
import com.yuyue.boss.api.mapper.CommodityMapper;
import com.yuyue.boss.api.service.CommodityService;
import com.yuyue.boss.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static javax.swing.UIManager.get;

@Service(value = "/CommodityService")
public class CommodityServiceImpl implements CommodityService {


    @Autowired
    private CommodityMapper commodityMapper;


    @Override
    public Commodity getCommodityInfoById(String id) {
        List<Commodity> commodityInfo = commodityMapper.getCommodityInfo(id, "", "", "", "", "");
        if (StringUtils.isNotEmpty(commodityInfo))return commodityInfo.get(0);
        else return null;
    }

    @Override
    public List<Commodity> getCommodityInfo( String commodityId,String commodityName, String category,String status,
                                            String startTime, String endTime) {
        return commodityMapper.getCommodityInfo(commodityId,commodityName,category,status,startTime,endTime);
    }

    @Override
    public void deleteCommodity(String id) {
        commodityMapper.deleteCommodityByPrimaryKey(id);
    }

    @Override
    public void updateCommodityInfo(Commodity commodity) {
        commodityMapper.updateCommodityInfo(commodity);
    }
}
