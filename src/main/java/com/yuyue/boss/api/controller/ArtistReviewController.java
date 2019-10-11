package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.ArtistReview;
import com.yuyue.boss.api.service.ArtistReviewService;
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

@RestController
@RequestMapping(value = "/artistReview" ,produces = "application/json; charset=UTF-8")
@Slf4j
public class ArtistReviewController extends BaseController {

    @Autowired
    private ArtistReviewService artistReviewService;


    /**
     * 获取艺人申请
     * @param artistReview
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getArtistReviewInfo")
    @ResponseBody
    @RequiresPermissions("show:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getArtistReviewInfo(ArtistReview artistReview, HttpServletRequest request, HttpServletResponse response){

        getParameterMap(request,response);
        log.info("获取艺人申请-------------->>/artistReview/getArtistReviewInfo");
        String page=request.getParameter("page");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        List<ArtistReview> artistReviewList = null ;
        if (StringUtils.isNull(artistReview)){
            PageHelper.startPage(Integer.parseInt(page), 10);
           artistReviewList = artistReviewService.getArtistReviewList();

        }else {
            artistReviewList = artistReviewService.searchArtistReviewList(artistReview);
        }




        return new ResponseData(artistReviewList);
    }

    @RequestMapping("/updateArtistReviewInfo")
    @ResponseBody
    @RequiresPermissions("show:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateArtistReviewInfo(HttpServletRequest request, HttpServletResponse response){

        String id =request.getParameter("id");
        String status = request.getParameter("status");
        artistReviewService.updateArtistReviewStatus(id,status);
        return new ResponseData();
    }


    @RequestMapping("/deleteArtistReviewInfo")
    @ResponseBody
    @RequiresPermissions("show:remove")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteArtistReviewInfo(HttpServletRequest request, HttpServletResponse response){



        return new ResponseData();
    }


}
