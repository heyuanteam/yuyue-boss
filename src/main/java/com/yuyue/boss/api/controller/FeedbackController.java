package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.ArtistReview;
import com.yuyue.boss.api.domain.Feedback;
import com.yuyue.boss.api.service.FeedbackService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/feedback" ,produces = "application/json; charset=UTF-8")
@Slf4j
public class FeedbackController extends BaseController {

    @Autowired
    private FeedbackService feedbackService;


    @RequestMapping("/getFeedbackInfo")
    @ResponseBody
    @RequiresPermissions("opinion:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getFeedbackInfo(HttpServletRequest request, HttpServletResponse response){

        getParameterMap(request,response);

        String page=request.getParameter("page");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String status = request.getParameter("status");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        List<Feedback> feedback = null;
        if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate) && StringUtils.isEmpty(status)){
            log.info("获取用户反馈-------------->>/feedback/getFeedbackInfo");
            PageHelper.startPage(Integer.parseInt(page), 10);
            feedback = feedbackService.getAllFeedback();
        }else {
            log.info("搜索用户反馈-------------->>/feedback/getFeedbackInfo");
            PageHelper.startPage(Integer.parseInt(page), 10);
            feedback = feedbackService.getFeedback(startDate, endDate, status);
        }
        PageInfo<Feedback> pageInfo=new PageInfo<>(feedback);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(feedback, currentPage,(int) total,pages);
    }

    /**
     * 修改用户反馈
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateFeedbackInfo")
    @ResponseBody
    @RequiresPermissions("opinion:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateFeedbackInfo(HttpServletRequest request, HttpServletResponse response){

        getParameterMap(request,response);
        log.info("修改用户反馈-------------->>/feedback/updateFeedbackInfo");
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        if (StringUtils.isEmpty(id)){
            return new ResponseData(CodeEnum.PARAM_ERROR);
        }else if(StringUtils.isEmpty(status)){
            return new ResponseData(CodeEnum.PARAM_ERROR);
        }
        feedbackService.updateFeedback(id,status);

        return new ResponseData(CodeEnum.SUCCESS);
    }

    /**
     * 删除用户反馈
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteFeedback")
    @ResponseBody
    @RequiresPermissions("opinion:remove")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteFeedback(HttpServletRequest request, HttpServletResponse response){

        getParameterMap(request,response);
        log.info("删除用户反馈-------------->>/feedback/deleteFeedback");
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id)){
            return new ResponseData(CodeEnum.PARAM_ERROR);
        }

        feedbackService.deleteFeedback(id);
        return new ResponseData(CodeEnum.SUCCESS);


    }
}
