package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.Advertisement;
import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.JPush;
import com.yuyue.boss.api.service.AdReviewService;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.api.service.SendService;
import com.yuyue.boss.config.JPushClients;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/adReview" ,produces = "application/json; charset=UTF-8")
@Slf4j
public class AdReviewController extends BaseController {

    @Autowired
    private AdReviewService adReviewService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private SendService sendService;
    @Autowired
    private JPushClients jPushClients;

    /**
     * 广告商审核列表
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getAdReviewList")
    @ResponseBody
    @RequiresPermissions("advertisement:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getAdReviewList(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("广告商审核列表-------------->>/adReview/getAdReviewList");
        String page=request.getParameter("page");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        String merchantName =request.getParameter("merchantName");
        String merchantAddr =request.getParameter("merchantAddr");
        String phone = request.getParameter("phone");
        String status = request.getParameter("status");
        String applicationStartTime = request.getParameter("applicationStartTime");
        String applicationEndTime = request.getParameter("applicationEndTime");

        PageHelper.startPage(Integer.parseInt(page), 10);
        List<Advertisement> adReviewList = adReviewService.getAdReviewList("",merchantName,merchantAddr, phone, status, applicationStartTime,applicationEndTime);
        PageInfo<Advertisement> pageInfo=new PageInfo<>(adReviewList);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(adReviewList, currentPage,(int) total,pages);
    }

    /**
     * 修改广告商审核状态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateAdReviewStatus")
    @ResponseBody
    @RequiresPermissions("advertisement:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateAdReviewStatus(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("修改广告商审核状态-------------->>/adReview/updateAdReviewStatus");
        String id = request.getParameter("id");
        String userId = request.getParameter("userId");
        String status = request.getParameter("status");
        if (StringUtils.isEmpty(id)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"参数id为空！！");
        }else if (StringUtils.isEmpty(userId)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"参数userId为空！！");
        }else if (StringUtils.isEmpty(status)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"参数status为空！！");
        }
        AppUser appUserMsg = appUserService.getAppUserMsg(userId);
        System.out.println(appUserMsg);
        if (StringUtils.isNull(appUserMsg)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"参数userId错误！！");
        }
        if ("10C".equals(status) || "10B".equals(status)){
            adReviewService.updateAdReviewStatus(id,status);
            if("1".equals(appUserMsg.getUserType()))
                appUserMsg.setUserType("3");
            else if ("2".equals(appUserMsg.getUserType()))
                appUserMsg.setUserType("4");
            else
                return new ResponseData(CodeEnum.E_400);
            System.out.println(appUserMsg);
            appUserService.updateAppUser(appUserMsg);
        } else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "type参数错误！！");
        }

        JPush jPush = new JPush();
        String str = "";
        if("10B".equals(status)){
            str = "通过！";
        } else {
            str = "拒绝！";
        }
        try {
            log.info("极光广告商审核的通知开始-------------->>start");
            List<Advertisement> adReviewList = adReviewService.getAdReviewList(id,"", "", "", "", "", "");
            if (CollectionUtils.isNotEmpty(adReviewList)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","2");
                map.put("notice","广告商审核的通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好！"+adReviewList.get(0).getPhone()+"广告商审核"+str);
                jPush.setMsgTitle(adReviewList.get(0).getMerchantName());
                jPush.setMsgContent(adReviewList.get(0).getBusinessLicense());
                jPush.setExtras("2");

                List<JPush> list = sendService.getValid(jPush.getId());
                if (CollectionUtils.isNotEmpty(list)) {
                    return new ResponseData(CodeEnum.SUCCESS.getCode(),"请不要重复点击！");
                }
                sendService.insertJPush(jPush);
                List<String> stringList = new ArrayList<>();
                stringList.add(userId);
                jPushClients.sendToAliasList(stringList,jPush.getNotificationTitle(), jPush.getMsgTitle(), jPush.getMsgContent(), map);
                sendService.updateValid("10B",jPush.getId());
                log.info("极光广告商审核的通知结束-------------->>SUCCESS");
            }
        } catch (Exception e) {
            log.info("极光广告商审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光广告商审核的通知失败！");
        }
        return new ResponseData();
    }

}
