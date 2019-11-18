package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.AdPrice;
import com.yuyue.boss.api.domain.MallShop;
import com.yuyue.boss.api.domain.ShopImage;
import com.yuyue.boss.api.domain.Specification;

import java.util.List;

public interface MallShopService {


/*---------------------------------商铺接口---------------------------------*/
    List<MallShop> getMallShopByVideoId(String videoId);

    MallShop getMyMallShop(String shopId);

    AdPrice getAdFee(String priceId);

    List<MallShop> getMyMallShops(String merchantId);

    //MallShop myMallShopInfo(String merchantId);
    //
    MallShop mallShopByShopId(String shopId);

    List<MallShop> getAllMallShop(String status, String content, String isRevise);

    void insertMyMallShop(MallShop mallShop);

    void updateMyMallShopInfo(MallShop mallShop);

    void updateMyMallShopStatus(String businessStatus, String shopId);

    List<ShopImage> getShopImage(String shopId);

    void insertShopImage(ShopImage shopImage);

    void deleteShopImage(String imagePath);

/*---------------------------------商品(规格)接口---------------------------------*/
    List<Specification> getSpecification(String shopId);

    Specification getSpecificationById(String specificationId);

    void insertSpecification(Specification specification);

    void deleteSpecification(String specificationId);

    void updateSpecification(Specification specification);



/*---------------------------------订单---------------------------------*/


//    List<OrderItem> getMallOrderItem(String orderId, String shopId, String status);
//
//
//    List<String> getOrderToItem(String shopId, String consumerId, String status);
//
//
//    void editMallOrderItem(OrderItem orderItem);
//
//    //修改订单项中的支付状态
//    void updateOrderItemsStatus(String orderId, String status);
//
//    //减库存及给商家们加钱的方法
//    void mallPaySuccess(String orderId);



}
