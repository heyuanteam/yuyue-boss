package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.ChangeMoney;

import java.util.List;

public interface ChangeMoneyService {

    List<ChangeMoney> getChangeMoneyList(String id, String changeNo, String sourceName, String tradeType,
                                         String mobile, String status, String note, String yiName,
                                         String startTime, String endTime);

    void updateChangeMoney(String id, String money, String tradeType, String note, String status, String mobile);

    void delOutMoney(String id);
}
