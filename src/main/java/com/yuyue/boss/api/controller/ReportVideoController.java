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
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.ResultJSONUtils;
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
    @Autowired
    private SendController sendController;


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
        if (StringUtils.isEmpty(reportVideos)){
            return new ResponseData(list);
        }
        Map<String,List<ReportVideo>> map = reportVideos.stream().collect(Collectors.groupingBy(ReportVideo::getVideoId));
        for (String key:map.keySet()
             ) {
            List<UploadFile> uploadFiles = videoService.searchVideoInfo(key, "", "", "", "", "", "");
            UploadFile uploadFile = uploadFiles.get(0);
            uploadFile.setStatus(status);
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


    @RequestMapping("/updateReportStatus")
    @ResponseBody
    @RequiresPermissions("ReportVideo:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateReportStatus(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("修改举报状态-------------->>/reportVideo/updateReportStatus");
        String status = request.getParameter("status");
        String videoId = request.getParameter("videoId");
        String authorId = request.getParameter("authorId");
        if (StringUtils.isEmpty(status)){
            return new ResponseData(CodeEnum.E_90003.getCode(),"状态不可为空！");
        }else if (StringUtils.isEmpty(videoId)){
            return new ResponseData(CodeEnum.E_90003.getCode(),"状态不可为空！");
        }else if (StringUtils.isEmpty(videoId)){
            return new ResponseData(CodeEnum.E_90003.getCode(),"状态不可为空！");
        }
        UploadFile uploadFile = videoService.selectById(ResultJSONUtils.getHashValue("yuyue_upload_file_",authorId), videoId);
        if(StringUtils.isNull(uploadFile)){
            return new ResponseData("该视频不存在！");
        }
        if ("10A".equals(status) || "10B".equals(status) || "10C".equals(status)){
            List<ReportVideo> reportVideos =  reportVideoService.getReportVideos("","",videoId);
            if (StringUtils.isNull(reportVideos)){
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"未查询数据！");
            }else {
                List<String> userIds = reportVideoService.getUserIds(videoId);
                //修改举报列表状态
                reportVideoService.updateReportStatus(videoId,status);
                //修改视频状态及举报状态   封禁   第三个参数:举报状态   第四个：视频状态
                if ("10B".equals(status)){
                    videoService.updateReportStatus(authorId,videoId,"10B","10C");
                }else if ("10C".equals(status)){
                    videoService.updateReportStatus(authorId,videoId,"10C",uploadFile.getStatus());
                }

                //给作者发推送
                if ("10B".equals(status)){
                    sendController.sendReportByAuthorId(authorId,uploadFile.getTitle());
                }
                //给举报人发推送
                List<String> appUserList = new ArrayList<>();


                System.out.println(userIds);
                for (String userId: userIds
                     ) {
                    AppUser appUserMsg = appUserService.getAppUserMsg(userId, "");
                    if (StringUtils.isNotEmpty(appUserMsg.getJpushName())){
                        appUserList.add(appUserMsg.getJpushName());
                    }
                }

                System.out.println("---------------");
                System.out.println(appUserList);
                if (StringUtils.isNotEmpty(appUserList)){
                    sendController.sendReportByUserId(appUserList,uploadFile,status);
                }

            }
        }else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"参数错误！");
        }
        return new ResponseData();
    }
}
