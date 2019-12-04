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
@Slf4j
@RestController
@RequestMapping(value = "/app", produces = "application/json; charset=UTF-8")
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
        log.info("获取版本列表----------->>/app/getVersionList");
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
        log.info("添加版本----------->>/app/addVersion");
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
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本号已经存在！");
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
        log.info("修改版本----------->>/app/editVersion");
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
        if (!parameterMap.get("versionNo").equals(appVersionList.get(0).getVersionNo())) {
            if (CollectionUtils.isNotEmpty(appVersionList)){
                for (AppVersion appVersion:appVersionList) {
                    if (parameterMap.get("versionNo").equals(appVersion.getVersionNo())){
                        return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "版本号已经存在！");
                    }
                }
            }
        }
        try {
            appService.updateAppVersion(parameterMap.get("appVersionId"),parameterMap.get("versionNo"),
                    parameterMap.get("systemType"),parameterMap.get("programDescription"),parameterMap.get("status")
                    ,parameterMap.get("number"));
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
        log.info("删除版本----------->>/app/delVersion");
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
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getAPPMenuList")
    @ResponseBody
    public ResponseData getAPPMenuList(HttpServletRequest request, HttpServletResponse response) {
        log.info("获取APP菜单列表----------->>/app/getAPPMenuList");
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
        log.info("添加APP菜单----------->>/app/addAPPMenu");
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
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名称已经存在！");
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
        log.info("修改APP菜单------------>>/app/editAPPMenu");
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
                log.info("=======>>>>>>修改APP菜单失败！");
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
                            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "菜单名称已经存在！");
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
        log.info("删除APP菜单----------->>/app/delAPPMenu");
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
        log.info("获取APP轮播图列表----------->>/app/getBannerList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<Banner> bannerList = appService.getBannerList("",parameterMap.get("name"), parameterMap.get("status"),0);
            PageUtil pageUtil = new PageUtil(bannerList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取APP轮播图列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取APP轮播图列表失败！");
        }
    }

    /**
     * 添加APP轮播图
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addBanner")
    @ResponseBody
    @RequiresPermissions("banner:save")
    @LoginRequired
    public ResponseData addBanner(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加APP轮播图----------->>/app/addBanner");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("name"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "轮播图名称不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("url"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "轮播图图片不可以为空！");
        }
        List<Banner> bannerList = appService.getBannerList("",parameterMap.get("name"), "",0);
        if (CollectionUtils.isNotEmpty(bannerList)){
            for (Banner banner:bannerList) {
                if (parameterMap.get("name").equals(banner.getName())){
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "轮播图名称已经存在！");
                }
            }
        }
        try {
            List<Banner> list = appService.getBannerList("","", "",0);
            int sort = list.get(list.size()-1).getSort();
            sort += 1;
            Banner banner = new Banner();
            banner.setId(RandomSaltUtil.generetRandomSaltCode(32));
            banner.setName(parameterMap.get("name"));
            banner.setUrl(parameterMap.get("url"));
            banner.setSort(sort);
            appService.insertBanner(banner);
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "添加APP轮播图成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>添加APP轮播图失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加APP轮播图失败！");
        }
    }

    /**
     * 修改APP轮播图
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editBanner")
    @ResponseBody
    @RequiresPermissions("banner:save")
    @LoginRequired
    public ResponseData editBanner(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改APP轮播图------------>>/app/editBanner");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "轮播图ID不可以为空！");
        }
        if (StringUtils.isNotEmpty(parameterMap.get("sort")) &&
                (0 == Integer.valueOf(parameterMap.get("sort")) || 1 == Integer.valueOf(parameterMap.get("sort")))) {
            try {
                List<Banner> bannerList = appService.getBannerList(parameterMap.get("id"),"", "",0);
                if (CollectionUtils.isEmpty(bannerList)) {
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "轮播图ID错误！");
                }
                int sort = bannerList.get(0).getSort();
                int number = 0;
                if (0 == Integer.valueOf(parameterMap.get("sort"))) {
                    number = sort + 1;//向下
                } else {
                    number = sort - 1;//向上
                }
                appService.updateBanner(bannerList.get(0).getId(), number,"","","");
                List<Banner> list2 = appService.getBannerList("", "", "",number);
                appService.updateBanner(list2.get(0).getId(), sort, "", "","");
                List<Banner> list = appService.getBannerList("","","",0);
                return new ResponseData(list);
            } catch (Exception e) {
                log.info("=======>>>>>>修改APP轮播图失败！");
                return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "修改APP轮播图失败！");
            }
        } else if (StringUtils.isNotEmpty(parameterMap.get("status")) && ("10A".equals(parameterMap.get("status"))
                || "10B".equals(parameterMap.get("status"))) && StringUtils.isNotEmpty(parameterMap.get("name"))
                && StringUtils.isNotEmpty(parameterMap.get("url"))) {

            List<Banner> bannerList = appService.getBannerList(parameterMap.get("id"),"", "",0);
            if (CollectionUtils.isEmpty(bannerList)) {
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改的轮播图不存在！");
            }
            if (!parameterMap.get("name").equals(bannerList.get(0).getName())) {
                List<Banner> nameList = appService.getBannerList("",parameterMap.get("name"), "",0);
                if (CollectionUtils.isNotEmpty(nameList)) {
                    for (Banner banner:nameList) {
                        if (parameterMap.get("name").equals(banner.getName())){
                            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "轮播图名称已经存在！");
                        }
                    }
                }
            }
            appService.updateBanner(bannerList.get(0).getId(), 0, parameterMap.get("name"),
                    parameterMap.get("status"), parameterMap.get("url"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改APP轮播图成功！");
        }
        return new ResponseData(CodeEnum.SYSTEM_BUSY.getCode(), "参数错误！");
    }

    /**
     * 删除APP轮播图
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delBanner")
    @ResponseBody
    @RequiresPermissions("banner:remove")
    @LoginRequired
    public ResponseData delBanner(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除APP轮播图----------->>/app/delBanner");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "APP轮播图id不可以为空！");
        }
        List<Banner> bannerList = appService.getBannerList(parameterMap.get("id"),"", "",0);
        if (CollectionUtils.isEmpty(bannerList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "APP轮播图不存在！");
        }
        try {
            appService.delBanner(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除APP轮播图成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除APP轮播图失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除APP轮播图失败！");
        }
    }
}
