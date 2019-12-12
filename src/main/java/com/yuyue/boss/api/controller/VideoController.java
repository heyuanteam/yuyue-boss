package com.yuyue.boss.api.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;

import com.yuyue.boss.api.service.AppService;
import com.yuyue.boss.api.service.ReportVideoService;
import com.yuyue.boss.api.service.VideoService;

import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;


import com.yuyue.boss.enums.Variables;
import com.yuyue.boss.utils.ResultJSONUtils;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping(value = "/video", produces = "application/json; charset=UTF-8")
public class VideoController extends BaseController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private SendController sendController;
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private AppService appService;
    @Autowired
    private ReportVideoService reportVideoService;


    /**
     * 获取举报视频的视频标题
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getVideoByTitle")
    @ResponseBody
    @RequiresPermissions("video:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getVideoByTitle(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        //交集参数
        log.info("视频审核 -获取视频列表-------------->>/video/getVideoByTitle");
        //String title=request.getParameter("title");
        List<ReturnValue> list = new ArrayList<>();
        //List<UploadFile> uploadFiles = videoService.searchVideoInfo("", "", "", "", title, "10B", "");
        List<ReportVideo> videoIds = reportVideoService.getVideoIds();
        if (StringUtils.isNotEmpty(videoIds)){

            for (ReportVideo reportVideo:videoIds
                 ) {
                ReturnValue returnValue =new ReturnValue();
                returnValue.setId(reportVideo.getVideoId());
                UploadFile uploadFile = videoService.selectById(ResultJSONUtils.getHashValue("yuyue_upload_file_", reportVideo.getAuthorId()), reportVideo.getVideoId());
                returnValue.setValue(uploadFile.getTitle());
                list.add(returnValue);
            }
        }
        return new ResponseData(list);
    }

    /**
     * 视频审核 -获取视频列表
     *
     * @param
     * @param request
     */
    @RequestMapping("/getVideoInfoList")
    @ResponseBody
    @RequiresPermissions("video:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getVideoInfoList(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        //交集参数
        log.info("视频审核 -获取视频列表-------------->>/video/getVideoInfoList");
        String page=request.getParameter("page");
        String id = request.getParameter("id");
        String categoryId = request.getParameter("categoryId");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String title = request.getParameter("title");
        String status = request.getParameter("status");
        String  nickName= request.getParameter("nickName");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";

        List<UploadFile> uploadFiles=null;
        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(categoryId)
                && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)
                && StringUtils.isEmpty(title) && StringUtils.isEmpty(status) & StringUtils.isEmpty(nickName)){
            log.info("获取视频信息------------>>/video/getVideoInfoList");
                PageHelper.startPage(Integer.parseInt(page), 10);
                uploadFiles = videoService.getVideoInfoList();

        }else {
            log.info("视频搜索------------>>/video/searchVideoInfo");
            PageHelper.startPage(Integer.parseInt(page), 10);
            uploadFiles = videoService.searchVideoInfo(id,categoryId, startTime, endTime, title, status,nickName);
        }
        PageInfo<UploadFile> pageInfo=new PageInfo<>(uploadFiles);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(uploadFiles,currentPage,(int)total,pages);
    }
    /**
     * 视频审核 - 上传视频
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/insertVideo")
    @ResponseBody
    @RequiresPermissions("video:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData insertVideo(@CurrentUser SystemUser systemUser, UploadFile uploadFile, HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("视频审核 - 上传视频----------------->/video/insertVideo");


        if (StringUtils.isEmpty(uploadFile.getCategoryId()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"视频种类id为空！！");
        else if (StringUtils.isEmpty(uploadFile.getTitle()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"标题名为空！！");
        else if (StringUtils.isEmpty(uploadFile.getFilesPath()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"视频路径为空！！");
        else if (StringUtils.isEmpty(uploadFile.getDescription()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"描述为空！！");
        else if (StringUtils.isEmpty(uploadFile.getVideoAddress()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"视频第一帧图片为空！！");
        uploadFile.setId(UUID.randomUUID().toString().toUpperCase().replace("-",""));
        uploadFile.setAuthorId(systemUser.getId());
        uploadFile.setFilesName(uploadFile.getTitle());
        uploadFile.setTableName(ResultJSONUtils.getHashValue("yuyue_upload_file_",systemUser.getId()));

        videoService.insertVideo(uploadFile);
        return new ResponseData(CodeEnum.SUCCESS);

    }

    /**
     * 视频审核 - 修改视频状态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateVideoStatus")
    @ResponseBody
    @RequiresPermissions("video:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateVideoStatus(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("视频审核 - 修改视频状态----------------->/video/updateVideoStatus");
        String id = request.getParameter("id");
        String authorId = request.getParameter("authorId");
        String status = request.getParameter("status");
        if (StringUtils.isEmpty(id))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        else if (StringUtils.isEmpty(authorId))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"作者id为空！！");
        else if (StringUtils.isEmpty(status))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场状态为空！！");
        List<UploadFile> videoInfoList = videoService.searchVideoInfo(id,"","","","","","");
        if (StringUtils.isEmpty(videoInfoList))return new ResponseData(CodeEnum.SUCCESS.getCode(),"未查询该视频！！");
        if ("10B".equals(status) || "10C".equals(status)){

            sendController.sendVideoJPush(id,authorId,status);
            sendController.sendFollowJPush(authorId,id);
            videoService.updateVideo(id,authorId,"",status);
        } else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场状态类型错误！！");
        }
        return new ResponseData(CodeEnum.SUCCESS);

    }


    /**
     * 视频审核 - 修改视频状态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateVideoCategory")
    @ResponseBody
    @RequiresPermissions("video:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateVideoCategory(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("视频审核 - 修改视频分类----------------->/video/updateVideoCategory");
        String id = request.getParameter("id");
        String authorId = request.getParameter("authorId");
        String categoryId = request.getParameter("categoryId");
        if (StringUtils.isEmpty(id))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"视频id为空！！");
        else if (StringUtils.isEmpty(authorId))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"作者id为空！！");
        else if (StringUtils.isEmpty(categoryId))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"视频分类为空！！");
        List<UploadFile> videoInfoList = videoService.searchVideoInfo(id,"","","","","","");
        if (StringUtils.isEmpty(videoInfoList))return new ResponseData(CodeEnum.SUCCESS.getCode(),"未查询该视频！！");
        List<VideoCategory> appMenuList = appService.getAPPMenuList("", "", "", 0);

        if (StringUtils.isNotEmpty(appMenuList)){
            videoService.updateVideo(id,authorId,categoryId,"");
        } else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"未查到该分类！！");
        }
        return new ResponseData(CodeEnum.SUCCESS);

    }


    /**
     * 视频审核 - 删除视频
     * @param
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteVideoById")
    @ResponseBody
    @RequiresPermissions("video:remove")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteVideoById(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("视频审核 - 删除视频----------------->/video/deleteVideoById");
        //视频id
        String id = request.getParameter("id");
        //作者id
        String authorId = request.getParameter("authorId");
        log.info("视频更新------------>>/video/updateVideo");
        if (StringUtils.isEmpty(id) )
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"视频id为空！！");
        else if (StringUtils.isEmpty(authorId))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"艺人id为空！！");
        List<UploadFile> videoInfoList = videoService.searchVideoInfo(id,"","","","","","");
        if (StringUtils.isEmpty(videoInfoList))
            return new ResponseData(CodeEnum.SUCCESS.getCode(),"未查询该视频！！");
        else{
            videoService.deleteVideoById(ResultJSONUtils.getHashValue("yuyue_upload_file_",authorId),id);
            String[] split = videoInfoList.get(0).getFilesPath().split("group1/");
            log.info(split[1]);
            this.storageClient.deleteFile(Variables.groupName,split[1]);
        }

        return new ResponseData(CodeEnum.SUCCESS);
    }

}
