package com.yuyue.boss.api.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SiteShow;
import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.YuYueSiteService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/site", produces = "application/json; charset=UTF-8")
@Slf4j
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
    @RequiresPermissions("scene:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getYuYueSiteInfo( HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);

        //交集参数
        String page=request.getParameter("page");
        String type=request.getParameter("type");

        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        List<YuYueSite> yuYueSiteInfo =null;
        if ("get".equals(type)){
            log.info("获取现场信息------------>>/site/getYuYueSiteInfo");
            String id=request.getParameter("id");
            if (StringUtils.isEmpty(id)){
                PageHelper.startPage(Integer.parseInt(page), 10);
                yuYueSiteInfo = yuYueSiteService.getYuYueSiteInfo("");
            }else {
                yuYueSiteInfo = yuYueSiteService.getYuYueSiteInfo(id);
            }

        }else if ("search".equals(type)){
            log.info("现场搜索------------>>/site/searchYuYueSiteInfo");
            String siteAddr=request.getParameter("siteAddr");
            String status=request.getParameter("status");
            String jPushStatus=request.getParameter("jPushStatus");
            String mainPerson=request.getParameter("mainPerson");
            String startTime=request.getParameter("startTime");
            String endTime=request.getParameter("endTime");
            PageHelper.startPage(Integer.parseInt(page), 10);
            yuYueSiteInfo = yuYueSiteService.searchYuYueSiteInfo(siteAddr, status, jPushStatus, mainPerson, startTime, endTime);
        }else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"type参数错误！！");
        }
        PageInfo<YuYueSite> pageInfo=new PageInfo<>(yuYueSiteInfo);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(yuYueSiteInfo,currentPage,(int)total,pages);

    }



    /**
     * 添加现场
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/insertYuYueSite" , method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("scene:save")//具有 user:save 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData insertYuYueSite(@RequestBody YuYueSite yuYueSite, HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("添加现场------------>>/site/insertYuYueSite"+yuYueSite);

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
        /*List<SiteShow> siteShows = yuYueSite.getSiteShow();
         if (StringUtils.isNotEmpty(siteShows)){
            for (SiteShow siteShow : siteShows
            ) {
                siteShow.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase().toString());
            }
        }*/


        yuYueSiteService.insertYuYueSite(new YuYueSite(id,title,imageUrl,siteAddr,mainPerson,personTotal,"",qrCodePath,admissionTime,startTime,endTime,status,jPushStatus,null));

        return new ResponseData();
    }


    /**
     * 添加现场节目
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/insertSiteShow" , method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("scene:save")//具有 user:save 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData insertSiteShow(@RequestBody List<SiteShow> siteShows, HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("添加现场------------>>/site/insertYuYueSite"+JSON.toJSONString(siteShows));
        try {

            if (StringUtils.isNotEmpty(siteShows)){
                    for (SiteShow siteShow : siteShows
                    ) {
                        if (StringUtils.isEmpty(siteShow.getId())){
                            siteShow.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase().toString());
                            yuYueSiteService.insertSiteShow(siteShow);
                        }else {
                            yuYueSiteService.updateSiteShow(siteShow);
                        }
                    }

            }

        }catch (NullPointerException n){
            n.printStackTrace();
            log.info("确实现场Id");
        }


        return new ResponseData();
    }

    /**
     * 更新娱悦现场信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/updateYuYueSite" , method = RequestMethod.POST)
    @ResponseBody
    @RequiresPermissions("scene:save")//具有 user:save 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateYuYueSite( @RequestBody YuYueSite yuYueSite,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("接受的参数yuyueSite:"+yuYueSite.toString());
        log.info("更新娱悦现场信息------------>>/site/updateYuYueSite");
        if (StringUtils.isEmpty(yuYueSite.getId()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        List<SiteShow> siteShows = yuYueSite.getSiteShow();
        if (StringUtils.isNotEmpty(siteShows)){
            for (SiteShow siteShow : siteShows
            ) {
                siteShow.setId(UUID.randomUUID().toString().replace("-", "").toUpperCase().toString());
            }
        }
        yuYueSiteService.updateYuYueSite(yuYueSite);
        return new ResponseData();
    }

    /**
     * 删除娱悦现场信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteYuYueSiteById")
    @ResponseBody
    @RequiresPermissions("scene:remove")//具有 user:remove 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteYuYueSiteById(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("删除娱悦现场信息------------>>/site/deleteYuYueSiteById");
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        yuYueSiteService.deleteYuYueSiteById(id);
        return new ResponseData();
    }

    @RequestMapping("/deleteSiteShowById")
    @ResponseBody
    @RequiresPermissions("scene:remove")//具有 user:remove 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteSiteShowById(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("删除娱悦现场信息------------>>/site/deleteYuYueSiteById");
        try {
            String id = request.getParameter("id");
            if (StringUtils.isEmpty(id))
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"节目id为空！！");
            yuYueSiteService.deleteSiteShow(id);
        }catch (Exception e){
            e.printStackTrace();
            log.info("删除失败");
        }

        return new ResponseData();
    }

}
