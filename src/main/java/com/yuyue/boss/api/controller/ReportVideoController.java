package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.Feedback;
import com.yuyue.boss.api.domain.ReportVideo;
import com.yuyue.boss.api.domain.UploadFile;
import com.yuyue.boss.api.mapper.VideoMapper;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.api.service.FeedbackService;
import com.yuyue.boss.api.service.ReportVideoService;
import com.yuyue.boss.api.service.VideoService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/reportVideo" ,produces = "application/json; charset=UTF-8")
public class ReportVideoController  extends BaseController {

    @Autowired
    private ReportVideoService reportVideoService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private VideoService videoService;


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
        List<UploadFile> list  =  new ArrayList<>();
        List<ReportVideo> reportVideos =  reportVideoService.getReportVideos(status,authorId,videoId);
        Map<String,List<ReportVideo>> map = reportVideos.stream().collect(Collectors.groupingBy(ReportVideo::getVideoId));
        for (String key:map.keySet()
             ) {
            List<UploadFile> uploadFiles = videoService.searchVideoInfo(key, "", "", "", "", "10B", "");
            UploadFile uploadFile = uploadFiles.get(0);
            AppUser appUserMsg = appUserService.getAppUserMsg(key, "");
            System.out.println(uploadFile.getAppUser());
            System.out.println("----------------------");
            System.out.println(appUserMsg);
            if (StringUtils.isNotNull(appUserMsg)){
                uploadFile.setAppUser(appUserMsg);
            }
            for (ReportVideo reportVideo:map.get(key)
                 ) {
                reportVideo.setAppUser(appUserService.getAppUserMsg(reportVideo.getUserId(),""));
            }
            uploadFile.setReportVideoList(map.get(key));
            if (StringUtils.isNotNull(uploadFile)){
                list.add(uploadFile);
            }

        }
//        PageInfo<ReportVideo> pageInfo=new PageInfo<>(reportVideos);
//        long total = pageInfo.getTotal();
//        int pages = pageInfo.getPages();
//        int currentPage = Integer.parseInt(page);
//        return new ResponseData(list, currentPage,(int) total,pages);
        return new ResponseData(list);
    }
}
