package com.yuyue.boss.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.mapper.AppMapper;
import com.yuyue.boss.api.mapper.LoginMapper;
import com.yuyue.boss.api.mapper.LookupCdeMapper;
import com.yuyue.boss.api.service.AppService;
import com.yuyue.boss.api.service.LookupCdeService;
import com.yuyue.boss.utils.BeanUtil;
import com.yuyue.boss.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ly
 */
@Service(value = "LookupCdeService")
public class LookupCdeServiceImpl implements LookupCdeService {

    @Autowired
    private LookupCdeMapper lookupCdeMapper;

    @Override
    public List<LookupCde> getLookupCdeSystem(String status, String typeName, String id) {
        return lookupCdeMapper.getLookupCdeSystem(status,typeName,id); }

    @Override
    public void insertLookupCde(LookupCde lookupCde) { lookupCdeMapper.insertLookupCde(lookupCde); }

    @Override
    public void updateLookupCde(String id, String typeName, String status) {
        lookupCdeMapper.updateLookupCde(id,typeName,status); }

    @Override
    public List<LookupCdeConfig> getLookupCdeList(String systemId,String id) {
        return lookupCdeMapper.getLookupCdeList(systemId,id); }

    @Override
    public void insertLookupCdeConfig(LookupCdeConfig lookupCdeConfig) {
        lookupCdeMapper.insertLookupCdeConfig(lookupCdeConfig); }

    @Override
    public void updateLookupCdeList(String id, String type, String status) {
        lookupCdeMapper.updateLookupCdeList(id,type,status); }

    @Override
    public void delLookupCdeList(String id) {
        lookupCdeMapper.delLookupCdeList(id); }

    @Override
    public void delLookupCdeSystem(String id) {
        lookupCdeMapper.delLookupCdeSystem(id); }

}
