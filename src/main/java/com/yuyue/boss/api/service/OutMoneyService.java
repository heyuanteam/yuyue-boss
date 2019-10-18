package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.OutMoney;

import java.util.List;

public interface OutMoneyService {

    List<OutMoney> getOutMoneyList(String id, String tradeType, String status, String realName,
                                   String startTime, String endTime, String outNo,String userName);

    void updateOutMoney(String id, String tradeType, String money, String status, String realName);

    void delOutMoney(String id);
}
