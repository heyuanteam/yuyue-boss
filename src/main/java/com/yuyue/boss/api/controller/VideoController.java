package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.UploadFile;
import com.yuyue.boss.api.service.VideoService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
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
import java.util.UUID;

@RestController
@RequestMapping(value = "/video", produces = "application/json; charset=UTF-8")
@Slf4j
public class VideoController extends BaseController {

    @Autowired
    private VideoService videoService;
    /**
     * 获取现场信息
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
        if (StringUtils.isEmpty(page))
            return new ResponseData(CodeEnum.E_90003.getCode(),"page不可为空");

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
     * 添加现场
     * @param request
     * @param response
     * @return
     */
 /*   @RequestMapping("/insertYuYueSite")
    @ResponseBody
    @RequiresPermissions("release:save")//具有 user:save 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData insertYuYueSite(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        String id = UUID.randomUUID().toString().replace("-", "").toUpperCase().toString();
        String title = request.getParameter("title");
        String imageUrl = request.getParameter("imageUrl");
        String siteAddr = request.getParameter("siteAddr");
        String mainPerson = request.getParameter("mainPerson");
        String personTotal = request.getParameter("personTotal");
        String qrCodePath = request.getParameter("qrCodePath");
        String admissionTime = request.getParameter("admissionTime");
        String startTime=request.getParameter("startTime");
        String endTime=request.getParameter("endTime");
        String status=request.getParameter("status");
        String jPushStatus=request.getParameter("jPushStatus");
        //YuYueSite yuYueSite=new YuYueSite(id,title,imageUrl,siteAddr,mainPerson,personTotal,"",qrCodePath,admissionTime,startTime,endTime,status,jPushStatus);
        videoService.insertVideo(new UploadFile());

        return new ResponseData(CodeEnum.SUCCESS);
    }*/
    /**
     * 视频更新
     * @param uploadFile
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateVideo")
    @ResponseBody
    @RequiresPermissions("release:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateVideo(UploadFile uploadFile,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("视频更新------------>>/video/updateVideo");
        if (StringUtils.isEmpty(uploadFile.getId()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        else if (StringUtils.isEmpty(uploadFile.getAuthorId()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"艺人id为空！！");
        else if (StringUtils.isEmpty(uploadFile.getStatus()))
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"视频状态为空！！");
        /*videoService.updateVideo(uploadFile);*/
        videoService.updateVideo(uploadFile.getId(),uploadFile.getAuthorId(),uploadFile.getStatus());
        return new ResponseData(CodeEnum.SUCCESS);
    }


    /**
     * 视频删除
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteVideoById")
    @ResponseBody
    @RequiresPermissions("release:remove")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteVideoById(HttpServletRequest request, HttpServletResponse response){
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
            /*UploadFile uploadFile = videoInfoList.get(0);
            uploadFile.setStatus(status);
            videoService.updateVideo(uploadFile);*/
            videoService.updateVideo(id,authorId,status);
        }
        /*else if ("10C".equals(status)){
            videoService.deleteVideoById(id,authorId);
        }*/
        else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场状态类型错误！！");
        }

        return new ResponseData(CodeEnum.SUCCESS);

    }
}
