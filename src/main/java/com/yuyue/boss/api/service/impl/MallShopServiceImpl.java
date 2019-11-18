package com.yuyue.boss.api.service.impl;


import com.yuyue.boss.api.domain.AdPrice;
import com.yuyue.boss.api.domain.MallShop;
import com.yuyue.boss.api.domain.ShopImage;
import com.yuyue.boss.api.domain.Specification;
import com.yuyue.boss.api.mapper.AdPriceMapper;
import com.yuyue.boss.api.mapper.MallShopMapper;
import com.yuyue.boss.api.mapper.ShopImageMapper;
import com.yuyue.boss.api.mapper.SpecificationMapper;
import com.yuyue.boss.api.service.MallShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "mallShopServiceImpl")
public class MallShopServiceImpl implements MallShopService {

    @Autowired
    private MallShopMapper mallShopMapper;
    @Autowired
    private ShopImageMapper shopImageMapper;
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private AdPriceMapper adPriceMapper;

   /* @Autowired
    private MallOrderItemMapper mallOrderItemMapper;
    @Autowired
    private MallAddressMapper mallAddressMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private PayService payService;
    */


    @Override
    public List<MallShop> getMallShopByVideoId(String videoId) {
        return mallShopMapper.getMallShopByVideoId(videoId);
    }

    @Override
    public MallShop getMyMallShop(String shopId) {
        return mallShopMapper.getMyMallShop(shopId);
    }

    @Override
    public AdPrice getAdFee(String priceId) {
        return adPriceMapper.getAdFee(priceId);
    }

    @Override
    public List<MallShop> getMyMallShops(String merchantId) {
        return mallShopMapper.getMyMallShops(merchantId);
    }

    @Override
    public MallShop mallShopByShopId(String shopId) {
        return mallShopMapper.mallShopByShopId(shopId);
    }

    @Override
    public List<MallShop> getAllMallShop(String status, String content, String isRevise) {
        return mallShopMapper.getAllMallShop(status,content,isRevise);
    }



    @Override
    public void insertMyMallShop(MallShop mallShop) {
        mallShopMapper.insertMyMallShop(mallShop);
    }

    @Override
    public void updateMyMallShopInfo(MallShop mallShop) {
        mallShopMapper.updateMyMallShopInfo(mallShop);
    }

    @Override
    public void updateMyMallShopStatus(String businessStatus, String shopId) {
        mallShopMapper.updateMyMallShopStatus(businessStatus,shopId);
    }

    @Override
    public List<ShopImage> getShopImage(String shopId) {
        return shopImageMapper.getShopImage(shopId);
    }

    @Override
    public void insertShopImage(ShopImage shopImage) {
        shopImageMapper.insertShopImage(shopImage);
    }

    @Override
    public void deleteShopImage(String imagePath) {
        shopImageMapper.deleteShopImage(imagePath);
    }

    @Override
    public List<Specification> getSpecification(String shopId) {
        return specificationMapper.getSpecification(shopId);
    }

    @Override
    public Specification getSpecificationById(String specificationId) {
        return specificationMapper.getSpecificationById(specificationId);
    }

    @Override
    public void insertSpecification(Specification specification) {
        specificationMapper.insertSpecification(specification);
    }

    @Override
    public void deleteSpecification(String specificationId) {
        specificationMapper.deleteSpecification(specificationId);
    }

    @Override
    public void updateSpecification(Specification specification) {
        specificationMapper.updateSpecification(specification);
    }





    /*@Override
    public List<OrderItem> getMallOrderItem(String orderId, String shopId, String status) {
        return mallOrderItemMapper.getMallOrderItem(orderId,shopId,status);
    }

    @Override
    public List<String> getOrderToItem(String shopId,String consumerId,String status) {
        return mallOrderItemMapper.getOrderToItem(shopId,consumerId,status);
    }

    @Override
    public void editMallOrderItem(OrderItem orderItem) {
        mallOrderItemMapper.editMallOrderItem(orderItem);
    }

    @Override
    public void updateOrderItemsStatus(String orderId, String status) {
        mallOrderItemMapper.updateOrderItemsStatus(orderId,status);
    }*/
    //减库存及给商家们加钱的方法及修改订单项状态
//    @Override
//    public void mallPaySuccess(String orderId) {
//
//        //给商家们加钱的方法
//        List<String> shopIds = mallOrderItemMapper.getShopIds(orderId);
//        for (String shopId:shopIds
//             ) {
//            List<OrderItem> mallOrderItems = getMallOrderItem(orderId, shopId, "");
//            String money = null;
//            for (OrderItem orderItem:mallOrderItems
//                 ) {
//                //减库存操作
//                Specification specification = new Specification();
//                specification.setCommodityId(orderItem.getCommodityId());
//                specification.setCommodityNum(orderItem.getCommodityNum());
//                updateSpecification(specification);
//                money = orderItem.getShopIncome().toString();
//                updateOrderItemsStatus(orderId,"10B");
//            }
//            //给商家加钱
//            MallShop myMallShop = getMyMallShop(shopId);
//            String merchantId = myMallShop.getMerchantId();
//            AppUser appUserMsg = loginMapper.getAppUserMsg("", "", merchantId,"");
//            BigDecimal mIncome = ResultJSONUtils.updateMIncome(appUserMsg, new BigDecimal(money), "+");
//            //修改订单项状态
//            payService.updateMIncome(merchantId,mIncome);
//        }
//
//    }


}
