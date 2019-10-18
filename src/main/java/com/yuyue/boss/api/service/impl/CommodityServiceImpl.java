package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.AdPrice;
import com.yuyue.boss.api.domain.Commodity;
import com.yuyue.boss.api.mapper.AdPriceMapper;
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
    @Autowired
    private AdPriceMapper adPriceMapper;


    /**
     * 获取爆款详情
     */
    @Override
    public Commodity getCommodityInfoById(String id) {
        List<Commodity> commodityInfo = commodityMapper.getCommodityInfo(id, "", "", "", "", "");
        if (StringUtils.isNotEmpty(commodityInfo))return commodityInfo.get(0);
        else return null;
    }

    /**
     * 获取爆款列表及搜索
     */
    @Override
    public List<Commodity> getCommodityInfo( String commodityId,String commodityName, String category,String status,
                                            String startTime, String endTime) {
        return commodityMapper.getCommodityInfo(commodityId,commodityName,category,status,startTime,endTime);
    }
    /**
     * 删除爆款
     */
    @Override
    public void deleteCommodity(String id) {
        commodityMapper.deleteCommodityByPrimaryKey(id);
    }
    /**
     * 修改商品状态及发布时间
     */
    @Override
    public void updateCommodityInfo(Commodity commodity) {
        commodityMapper.updateCommodityInfo(commodity);
    }

    /**
     * 获取已投放的广告
     * @param videoId
     * @return
     */
    @Override
    public List<Commodity> getReleaseCommodity(String videoId) {
        return commodityMapper.getReleaseCommodity(videoId);
    }

//    @Override
//    public  List<AdPrice> getAdvertisementFeeInfo(String priceId){
//        return adPriceMapper.getAdvertisementFeeInfo(priceId);
//    }

    @Override
    public void insertCommodity(Commodity commodity) {
        commodityMapper.insertCommodity(commodity);
    }
}
