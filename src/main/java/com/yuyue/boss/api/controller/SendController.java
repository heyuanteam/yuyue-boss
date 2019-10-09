package com.yuyue.boss.api.controller;

import com.yuyue.boss.api.domain.JPush;
import com.yuyue.boss.api.service.SendService;
import com.yuyue.boss.config.JPushClients;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.RedisUtil;
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
import java.util.List;

@RestController
@RequestMapping(value="/send", produces = "application/json; charset=UTF-8")
public class SendController extends BaseController{
    private static Logger log = LoggerFactory.getLogger(SendController.class);

    @Autowired
    private JPushClients jPushClients;
    @Autowired
    private SendService sendService;

    //极光推送
    @Value("${jpush.appKey}")
    private String appKey;

    @Value("${jpush.masterSecret}")
    private String masterSecret;

    @Value("${jpush.apnsProduction}")
    private boolean apnsProduction;

    /**
     * 极光推送，全部
     */
    @RequestMapping("/sendJPush")
    @ResponseBody
//    @Async // 异步方法
    public ResponseData sendJPush(HttpServletRequest request, HttpServletResponse response){
        log.info("极光推送-------------->>/sendJPush/sendJPush");
        getParameterMap(request,response);
//        用户ID,别名
//        List<String> aliasList = Arrays.asList("239");
//        String notificationTitle = "通知内容标题";
//        String msgTitle = "消息内容标题";
//        String msgContent = "消息内容";
        List<JPush> list = sendService.getValid();
        if(CollectionUtils.isNotEmpty(list)){
            for (int i = 0; i < list.size(); i++) {
                JPush jPush = list.get(i);
                if(jPush != null){
                    try {
                        jPushClients.sendToAll(jPush.getNotificationTitle(), jPush.getMsgTitle(), jPush.getMsgContent(), jPush.getExtras(),
                                apnsProduction,masterSecret,appKey);
                        sendService.updateValid("10B",jPush.getId());
                        return new ResponseData(CodeEnum.SUCCESS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("极光推送失败！");
                        sendService.updateValid("10C",jPush.getId());
                        return new ResponseData(CodeEnum.E_400.getCode(),"极光推送失败！");
                    }
                }
            }
        }
        return new ResponseData(CodeEnum.SUCCESS);
    }
}
