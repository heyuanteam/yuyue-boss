package com.yuyue.boss.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commodity implements Serializable {
    private static final long serialVersionUID = 1L;
    //商品id
    private String commodityId;
    //订单ID
    private String orderId;
    //广告价格ID
    private String priceId;
    //商品种类
    private String category;
    //商品名
    private String commodityName;
    //广告词
    private String adWord;
    //广告音频图片
    private String adImageUrl;
    //商品价格
    private BigDecimal commodityPrice;
    //购买链接
    private String payUrl;
    //地址
    private String addr;
    //代言人id
    private String spokesPersonId;
    //视频id
    private String videoId;
    //商家id
    private String merchantId;
    //申请时间
   // @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private String applicationTime;
    //推广开始时间
    //@JSONField(format="yyyy-MM-dd HH:mm:ss")
    private String startDate;
    //结束时间
   // @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private String endDate;
    //广告状态
    private String status;
    //价格
    private AdPrice adPrice;
    //用户
    private AppUser appUser;
    //公司
    private Advertisement advertisement;
}
