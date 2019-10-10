package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.JPush;

import java.util.List;

public interface SendService {
    //极光获取
    List<JPush> getValid();

    //极光更新
    void updateValid(String status, String id);

    //极光插入
    void insertJPush(JPush jPush);
}
