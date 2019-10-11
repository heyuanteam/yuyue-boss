package com.yuyue.boss.api.controller;

import com.google.common.collect.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.JPush;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.SendService;
import com.yuyue.boss.api.service.YuYueSiteService;
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

    //极光推送类型
    private static final Map<String, Object> iosMap = new HashMap<>();
    static {
        iosMap.put("1","艺人审核的通知");//未接，不需要参数
        iosMap.put("2","广告商审核的通知");//不需要参数
        iosMap.put("3","关注人发视频的通知");//未接，需要参数
        iosMap.put("4","现场详情的通知");//未接，需要参数
        iosMap.put("5","视频审核的通知");//不需要参数
        iosMap.put("6","广告审核的通知");//未接，不需要参数
    }

    /**
     * 极光推送，全部
     */
    @RequestMapping("/sendJPush")
    @ResponseBody
//    @Async // 异步方法
    @LoginRequired
    public ResponseData sendJPush(@CurrentUser SystemUser systemUser,HttpServletRequest request, HttpServletResponse response){
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
                map.put("type","1");
                map.put("notice","艺人审核的通知");

                jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                jPush.setNotificationTitle(idList.get(0).getTitle());
                jPush.setMsgTitle(idList.get(0).getSiteAddr());
                jPush.setMsgContent(idList.get(0).getImageUrl());
                jPush.setExtras(map.toString());

                List<JPush> list = sendService.getValid(jPush.getId());
                if (CollectionUtils.isNotEmpty(list)) {
                    return new ResponseData(CodeEnum.SUCCESS.getCode(),"请不要重复点击发布！");
                }
                sendService.insertJPush(jPush);
                jPushClients.sendToAll(jPush.getNotificationTitle(), jPush.getMsgTitle(), jPush.getMsgContent(), map);
                sendService.updateValid("10B",jPush.getId());
                log.info("极光推送结束-------------->>SUCCESS");
                return new ResponseData(CodeEnum.SUCCESS);
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
}
