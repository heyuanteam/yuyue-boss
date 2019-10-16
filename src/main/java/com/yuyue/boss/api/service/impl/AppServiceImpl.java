package com.yuyue.boss.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.mapper.AppMapper;
import com.yuyue.boss.api.mapper.LoginMapper;
import com.yuyue.boss.api.service.AppService;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.utils.BeanUtil;
import com.yuyue.boss.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ly
 */
@Service(value = "AppService")
public class AppServiceImpl implements AppService {

    @Autowired
    private AppMapper appMapper;

    @Override
    public List<AppVersion> getVersionList(String systemType, String versionNo,String appVersionId){
        return appMapper.getVersionList(systemType,versionNo,appVersionId); }

    @Override
    public void insertAppVersion(AppVersion appVersion) {
        appMapper.insertAppVersion(appVersion); }

    @Override
    public void updateAppVersion(String appVersionId, String versionNo, String systemType, String programDescription, String status) {
        appMapper.updateAppVersion(appVersionId,versionNo,systemType,programDescription,status); }

    @Override
    public void delVersion(String appVersionId) {
        appMapper.delVersion(appVersionId); }

    @Override
    public List<VideoCategory> getAPPMenuList(String id,String category, String status,int number) {
        return appMapper.getAPPMenuList(id,category,status,number); }

    @Override
    public void insertVideoCategory(VideoCategory videoCategory) {
        appMapper.insertVideoCategory(videoCategory); }

    @Override
    public void updateAPPMenu(String id, int sort, String status, String category,String url) {
        appMapper.updateAPPMenu(id,sort,status,category,url); }

    @Override
    public void delAPPMenu(String id) {
        appMapper.delAPPMenu(id); }
}
