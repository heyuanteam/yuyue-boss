package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
//    订单号
    private String orderNo;
//    交易类型
    private String tradeType;
//    交易金额
    private BigDecimal money;
//    手机号
    private String mobile;
//    交易状态
    private String status;
//    字典状态code
    private String statusCode;
//    交易时间
    private String createTime;
//    完成时间
    private String completeTime;
//    备注
    private String note;
//    支付返回码
    private String responseCode;
//    支付返回详情
    private String responseMessage;
//    商户ID
    private String merchantId;
//    收礼物的那个人
    private String sourceId;

    //查询所用到的字段
    private String startTime;

    private String endTime;
    //用户信息
    private String realName;
    //用户类型
    private String userType;
    //订单类型
//    private List<String> typeList;
    //订单类型
    private String type;
    //订单收货地址
    private MallAddress mallAddress;

}
