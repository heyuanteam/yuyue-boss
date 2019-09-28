package com.yuyue.boss.api.controller;

import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.YuYueSiteService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.QRCodeUtil;
import com.yuyue.boss.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/site", produces = "application/json; charset=UTF-8")
public class YuYueSiteController extends BaseController{

    @Autowired
    private YuYueSiteService yuYueSiteService;
    /**
     * 获取现场信息
     * @param
     * @param request
     */
    @RequestMapping("/getYuYueSiteInfo")
    @ResponseBody
    public ResponseData getYuYueSiteInfo(HttpServletRequest request, HttpServletResponse response){
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

}
