package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.*;

import java.util.List;


public interface LookupCdeService {

    //搜索字典
    List<LookupCde> getLookupCdeSystem(String status, String typeName, String id);

    //添加字典
    void insertLookupCde(LookupCde lookupCde);

    //修改字典
    void updateLookupCde(String id, String typeName, String status);

    //搜索字典下级
    List<LookupCdeConfig> getLookupCdeList(String systemId, String id);

    //添加字典下级
    void insertLookupCdeConfig(LookupCdeConfig lookupCdeConfig);

    //修改字典下级
    void updateLookupCdeList(String id, String type, String status);

    //删除字典下级
    void delLookupCdeList(String id);

    //删除字典
    void delLookupCdeSystem(String id);
}
