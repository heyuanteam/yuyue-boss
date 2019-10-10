package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.JPush;
import com.yuyue.boss.api.mapper.SendMapper;
import com.yuyue.boss.api.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "SendSmsService")
public class SendServiceImpl implements SendService {

    @Autowired
    private SendMapper sendMapper;

    @Override
    public List<JPush> getValid() { return sendMapper.getValid(); }

    @Override
    public void updateValid(String status, String id) { sendMapper.updateValid(status,id); }

}
