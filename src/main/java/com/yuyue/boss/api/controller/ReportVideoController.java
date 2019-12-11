package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.Feedback;
import com.yuyue.boss.api.domain.ReportVideo;
import com.yuyue.boss.api.service.FeedbackService;
import com.yuyue.boss.api.service.ReportVideoService;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/reportVideo" ,produces = "application/json; charset=UTF-8")
public class ReportVideoController  extends BaseController {

    @Autowired
    private ReportVideoService reportVideoService;


    @RequestMapping("/reportVideoMenu")
    @ResponseBody
    @RequiresPermissions("ReportVideo:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData reportVideoMenu(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("获取视频反馈-------------->>/reportVideo/reportVideoMenu");
        String page=request.getParameter("page");
       /* String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");*/
        String status = request.getParameter("status");
        //String nickName = request.getParameter("nickName");
        String authorId = request.getParameter("authorId");
        String videoId = request.getParameter("videoId");

        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        PageHelper.startPage(Integer.parseInt(page), 10);
        List<ReportVideo> feedback =  reportVideoService.getReportVideos(status,authorId,videoId);
        PageInfo<ReportVideo> pageInfo=new PageInfo<>(feedback);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(feedback, currentPage,(int) total,pages);
    }
}
