package com.yuyue.boss.api.controller;

import com.google.common.collect.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.*;
import com.yuyue.boss.config.JPushClients;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.ResultJSONUtils;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告管理
 */

@Slf4j
@RestController
@RequestMapping(value="/send", produces = "application/json; charset=UTF-8")
public class SendController extends BaseController {

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
    @Autowired
    private OrderService orderService;

    //极光推送类型
    private static final Map<String, Object> sendMap = new HashMap<>();
    static {
        sendMap.put("1","艺人审核通知");//不需要参数
        sendMap.put("2","广告商审核通知");//不需要参数
        sendMap.put("3","关注人发视频通知");//需要参数
        sendMap.put("4","现场详情通知");//需要参数
        sendMap.put("5","视频审核通知");//不需要参数
        sendMap.put("6","广告审核通知");//不需要参数
        sendMap.put("7","库存通知");//需要参数
        sendMap.put("8","商家卖出商品通知");//需要参数
        sendMap.put("9","极光商家退款通知");//需要参数
        sendMap.put("10","极光推送举报反馈通知");//需要参数
    }

    /**
     * 获取公告列表
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getJPushList")
    @ResponseBody
    @RequiresPermissions("Announcement:menu")
    @LoginRequired
    public ResponseData getJPushList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取公告列表----------->>/send/getJPushList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<JPush> jPushList = sendService.getJPushList("",parameterMap.get("msgTitle"), parameterMap.get("extras"),
                    parameterMap.get("startTime"),parameterMap.get("endTime"));
            PageUtil pageUtil = new PageUtil(jPushList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取公告列表失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取公告列表失败！");
        }
    }

    /**
     * 删除公告
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delJPush")
    @ResponseBody
    @RequiresPermissions("Announcement:remove")
    @LoginRequired
    public ResponseData delJPush(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除公告----------->>/send/delJPush");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "公告id不可以为空！");
        }
        List<JPush> jPushList = sendService.getJPushList(parameterMap.get("id"),"","","","");
        if (CollectionUtils.isEmpty(jPushList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "公告不存在！");
        }
        try {
            sendService.delJPush(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除公告成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除公告失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除公告失败！");
        }
    }

    /**
     * 极光艺人审核的通知 : 1
     * @return
     */
    public ResponseData sendShowJPush(ArtistReview artistReview, AppUser appUserMsg, String status){
        JPush jPush = new JPush();
        String str = "拒绝!";
        if ("10B".equals(status)){
            str = "通过!";
        }
        try {
            log.info("极光艺人审核的通知开始-------------->>start");
            if (StringUtils.isNotNull(artistReview)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","1");
                map.put("notice","艺人审核通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好!"+artistReview.getTeamName()+"艺人审核"+str);
                jPush.setMsgTitle(artistReview.getDescription());
                jPush.setMsgContent("艺人审核通知!");
                jPush.setExtras("1");
                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())){
                    log.info("极光别名=========="+appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush,stringList,map,0);
                }
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
    public ResponseData sendAdReviewJPush(String id, AppUser appUserMsg, String status){
        JPush jPush = new JPush();
        String str = "拒绝!";
        if ("10B".equals(status)){
            str = "通过!";
        }
        try {
            log.info("极光广告商审核的通知开始-------------->>start");
            List<Advertisement> adReviewList = adReviewService.getAdReviewList(id,"", "", "", "", "", "");
            if (CollectionUtils.isNotEmpty(adReviewList)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","2");
                map.put("notice","广告商审核通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好!"+adReviewList.get(0).getPhone()+"广告商审核"+str);
                jPush.setMsgTitle(adReviewList.get(0).getMerchantName());
                jPush.setMsgContent(adReviewList.get(0).getBusinessLicense());
                jPush.setExtras("2");
                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush, stringList, map, 0);
                }
            }
        } catch (Exception e) {
            log.info("极光广告商审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光广告商审核的通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 极光关注人发视频的通知 : 3
     * @return
     */
    public ResponseData sendFollowJPush(String authorId, String videoId){
        JPush jPush = new JPush();
        try {
            log.info("极光关注人发视频开始-------------->>start");
            AppUser appUserMsg = appUserService.getAppUserMsg(authorId,"");
            if (StringUtils.isNotNull(appUserMsg)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","3");
                map.put("authorId",authorId);
                map.put("videoId",videoId);
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您关注的"+appUserMsg.getNickName()+"发视频啦!");
                jPush.setMsgTitle(appUserMsg.getNickName()+"发视频啦!");
                jPush.setMsgContent("关注人发视频通知!");
                jPush.setExtras("3");

                List<String> stringList = new ArrayList<>();
                List<String> attentionList = sendService.getAttentionList(authorId);
                if (CollectionUtils.isNotEmpty(attentionList)){
                    for (String userId:attentionList){
                        AppUser appUser = appUserService.getAppUserMsg(userId,"");
                        if (StringUtils.isNotNull(appUser) && StringUtils.isNotEmpty(appUser.getJpushName())){
                            log.info("极光别名=========="+appUser.getJpushName());
                            stringList.add(appUser.getJpushName());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(stringList)){
                    return getJPush(jPush,stringList,map,0);
                }
            }
        } catch (Exception e) {
            log.info("极光关注人发视频的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光关注人发视频的通知失败！");
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
                map.put("notice","现场详情通知");
                map.put("sceneId",parameterMap.get("id"));
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle(idList.get(0).getTitle());
                jPush.setMsgTitle(idList.get(0).getSiteAddr());
                jPush.setMsgContent(idList.get(0).getImageUrl());
                jPush.setExtras("4");

                AppUser appUserMsg = appUserService.getAppUserMsg("","");
                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    //修改现场极光状态
                    idList.get(0).setStatus("10B");
                    yuYueSiteService.updateYuYueSite(idList.get(0));
                    return getJPush(jPush, stringList, map, 1);
                }
            }
            return new ResponseData(CodeEnum.E_400.getCode(),"极光推送参数错误！");
        } catch (Exception e) {
            log.info("极光推送失败！");
            sendService.updateValid("10C",jPush.getId());
            //修改现场极光状态
            idList.get(0).setStatus("10C");
            yuYueSiteService.updateYuYueSite(idList.get(0));
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
    public ResponseData sendVideoJPush(String id, String authorId, String status){
        JPush jPush = new JPush();
        String str = "拒绝!";
        if ("10B".equals(status)){
            str = "通过!";
        }
        try {
            log.info("极光视频审核的通知开始-------------->>start");
            List<UploadFile> videoList = videoService.searchVideoInfo(id,"","","","","","");
            if (CollectionUtils.isNotEmpty(videoList)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","5");
                map.put("notice","视频审核通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好!"+videoList.get(0).getFilesName()+"视频审核"+str);
                jPush.setMsgTitle("视频审核通知");
                jPush.setMsgContent(videoList.get(0).getTitle());
                jPush.setExtras("5");

                AppUser appUserMsg = appUserService.getAppUserMsg(authorId,"");
                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush, stringList, map, 0);
                }
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
    public ResponseData sendCommodityInfoJPush(Commodity commodity, String status){
        JPush jPush = new JPush();
        String str = "拒绝!";
        if ("10C".equals(status)){
            str = "通过!";
        }
        try {
            log.info("极光广告审核的通知开始-------------->>start");
            AppUser appUserMsg = appUserService.getAppUserMsg(commodity.getMerchantId(),"");
            if (StringUtils.isNotNull(appUserMsg)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","6");
                map.put("notice","广告审核通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好!"+commodity.getCommodityName()+"广告审核"+str);
                jPush.setMsgTitle(commodity.getCommodityName());
                jPush.setMsgContent(commodity.getPayUrl());
                jPush.setExtras("6");

                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush, stringList, map, 0);
                }
            }
        } catch (Exception e) {
            log.info("极光广告审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光广告审核的通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 第二版
     * 商铺审核通过推送
     * @param mallShop
     * @param status
     * @return
     */
    public ResponseData sendMallShopInfoJPush(MallShop mallShop, String status){
        JPush jPush = new JPush();
        String str = "拒绝!";
        if ("10C".equals(status)){
            str = "通过!";
        }
        try {
            log.info("极光商铺审核开始-------------->>start");
            AppUser appUserMsg = appUserService.getAppUserMsg(mallShop.getMerchantId(),"");
            if (StringUtils.isNotNull(appUserMsg)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","6");
                map.put("notice","极光商铺审核");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好!"+mallShop.getCommodityName()+"商铺审核"+str);
                jPush.setMsgTitle(mallShop.getCommodityName());
                jPush.setMsgContent("您可以开始你店铺商品添加啦！以便客户购买！");
                jPush.setExtras("6");

                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush, stringList, map, 0);
                }
            }
        } catch (Exception e) {
            log.info("极光商铺审核的通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光商铺审核的通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }
    /**
     * 极光库存通知 : 7
     * @param shopid
     * @return
     */
    @RequestMapping("/sendStockJPush")
    @ResponseBody
    public ResponseData sendStockJPush(String merchantId, String shopid){
        JPush jPush = new JPush();
        try {
            log.info("极光库存通知开始-------------->>start");
            AppUser appUserMsg = appUserService.getAppUserMsg(merchantId,"");
            List<MallShop> mallShop = sendService.findShopId(shopid,merchantId,"");
            if (StringUtils.isNotNull(appUserMsg) && CollectionUtils.isNotEmpty(mallShop)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","7");
                map.put("notice","极光库存通知");
                map.put("shopid",shopid);
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好！"+mallShop.get(0).getCommodityName()+"库存已严重不足！");
                jPush.setMsgTitle("极光库存通知");
                jPush.setMsgContent("您好！"+mallShop.get(0).getCommodityName()+"库存已严重不足，请及时补充！以便客户购买！");
                jPush.setExtras("7");

                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush, stringList, map, 0);
                }
            }
        } catch (Exception e) {
            log.info("极光库存通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光库存通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 极光商家卖出商品通知 : 8
     * @param orderId
     * @return  1
     */
    @RequestMapping("/sendClotheSold")
    @ResponseBody
    public ResponseData sendClotheSold(String orderId){
        JPush jPush = new JPush();
        try {
            log.info("极光商家卖出商品通知开始-------------->>orderId"+orderId);
            Order order = orderService.getOrderById(orderId);
            List<OrderItem> list = sendService.findOrderItemId(orderId);
            if (CollectionUtils.isNotEmpty(list) && StringUtils.isNotNull(order) && "10B".equals(order.getStatus())){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","8");
                map.put("notice","极光商家卖出商品通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好！娱悦APP提醒您！有商品已卖出！请及时查看！");
                jPush.setMsgTitle("极光商家卖出商品通知");
                jPush.setMsgContent("您好！娱悦APP提醒您！有商品已卖出！请及时查看！以便包装！");
                jPush.setExtras("8");

                List<String> stringList = new ArrayList<>();
                for (OrderItem orderItem: list) {
                    AppUser appUserMsg = appUserService.getAppUserMsg(orderItem.getConsumerId(),"");
                    if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                        log.info("极光别名==========" + appUserMsg.getJpushName());
                        stringList.add(appUserMsg.getJpushName());
                    }
                }
                if (CollectionUtils.isNotEmpty(stringList)) {
                    return getJPush(jPush, stringList, map, 0);
                }
            }
        } catch (Exception e) {
            log.info("极光商家卖出商品通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光商家卖出商品通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 极光商家退款通知 : 9 (jpushName,sourceId)
     * @param id
     * @param sourceId
     * @return
     */
    @RequestMapping("/sendRefund")
    @ResponseBody
    public ResponseData sendRefund(String id,String sourceId){
        JPush jPush = new JPush();
        try {
            log.info("极光商家退款通知开始-------------->>id"+id+"=====>>>sourceId"+sourceId);
            AppUser oldAppUser = appUserService.getAppUserMsg(id,"");
            AppUser appUser = appUserService.getAppUserMsg(sourceId,"");
            if (StringUtils.isNotNull(oldAppUser) && StringUtils.isNotNull(appUser)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","9");
                map.put("notice","极光商家退款通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好！娱悦APP提醒您！有商品已退款成功！请及时查看！");
                jPush.setMsgTitle("极光商家退款通知");
                jPush.setMsgContent("您好！娱悦APP提醒您！有商品已退款成功！请及时查看！");
                jPush.setExtras("9");

                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(oldAppUser.getJpushName()) || StringUtils.isNotEmpty(appUser.getJpushName())) {
                    log.info("极光别名==========oldAppUser==>>>>>" + oldAppUser.getJpushName());
                    log.info("极光别名==========appUser==>>>>>" + appUser.getJpushName());
                    stringList.add(oldAppUser.getJpushName());
                    stringList.add(appUser.getJpushName());
                }
                if (CollectionUtils.isNotEmpty(stringList)) {
                    return getJPush(jPush, stringList, map, 0);
                }
            }
        } catch (Exception e) {
            log.info("极光商家卖出商品通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光商家卖出商品通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }


    /**
     * 极光推送举报反馈作者通知 : 10 (jpushName,sourceId)
     * @param authorId
     * @param
     * @return
     */
    @RequestMapping("/sendReportByAuthorId")
    @ResponseBody
    public ResponseData sendReportByAuthorId(String authorId,String title){
        JPush jPush = new JPush();
        try {
            log.info("极光推送举报反馈作者通知-------------->>authorId:"+authorId);
            AppUser appUserMsg = appUserService.getAppUserMsg(authorId, "");
            if (StringUtils.isEmpty(title)){
                title = "您有视频";
            }else {
                title = "您的视频："+title;
            }
            if (StringUtils.isNotNull(appUserMsg)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","10");
                map.put("notice","极光推送举报反馈作者通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好！"+appUserMsg.getNickName()+title+"存在违规现象已被封禁！请及时查看！");
                jPush.setMsgTitle("极光推送举报反馈通知");
                jPush.setMsgContent("您好！您的视频有存在违规现象已被封禁！请及时查看！");
                jPush.setExtras("10");

                List<String> stringList = new ArrayList<>();
                if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush, stringList, map, 0);
                }
            }
        } catch (Exception e) {
            log.info("极光推送举报反馈作者通知失败！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光推送举报反馈作者通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }


    /**
     * 极光推送举报反馈举报用户通知
     * @param authorId
     * @param videoId
     * @return
     */
    @RequestMapping("/sendReportByUserId")
    @ResponseBody
    public ResponseData sendReportByUserId(String authorId,String videoId,String status){
        JPush jPush = new JPush();
        try {
            log.info("极光推送举报反馈举报用户通知-------------->>");
            UploadFile uploadFile = videoService.selectById(ResultJSONUtils.getHashValue("yuyue_upload_file_",authorId), videoId);
            String title = null;
            if ("10B".equals(status)){
                title = "存在违规现象已被封禁！请及时查看！";
            }else {
                title = "未存在违规现象！请及时查看！";
            }
            if (StringUtils.isNotNull(uploadFile)){
                Map<String, String> map = Maps.newHashMap();
                map.put("type","10");
                map.put("notice","极光推送举报反馈通知");
                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle("您好！您举报的视频:"+uploadFile.getTitle()+title);
                jPush.setMsgTitle("极光推送举报反馈举报用户通知");
                jPush.setMsgContent("您好！您举报的视频:"+uploadFile.getTitle()+title);
                jPush.setExtras("11");

                List<String> stringList = new ArrayList<>();
         /*       if (StringUtils.isNotEmpty(appUserMsg.getJpushName())) {
                    log.info("极光别名==========" + appUserMsg.getJpushName());
                    stringList.add(appUserMsg.getJpushName());
                    return getJPush(jPush, stringList, map, 0);
                }*/
            }
        } catch (Exception e) {
            log.info("极光推送举报反馈举报用户通知！");
            sendService.updateValid("10C",jPush.getId());
            return new ResponseData(CodeEnum.E_400.getCode(),"极光推送举报反馈举报用户通知失败！");
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }



    public ResponseData getJPush(JPush jPush, List<String> stringList, Map<String, String> map, int num){
        List<JPush> list = sendService.getValid(jPush.getNotificationTitle(),jPush.getMsgTitle(),
                jPush.getMsgContent(),jPush.getExtras(),jPush.getValid());
        if (CollectionUtils.isEmpty(list)) {
            sendService.insertJPush(jPush);
            if (0 == num) {
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
