package com.yuyue.boss.api.service;


import com.yuyue.boss.api.domain.JPush;

import java.util.List;

public interface SendService {
    List<JPush> getValid();

    void updateValid(String status, String id);
}
