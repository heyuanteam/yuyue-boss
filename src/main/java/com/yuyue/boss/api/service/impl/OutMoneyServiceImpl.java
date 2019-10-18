package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.OutMoney;
import com.yuyue.boss.api.mapper.OutMoneyMapper;
import com.yuyue.boss.api.service.OutMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "OutMoneyService")
public class OutMoneyServiceImpl implements OutMoneyService {

    @Autowired
    private OutMoneyMapper outMoneyMapper;

    @Override
    public List<OutMoney> getOutMoneyList(String id, String tradeType, String status, String realName,
                                          String startTime, String endTime, String outNo,String userName) {
        return outMoneyMapper.getOutMoneyList(id, tradeType,status,realName,startTime,endTime,outNo,userName);
    }

    @Override
    public void updateOutMoney(String id, String tradeType, String money, String status, String realName) {
        outMoneyMapper.updateOutMoney(id, tradeType,money,status,realName);
    }

    @Override
    public void delOutMoney(String id) { outMoneyMapper.delOutMoney(id);}
}
