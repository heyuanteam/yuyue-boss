package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.YuYueSiteService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.QRCodeUtil;
import com.yuyue.boss.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/site", produces = "application/json; charset=UTF-8")
public class YuYueSiteController extends BaseController{

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private YuYueSiteService yuYueSiteService;
    /**
     * 获取现场信息
     * @param
     * @param request
     */
    @RequestMapping("/getYuYueSiteInfo")
    @ResponseBody
    @RequiresPermissions("scene:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getYuYueSiteInfo(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("获取现场信息------------>>/site/getYuYueSiteInfo");
        String id=request.getParameter("id");
        String page=request.getParameter("page");


        List<YuYueSite> yuYueSiteInfo = yuYueSiteService.getYuYueSiteInfo(id, PageUtil.getBeginPage(page).getBegin(), 15);
        return new ResponseData(yuYueSiteInfo);

    }

    /**
     * 现场搜索
     * @param
     * @param request
     */
    @RequestMapping("/searchYuYueSiteInfo")
    @ResponseBody
    @RequiresPermissions("scene:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData searchYuYueSiteInfo(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("现场搜索------------>>/site/searchYuYueSiteInfo");
        String siteAddr=request.getParameter("siteAddr");
        String status=request.getParameter("status");
        String jPushStatus=request.getParameter("jPushStatus");
        String mainPerson=request.getParameter("mainPerson");
        String startTime=request.getParameter("startTime");
        String endTime=request.getParameter("endTime");
        String page=request.getParameter("page");

        List<YuYueSite> yuYueSites = yuYueSiteService.searchYuYueSiteInfo(siteAddr, status, jPushStatus, mainPerson, startTime, endTime, PageUtil.getBeginPage(page).getBegin(), 15);
        return new ResponseData(yuYueSites);

    }

    @RequestMapping("/insertYuYueSite")
    @ResponseBody
    @RequiresPermissions("scene:save")//具有 user:save 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData insertYuYueSite(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        String id = UUID.randomUUID().toString().replace("-", "").toUpperCase().toString();
        String title = request.getParameter("title");
        String imageUrl = request.getParameter("imageUrl");
        String siteAddr = request.getParameter("siteAddr");
        String mainPerson = request.getParameter("mainPerson");
        String personTotal = request.getParameter("personTotal");
        String qrCodePath = request.getParameter("qrCodePath");
        String admissionTime = request.getParameter("admissionTime");
        String startTime=request.getParameter("startTime");
        String endTime=request.getParameter("endTime");
        String status=request.getParameter("status");
        String jPushStatus=request.getParameter("jPushStatus");
        YuYueSite yuYueSite=new YuYueSite(id,title,imageUrl,siteAddr,mainPerson,personTotal,"",qrCodePath,admissionTime,startTime,endTime,status,jPushStatus);

        yuYueSiteService.insertYuYueSite(yuYueSite);

        return new ResponseData();
    }

    /**
     * 更新娱悦现场信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateYuYueSite")
    @ResponseBody
    @RequiresPermissions("scene:save")//具有 user:save 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateYuYueSite(YuYueSite yuYueSite,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        /*String id = request.getParameter("id");*/
        if (StringUtils.isEmpty(yuYueSite.getId()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        yuYueSiteService.updateYuYueSite(yuYueSite);
        return new ResponseData();
    }


    @RequestMapping("/deleteYuYueSiteById")
    @ResponseBody
    @RequiresPermissions("scene:remove")//具有 user:remove 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteYuYueSiteById(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        yuYueSiteService.deleteYuYueSiteById(id);
        return new ResponseData();
    }



}
