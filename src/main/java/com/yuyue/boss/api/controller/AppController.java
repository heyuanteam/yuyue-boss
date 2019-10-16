package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.AppService;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * APP管理
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/app", produces = "application/json; charset=UTF-8")
@Slf4j
public class AppController extends BaseController {

    @Autowired
    private AppService appService;

    /**
     * 获取版本列表
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getVersionList")
    @ResponseBody
    @RequiresPermissions("appversion:menu")
    @LoginRequired
    public ResponseData getVersionList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取版本列表----------->>/system/getVersionList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<AppVersion> appVersionList = appService.getVersionList(parameterMap.get("systemType"), parameterMap.get("versionNo"),"");
            PageUtil pageUtil = new PageUtil(appVersionList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取版本列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取版本列表失败！");
        }
    }

    /**
     * 添加版本
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addVersion")
    @ResponseBody
    @RequiresPermissions("appversion:save")
    @LoginRequired
    public ResponseData addVersion(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加版本----------->>/system/addVersion");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("versionNo"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本号不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemType"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("programDescription"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本介绍不可以为空！");
        }
        List<AppVersion> appVersionList = appService.getVersionList(parameterMap.get("systemType"), parameterMap.get("versionNo"),"");
        if (CollectionUtils.isNotEmpty(appVersionList)){
            for (AppVersion appVersion:appVersionList) {
                if (parameterMap.get("versionNo").equals(appVersion.getVersionNo())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "版本号已经存在！");
                }
            }
        }
        try {
            List<AppVersion> list = appService.getVersionList(parameterMap.get("systemType"), "","");
            int sort = list.get(list.size()-1).getNumber();
            sort += 1;
            AppVersion appVersion = new AppVersion();
            appVersion.setAppVersionId(RandomSaltUtil.generetRandomSaltCode(32));
            appVersion.setSystemType(parameterMap.get("systemType"));
            appVersion.setVersionNo(parameterMap.get("versionNo"));
            appVersion.setUpdateUser(systemUser.getLoginName());
            appVersion.setProgramDescription(parameterMap.get("programDescription"));
            appVersion.setNumber(sort);
            appService.insertAppVersion(appVersion);
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "添加版本成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>添加版本失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加版本失败！");
        }
    }

    /**
     * 修改版本
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editVersion")
    @ResponseBody
    @RequiresPermissions("appversion:save")
    @LoginRequired
    public ResponseData editVersion(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改版本----------->>/system/editVersion");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("versionNo"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本号不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemType"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("programDescription"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本介绍不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "强制更新状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("appVersionId"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本appVersionId不可以为空！");
        }
        List<AppVersion> appVersionList = appService.getVersionList(parameterMap.get("systemType"), parameterMap.get("versionNo"),"");
        if (CollectionUtils.isNotEmpty(appVersionList)){
            for (AppVersion appVersion:appVersionList) {
                if (parameterMap.get("versionNo").equals(appVersion.getVersionNo())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "版本号已经存在！");
                }
            }
        }
        try {
            appService.updateAppVersion(parameterMap.get("appVersionId"),parameterMap.get("versionNo"),
                    parameterMap.get("systemType"),parameterMap.get("programDescription"),parameterMap.get("status"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改版本成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>修改版本失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改版本失败！");
        }
    }

    /**
     * 删除版本
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delVersion")
    @ResponseBody
    @RequiresPermissions("appversion:remove")
    @LoginRequired
    public ResponseData delVersion(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除版本----------->>/system/delVersion");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("appVersionId"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本appVersionId不可以为空！");
        }
        List<AppVersion> appVersionList = appService.getVersionList("","",parameterMap.get("appVersionId"));
        if (CollectionUtils.isEmpty(appVersionList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本不存在！");
        }
        try {
            appService.delVersion(parameterMap.get("appVersionId"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除版本成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除版本失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除版本失败！");
        }
    }

    /**
     * 获取APP菜单列表
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getAPPMenuList")
    @ResponseBody
    @RequiresPermissions("appMenu:menu")
    @LoginRequired
    public ResponseData getAPPMenuList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取APP菜单列表----------->>/system/getAPPMenuList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<VideoCategory> videoCategoryList = appService.getAPPMenuList("",parameterMap.get("category"), parameterMap.get("status"),0);
            PageUtil pageUtil = new PageUtil(videoCategoryList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取APP菜单列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取APP菜单列表失败！");
        }
    }

    /**
     * 添加APP菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addAPPMenu")
    @ResponseBody
    @RequiresPermissions("appMenu:save")
    @LoginRequired
    public ResponseData addAPPMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加APP菜单----------->>/system/addAPPMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("category"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名称不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("url"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单图标不可以为空！");
        }
        List<VideoCategory> videoCategoryList = appService.getAPPMenuList("",parameterMap.get("category"), "",0);
        if (CollectionUtils.isNotEmpty(videoCategoryList)){
            for (VideoCategory videoCategory:videoCategoryList) {
                if (parameterMap.get("category").equals(videoCategory.getCategory())){
                    return new ResponseData(CodeEnum.SUCCESS.getCode(), "菜单名称已经存在！");
                }
            }
        }
        try {
            List<VideoCategory> list = appService.getAPPMenuList("","", "",0);
            int sort = list.get(list.size()-1).getCategoryNo();
            sort += 1;
            VideoCategory videoCategory = new VideoCategory();
            videoCategory.setId(RandomSaltUtil.generetRandomSaltCode(32));
            videoCategory.setCategory(parameterMap.get("category"));
            videoCategory.setUrl(parameterMap.get("url"));
            videoCategory.setCategoryNo(sort);
            appService.insertVideoCategory(videoCategory);
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "添加APP菜单成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>添加APP菜单失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加APP菜单失败！");
        }
    }

    /**
     * 修改APP菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editAPPMenu")
    @ResponseBody
    @RequiresPermissions("appMenu:save")
    @LoginRequired
    public ResponseData editAPPMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改APP菜单------------>>/system/editAPPMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID不可以为空！");
        }
        if (StringUtils.isNotEmpty(parameterMap.get("sort")) &&
                (0 == Integer.valueOf(parameterMap.get("sort")) || 1 == Integer.valueOf(parameterMap.get("sort")))) {
            try {
                List<VideoCategory> videoCategoryList = appService.getAPPMenuList(parameterMap.get("id"),"", "",0);
                if (CollectionUtils.isEmpty(videoCategoryList)) {
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单ID错误！");
                }
                int sort = videoCategoryList.get(0).getCategoryNo();
                int number = 0;
                if (0 == Integer.valueOf(parameterMap.get("sort"))) {
                    number = sort + 1;//向下
                } else {
                    number = sort - 1;//向上
                }
                appService.updateAPPMenu(videoCategoryList.get(0).getId(), number,"","","");
                List<VideoCategory> list2 = appService.getAPPMenuList("", "", "",number);
                appService.updateAPPMenu(list2.get(0).getId(), sort, "", "","");
                List<VideoCategory> list = appService.getAPPMenuList("","","",0);
                return new ResponseData(list);
            } catch (Exception e) {
                log.info("=======>>>>>>修改修改APP菜单失败！");
                return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "修改APP菜单失败！");
            }
        } else if (StringUtils.isNotEmpty(parameterMap.get("status")) && ("10A".equals(parameterMap.get("status"))
                || "10B".equals(parameterMap.get("status"))) && StringUtils.isNotEmpty(parameterMap.get("category"))
                && StringUtils.isNotEmpty(parameterMap.get("url"))) {

            List<VideoCategory> videoCategoryList = appService.getAPPMenuList(parameterMap.get("id"),"", "",0);
            if (CollectionUtils.isEmpty(videoCategoryList)) {
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改的菜单不存在！");
            }
            if (!parameterMap.get("category").equals(videoCategoryList.get(0).getCategory())) {
                List<VideoCategory> categoryList = appService.getAPPMenuList("",parameterMap.get("category"), "",0);
                if (CollectionUtils.isNotEmpty(categoryList)) {
                    for (VideoCategory videoCategory:categoryList) {
                        if (parameterMap.get("category").equals(videoCategory.getCategory())){
                            return new ResponseData(CodeEnum.SUCCESS.getCode(), "菜单名称已经存在！");
                        }
                    }
                }
            }
            appService.updateAPPMenu(videoCategoryList.get(0).getId(), 0, parameterMap.get("status"), parameterMap.get("category"),
                    parameterMap.get("url"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改APP菜单成功！");
        }
        return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "参数错误！");
    }

    /**
     * 删除APP菜单
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delAPPMenu")
    @ResponseBody
    @RequiresPermissions("appMenu:remove")
    @LoginRequired
    public ResponseData delAPPMenu(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除APP菜单----------->>/system/delAPPMenu");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "APP菜单id不可以为空！");
        }
        List<VideoCategory> categoryList = appService.getAPPMenuList(parameterMap.get("id"),"", "",0);
        if (CollectionUtils.isEmpty(categoryList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "APP菜单不存在！");
        }
        try {
            appService.delAPPMenu(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除APP菜单成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除APP菜单失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除APP菜单失败！");
        }
    }

    /**
     * 获取APP轮播图列表
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getBannerList")
    @ResponseBody
    @RequiresPermissions("banner:menu")
    @LoginRequired
    public ResponseData getBannerList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取APP轮播图列表----------->>/system/getBannerList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
//            List<VideoCategory> videoCategoryList = loginService.getBannerList("",parameterMap.get("category"), parameterMap.get("status"),0);
//            PageUtil pageUtil = new PageUtil(videoCategoryList);
            return new ResponseData("");
        } catch (Exception e) {
            log.info("===========>>>>>>获取APP轮播图列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取APP轮播图列表失败！");
        }
    }
}
