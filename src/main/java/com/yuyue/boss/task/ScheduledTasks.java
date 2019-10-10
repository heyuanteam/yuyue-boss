package com.yuyue.boss.task;

import com.yuyue.boss.api.domain.Commodity;
import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.CommodityService;
import com.yuyue.boss.api.service.YuYueSiteService;
import com.yuyue.boss.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private YuYueSiteService yuYueSiteService;
    @Autowired
    private CommodityService commodityService;


    /**
     * 8小时执行一次（执行娱悦现场定时任务）
     *
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


    /**
     * 8小时执行一次（执行娱悦现场定时任务）
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
