package com.yuyue.boss.task;

import com.yuyue.boss.api.domain.Order;
import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.PayService;
import com.yuyue.boss.api.service.YuYueSiteService;
import com.yuyue.boss.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PayService pyService;
    @Autowired
    private YuYueSiteService yuYueSiteService;

    /**
     * 订单支付超时判断,25分钟
     */
    @Scheduled(cron = "0 0/25 * * * *")
    public void outTime() {
        log.info("订单支付超时判断开始==================================>>>>>>>>>>>");
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        c.add(Calendar.MINUTE,-30);
        String startTime = dateFormat.format(c.getTime());
        System.out.println("========>>>"+startTime);
        List<Order> list = pyService.findOrderList(startTime);
        if(CollectionUtils.isNotEmpty(list)){
            for (Order order: list) {
                log.info("订单"+order.getOrderNo()+"=====金额："+order.getMoney()+">>>>>>>>>>>已超时");
                pyService.updateOrderStatus("ERROR", "支付超时", "10D", order.getOrderNo());
            }
        }
        log.info("订单支付超时判断结束==================================>>>>>>>>>>>");
    }

    /**
     * 8小时执行一次（执行娱悦现场定时任务）
     */
    @Scheduled(cron = "0 0 8 * * ?")
    private void updateYuYueSiteStartStatus(){
        log.info("执行娱悦现场定时任务：---------->");
        List<YuYueSite> yuYueSites = yuYueSiteService.getYuYueSiteInfo("","10A");
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
    @Scheduled(cron = "0 0 8 * * ?")
    private void updateYuYueSiteEndStatus(){
        log.info("执行娱悦现场定时任务：---------->结束状态");
        List<YuYueSite> yuYueSites = yuYueSiteService.getYuYueSiteInfo("","10B");
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



}
