package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.api.service.SendService;
import com.yuyue.boss.api.service.VideoService;
import com.yuyue.boss.config.JPushClients;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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

@RestController
@RequestMapping(value = "/video", produces = "application/json; charset=UTF-8")
@Slf4j
public class VideoController extends BaseController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private SendService sendService;
    @Autowired
    private JPushClients jPushClients;
    @Autowired
    private AppUserService appUserService;

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
        String type=request.getParameter("type");
        String page=request.getParameter("page");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";

        List<UploadFile> uploadFiles=null;
        if ("get".equals(type)){
            log.info("获取视频信息------------>>/video/getVideoInfoList");
            String id=request.getParameter("id");
            String authorId=request.getParameter("authorId");
            if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(authorId) ) uploadFiles = videoService.getVideoInfoList(id,authorId,"");
            else {
                PageHelper.startPage(Integer.parseInt(page), 10);
                uploadFiles = videoService.getVideoInfoList("","","10A");
            }
        }else if("search".equals(type)){
            log.info("视频搜索------------>>/video/searchVideoInfo");
            String categoryId=request.getParameter("categoryId");
            String startTime=request.getParameter("startTime");
            String endTime=request.getParameter("endTime");
            String title=request.getParameter("title");
            String status=request.getParameter("status");
            PageHelper.startPage(Integer.parseInt(page), 10);
            uploadFiles = videoService.searchVideoInfo(categoryId, startTime, endTime, title, status);
        }else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"type参数错误！！");
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
        List<UploadFile> videoInfoList = videoService.getVideoInfoList(id, authorId,"");
        if (StringUtils.isEmpty(videoInfoList))return new ResponseData(CodeEnum.SUCCESS.getCode(),"未查询该视频！！");
        if ("10B".equals(status) || "10C".equals(status)){
            videoService.updateVideo(id,authorId,status);

            JPush jPush = new JPush();
            String str = "";
            if("10B".equals(status)){
                str = "通过! ";
            } else {
                str = "拒绝! ";
            }
            try {
                log.info("极光视频审核的通知开始-------------->>start");
                List<UploadFile> videoList = videoService.getVideoInfoList(id, authorId, "");
                if (CollectionUtils.isNotEmpty(videoList)){
                    Map<String, String> map = Maps.newHashMap();
                    map.put("type","5");
                    map.put("notice","视频审核的通知");
                    jPush.setId(RandomSaltUtil.generetRandomSaltCode(32));
                    jPush.setNotificationTitle("您好! "+videoList.get(0).getFilesName()+"视频审核"+str);
                    jPush.setMsgTitle(videoList.get(0).getTitle());
                    jPush.setMsgContent(videoList.get(0).getDescription());
                    jPush.setExtras("5");

                    List<JPush> list = sendService.getValid(jPush.getId());
                    if (CollectionUtils.isNotEmpty(list)) {
                        return new ResponseData(CodeEnum.SUCCESS.getCode(),"请不要重复点击！");
                    }
                    sendService.insertJPush(jPush);
                    AppUser appUserMsg = appUserService.getAppUserMsg(authorId);
                    log.info("极光别名=========="+appUserMsg.getJpushName());
                    List<String> stringList = new ArrayList<>();
                    stringList.add(appUserMsg.getJpushName());
                    jPushClients.sendToAliasList(stringList,jPush.getNotificationTitle(), jPush.getMsgTitle(), jPush.getMsgContent(), map);
                    sendService.updateValid("10B",jPush.getId());
                    log.info("极光视频审核的通知结束-------------->>SUCCESS");
                }
            } catch (Exception e) {
                log.info("极光视频审核的通知失败！");
                sendService.updateValid("10C",jPush.getId());
                return new ResponseData(CodeEnum.E_400.getCode(),"极光视频审核的通知失败！");
            }

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
        List<UploadFile> videoInfoList = videoService.getVideoInfoList(uploadFile.getId(),uploadFile.getAuthorId(),"");
        if (StringUtils.isEmpty(videoInfoList))return new ResponseData(CodeEnum.SUCCESS.getCode(),"未查询该视频！！");

        videoService.deleteVideoById(uploadFile.getId(),uploadFile.getAuthorId());
        return new ResponseData(CodeEnum.SUCCESS);
    }

}
