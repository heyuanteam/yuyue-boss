package com.yuyue.boss.task;

import com.beust.jcommander.internal.Lists;
import com.yuyue.boss.api.controller.SendController;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.*;
import com.yuyue.boss.utils.ResultJSONUtils;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ScheduledTasks {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PayService payService;
    @Autowired
    private YuYueSiteService yuYueSiteService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private SendService sendService;

    /**
     * 订单支付超时判断,25分钟
     */
    @Scheduled(cron = "0 0/25 * * * *")
    public void outTime() {
        log.info("订单支付超时判断开始==================================>>>>>>>>>>>");
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        c.add(Calendar.MINUTE,-30);
        String startTime = dateFormat.format(c.getTime());
        log.info("========>>>"+startTime);
        List<Order> list = payService.findOrderList(startTime);
        if(CollectionUtils.isNotEmpty(list)){
            for (Order order: list) {
                log.info("订单"+order.getOrderNo()+"=====金额："+order.getMoney()+">>>>>>>>>>>已超时");
                payService.updateOrderStatus("ERROR", "支付超时", "10D", order.getOrderNo());
            }
        }
        log.info("订单支付超时判断结束==================================>>>>>>>>>>>");
    }

    /**
     * 业务员推广奖励,30分钟
     */
    @Scheduled(cron = "0 0/30 * * * *")
    public void toExtension() {
        log.info("业务员推广奖励开始==================================>>>>>>>>>>>");
        AppUser appUser = new AppUser();
        appUser.setUserType("6");
        List<AppUser> appUserMsgList = appUserService.getAppUserMsgList(appUser);
        List<AppUser> appList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(appUserMsgList)){
            for (AppUser user: appUserMsgList) {
                if (StringUtils.isNotEmpty(user.getPhone())) {
                    AppUser app = new AppUser();
                    app.setFatherPhone(user.getPhone());
                    app.setUserType("3");
                    appList = appUserService.getAppUserMsgList(app);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(appList)){
            for (AppUser users:appList) {
                List<MallShop> mallShop = sendService.findShopId("",users.getId());
                if (CollectionUtils.isNotEmpty(mallShop) && "10A".equals(users.getRewardStatus())) {
                    AppUser appUserMsg = appUserService.getAppUserMsg("", users.getFatherPhone());
                    if (StringUtils.isNotNull(appUserMsg) && "6".equals(appUserMsg.getUserType())) {
                        log.info(appUserMsg.getRealName()+"推荐的"+users.getRealName()+"=====成功发布商品啦！奖励后"+appUserMsg.getIncome());
                        appUserMsg.setRewardStatus("10B");
                        appUserMsg.setIncome(ResultJSONUtils.updateTotalMoney(appUserMsg, new BigDecimal(3), "+"));
                        appUserService.updateAppUser(appUserMsg);
                    }
                }
            }
        }
        log.info("业务员推广奖励结束==================================>>>>>>>>>>>");
    }

    /**
     * 6小时执行一次（执行娱悦现场定时任务）
     */
    @Scheduled(cron = "0 0 6 * * ?")
    private void updateYuYueSiteStartStatus(){
        log.info("执行娱悦现场定时任务：---------->");
        List<YuYueSite> yuYueSites = yuYueSiteService.searchYuYueSiteInfo("","10A","","","","");
        int times=0;
        if (StringUtils.isEmpty(yuYueSites)) return;
        log.info("查出的数据："+yuYueSites.size());
        for (YuYueSite yuyueSite: yuYueSites) {
            String startData = yuyueSite.getStartTime();
            if (StringUtils.isEmpty(startData)) continue;
            try {
                log.info(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                log.info(startData.split(" ")[0]);
                if (new SimpleDateFormat("yyyy-MM-dd").format(new Date()).equals(startData.split(" ")[0])){
                    yuyueSite.setStatus("10B");
                    times++;
                    yuYueSiteService.updateYuYueSite(yuyueSite);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("已修改次数："+times);

    }


    /**
     * 8小时执行一次（执行娱悦现场定时任务）
     */
    @Scheduled(cron = "0 0 7 * * ?")
    private void updateYuYueSiteEndStatus(){
        log.info("执行娱悦现场定时任务：---------->结束状态");
        List<YuYueSite> yuYueSites = yuYueSiteService.searchYuYueSiteInfo("","10A","","","","");
        int times=0;
        if (StringUtils.isEmpty(yuYueSites)) return;
        log.info("查出的数据："+yuYueSites.size());
        for (YuYueSite yuyueSite: yuYueSites) {
            String endTime = yuyueSite.getEndTime();
            if (StringUtils.isEmpty(endTime)) continue;
            try {
                if (new Date().after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime))){
                    yuyueSite.setStatus("10C");
                    times++;
                    yuYueSiteService.updateYuYueSite(yuyueSite);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("已修改次数："+times);
    }


    /**
     * 8小时执行一次（执行爆款定时任务）
     */
    @Scheduled(cron = "0 0 8 * * ?")
    private void updateCommodityEndStatus(){
        log.info("执行爆款定时任务：---------->结束状态");
        List<Commodity> commodityInfo = commodityService.getCommodityInfo("", "", "", "10C", "", "");
        if (StringUtils.isEmpty(commodityInfo)) return;
        for (Commodity commodity: commodityInfo
             ) {
            String endTime = commodity.getEndDate();
            if (StringUtils.isEmpty(endTime)) continue;
            try {
                if (new Date().after(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime))){
                    commodity.setStatus("10D");
                     commodityService.updateCommodityInfo(commodity);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
