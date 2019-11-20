package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.MallShop;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MallShopMapper extends MyBaseMapper<MallShop> {

    @Select("SELECT * FROM yuyue_mall_shop WHERE video_id = #{videoId}  ")
    List<MallShop> getMallShopByVideoId(@Param(value = "videoId") String videoId);


    //获取我的商铺包括图片
    MallShop getMyMallShop(@Param(value = "shopId") String shopId);

    //获取商铺列表
    List<MallShop> getAllMallShop(@Param(value = "status") String status,
                                  @Param(value = "content") String content,
                                  @Param(value = "isRevise") String isRevise);

    @Select("SELECT * FROM yuyue_mall_shop WHERE  business_status = #{businessStatus} and status = '10C' and is_revise = 'N'")
    List<MallShop> getShopByBusinessStatus(String businessStatus);

    @Select("SELECT * FROM yuyue_mall_shop WHERE merchant_id = #{merchantId}  ")
    List<MallShop> getMyMallShops(@Param(value = "merchantId") String merchantId);

//    @Select("SELECT * FROM yuyue_mall_shop WHERE merchant_id = #{merchantId} LIMIT 1 ")
//    MallShop myMallShopInfo(@Param(value = "merchantId") String merchantId);

    //只获取信息不含其他
    @Select("SELECT * FROM yuyue_mall_shop WHERE shop_id = #{shopId}  ")
    MallShop mallShopByShopId(@Param(value = "shopId") String shopId);

    @Transactional
    void insertMyMallShop(MallShop mallShop);

    @Transactional
    void updateMyMallShopInfo(MallShop mallShop);

    @Transactional
    @Update("update yuyue_mall_shop set `business_status` = #{businessStatus} where shop_id = #{shopId} ")
    void updateMyMallShopStatus(@Param(value = "businessStatus") String businessStatus, @Param(value = "shopId") String shopId);
}
