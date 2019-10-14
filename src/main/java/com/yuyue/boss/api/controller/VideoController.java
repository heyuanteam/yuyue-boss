package com.yuyue.boss.api.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;

import com.yuyue.boss.api.service.VideoService;

import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;


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

import java.util.List;


@RestController
@RequestMapping(value = "/video", produces = "application/json; charset=UTF-8")
@Slf4j
public class VideoController extends BaseController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private SendController sendController;

    /**
     * 视频审核
     * 获取视频列表
     * @param
     * @param request
     */
    @RequestMapping("/getVideoInfoList")
    @ResponseBody
    @RequiresPermissions("release:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getVideoInfoList(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        //交集参数

        String page=request.getParameter("page");
        String id = request.getParameter("id");
        String categoryId = request.getParameter("categoryId");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String title = request.getParameter("title");
        String status = request.getParameter("status");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";

        List<UploadFile> uploadFiles=null;
        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(categoryId)
                && StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)
                && StringUtils.isEmpty(title) && StringUtils.isEmpty(status) ){
            log.info("获取视频信息------------>>/video/getVideoInfoList");
                PageHelper.startPage(Integer.parseInt(page), 10);
                uploadFiles = videoService.getVideoInfoList();

        }else {
            log.info("视频搜索------------>>/video/searchVideoInfo");

            PageHelper.startPage(Integer.parseInt(page), 10);
            uploadFiles = videoService.searchVideoInfo(id,categoryId, startTime, endTime, title, status);
        }
        PageInfo<UploadFile> pageInfo=new PageInfo<>(uploadFiles);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(uploadFiles,currentPage,(int)total,pages);
    }


    /**
     * 视频审核
     * 修改视频状态
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateVideoStatus")
    @ResponseBody
    @RequiresPermissions("release:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateVideoStatus(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        String id = request.getParameter("id");
        String authorId = request.getParameter("authorId");
        String status = request.getParameter("status");
        if (StringUtils.isEmpty(id))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        else if (StringUtils.isEmpty(authorId))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"作者id为空！！");
        else if (StringUtils.isEmpty(status))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场状态为空！！");
        List<UploadFile> videoInfoList = videoService.searchVideoInfo(id,"","","","","");
        if (StringUtils.isEmpty(videoInfoList))return new ResponseData(CodeEnum.SUCCESS.getCode(),"未查询该视频！！");
        if ("10B".equals(status) || "10C".equals(status)){

            sendController.sendVideoJPush(id,authorId,status);
            videoService.updateVideo(id,authorId,status);
        } else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场状态类型错误！！");
        }
        return new ResponseData(CodeEnum.SUCCESS);

    }


    /**
     * 视频审核
     * 删除视频
     * @param uploadFile
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteVideoById")
    @ResponseBody
    @RequiresPermissions("release:remove")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateVideo(UploadFile uploadFile,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("视频更新------------>>/video/updateVideo");
        if (StringUtils.isEmpty(uploadFile.getId()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        else if (StringUtils.isEmpty(uploadFile.getAuthorId()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"艺人id为空！！");
        List<UploadFile> videoInfoList = videoService.searchVideoInfo(uploadFile.getId(),"","","","","");
        if (StringUtils.isEmpty(videoInfoList))return new ResponseData(CodeEnum.SUCCESS.getCode(),"未查询该视频！！");

        videoService.deleteVideoById(uploadFile.getId(),uploadFile.getAuthorId());
        return new ResponseData(CodeEnum.SUCCESS);
    }

}