package com.yuyue.boss.api.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.*;
import com.yuyue.boss.config.JPushClients;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.RedisUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/send", produces = "application/json; charset=UTF-8")
@Slf4j
public class SendController extends BaseController{

    @Autowired
    private JPushClients jPushClients;
    @Autowired
    private SendService sendService;
    @Autowired
    private YuYueSiteService yuYueSiteService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private AdReviewService adReviewService;

    //极光推送类型
    private static final Map<String, Object> iosMap = new HashMap<>();
    static {
        iosMap.put("1","艺人审核的通知");//不需要参数
        iosMap.put("2","广告商审核的通知");//不需要参数
        iosMap.put("4","现场详情的通知");//需要参数
        iosMap.put("5","视频审核的通知");//不需要参数
        iosMap.put("6","广告审核的通知");//不需要参数
//        iosMap.put("3","关注人发视频的通知");//需要参数
    }

    /**
     * 极光艺人审核的通知 : 1
     * @return
     */
    public ResponseData sendShowJPush(ArtistReview artistReview,AppUser appUserMsg,String status){
        JPush jPush = new JPush();
        String str = "10B" == status?"通过! ":"拒绝! ";
        try {
            log.info("极光艺人审核的通知开始-------------->>start");
            if (StringUtils.isNotNull(artistReview)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","1");
                map.put("notice","艺人审核的通知");
                jPush.setNotificationTitle("您好! "+artistReview.getTeamName()+"艺人审核"+str);
                jPush.setMsgTitle(artistReview.getDescription());
                jPush.setMsgContent("艺人审核通知！");
                jPush.setExtras("1");
                return getJPush(jPush,appUserMsg,map,0);
            }
        } catch (Exception e) {
            log.info("极光广告商审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光广告商审核的通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 极光广告商审核的通知 : 2
     * @param id           广告商id
     * @param appUserMsg
     * @param status
     * @return
     */
    public ResponseData sendAdReviewJPush(String id,AppUser appUserMsg,String status){
        JPush jPush = new JPush();
        String str = "10B" == status?"通过! ":"拒绝! ";
        try {
            log.info("极光广告商审核的通知开始-------------->>start");
            List<Advertisement> adReviewList = adReviewService.getAdReviewList(id,"", "", "", "", "", "");
            if (CollectionUtils.isNotEmpty(adReviewList)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","2");
                map.put("notice","广告商审核的通知");
                jPush.setNotificationTitle("您好! "+adReviewList.get(0).getPhone()+"广告商审核"+str);
                jPush.setMsgTitle(adReviewList.get(0).getMerchantName());
                jPush.setMsgContent(adReviewList.get(0).getBusinessLicense());
                jPush.setExtras("2");
                return getJPush(jPush,appUserMsg,map,0);
            }
        } catch (Exception e) {
            log.info("极光广告商审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光广告商审核的通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 全部，现场详情的通知 4
     * sceneId 现场ID
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/sendJPush")
    @ResponseBody
//    @Async // 异步方法
    @LoginRequired
    public ResponseData sendJPush(HttpServletRequest request, HttpServletResponse response){
        log.info("极光推送-------------->>/sendJPush/sendJPush");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "标题ID不可以为空！");
        }
        List<YuYueSite> idList = yuYueSiteService.getYuYueSiteInfo(parameterMap.get("id"));
        JPush jPush = new JPush();
        try {
            if (CollectionUtils.isNotEmpty(idList)) {
                Map<String, String> map = Maps.newHashMap();
                map.put("type","4");
                map.put("notice","现场详情的通知");
                map.put("sceneId",parameterMap.get("id"));
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle(idList.get(0).getTitle());
                jPush.setMsgTitle(idList.get(0).getSiteAddr());
                jPush.setMsgContent(idList.get(0).getImageUrl());
                jPush.setExtras("4");

                AppUser appUserMsg = appUserService.getAppUserMsg("");
                return getJPush(jPush,appUserMsg,map,1);
            }
            return new ResponseData(CodeEnum.E_400.getCode(),"极光推送参数错误！");
        } catch (Exception e) {
            log.info("极光推送失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光推送失败！");
        }
//        用户ID,别名
//        List<String> aliasList = Arrays.asList("239");
//        String notificationTitle = "通知内容标题";
//        String msgTitle = "消息内容标题";
//        String msgContent = "消息内容";
    }

    /**
     * 极光视频审核的通知 : 5
     * @param id            现场id
     * @param authorId      作者id
     * @param status        现场状态
     * @return
     */
    public ResponseData sendVideoJPush(String id,String authorId,String status){
        JPush jPush = new JPush();
        String str = "10B" == status?"通过! ":"拒绝! ";
        try {
            log.info("极光视频审核的通知开始-------------->>start");
            List<UploadFile> videoList = videoService.getVideoInfoList();
            if (CollectionUtils.isNotEmpty(videoList)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","5");
                map.put("notice","视频审核的通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好! "+videoList.get(0).getFilesName()+"视频审核"+str);
                jPush.setMsgTitle("视频审核的通知");
                jPush.setMsgContent(videoList.get(0).getTitle());
                jPush.setExtras("5");

                AppUser appUserMsg = appUserService.getAppUserMsg(authorId);
                return getJPush(jPush,appUserMsg,map,0);
            }
        } catch (Exception e) {
            log.info("极光视频审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光视频审核的通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 极光广告审核的通知 : 6
     * @param commodity 广告
     * @param status
     * @return
     */
    public ResponseData sendCommodityInfoJPush(Commodity commodity,String status){
        JPush jPush = new JPush();
        String str = "10C" == status?"通过! ":"拒绝! ";
        try {
            log.info("极光广告审核的通知开始-------------->>start");
            AppUser appUserMsg = appUserService.getAppUserMsg(commodity.getMerchantId());
            if (StringUtils.isNotNull(appUserMsg)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","6");
                map.put("notice","广告审核的通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好! "+commodity.getCommodityName()+"广告审核"+str);
                jPush.setMsgTitle(commodity.getCommodityName());
                jPush.setMsgContent(commodity.getPayUrl());
                jPush.setExtras("6");
                return getJPush(jPush,appUserMsg,map,0);
            }
        } catch (Exception e) {
            log.info("极光广告审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光广告审核的通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    public ResponseData getJPush(JPush jPush,AppUser appUserMsg,Map<String, String> map,int num){
        List<JPush> list = sendService.getValid(jPush.getNotificationTitle(),jPush.getMsgTitle(),
                jPush.getMsgContent(),jPush.getExtras(),jPush.getValid());
        if (CollectionUtils.isEmpty(list)) {
            sendService.insertJPush(jPush);
            if (0 == num) {
                List<String> stringList = new ArrayList<>();
                log.info("极光别名=========="+appUserMsg.getJpushName());
                stringList.add(appUserMsg.getJpushName());
                jPushClients.sendToAliasList(stringList,jPush.getNotificationTitle(), jPush.getMsgTitle(), jPush.getMsgContent(), map);
            } else {
                jPushClients.sendToAll(jPush.getNotificationTitle(), jPush.getMsgTitle(), jPush.getMsgContent(), map);
            }
            sendService.updateValid("10B",jPush.getId());
            log.info("极光视频审核的通知结束-------------->>SUCCESS");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }
}
