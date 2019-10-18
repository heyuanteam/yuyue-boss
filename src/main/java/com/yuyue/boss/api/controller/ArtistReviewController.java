package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.ArtistReview;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.api.service.ArtistReviewService;
import com.yuyue.boss.enums.CodeEnum;
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
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private SendController sendController;

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
            PageHelper.startPage(Integer.parseInt(page), 10);
            artistReviewList = artistReviewService.searchArtistReviewList(artistReview);
        }
        PageInfo<ArtistReview> pageInfo=new PageInfo<>(artistReviewList);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(artistReviewList, currentPage,(int) total,pages);

    }
    /**
     * 修改审核
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateArtistReviewInfo")
    @ResponseBody
    @RequiresPermissions("show:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateArtistReviewInfo(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("修改审核-------------->>//artistReview/updateArtistReviewInfo");
        String id =request.getParameter("id");
        String status = request.getParameter("status");
        ArtistReview artistReview = new ArtistReview();
        artistReview.setId(id);
        List<ArtistReview> artistReviewList = artistReviewService.searchArtistReviewList(artistReview);
        if (StringUtils.isEmpty(artistReviewList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"参数：id不合法！！");
        }
        if("10B".equals(status) || "10C".equals(status)){
            AppUser appUserMsg = appUserService.getAppUserMsg(artistReviewList.get(0).getUserId(),"");
            sendController.sendShowJPush(artistReviewList.get(0),appUserMsg,status);

            artistReviewService.updateArtistReviewStatus(id,status);
            if ("10B".equals(status)){
                if (StringUtils.isNull(appUserMsg))
                    return new ResponseData("该用户不存在");

                if ("1".equals(appUserMsg.getUserType()))
                    appUserMsg.setUserType("2");
                if ("3".equals(appUserMsg.getUserType()))
                    appUserMsg.setUserType("4");
                appUserService.updateAppUser(appUserMsg);
            }
        }else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"参数：status不合法！！");
        }
        return new ResponseData();

    }

    /**
     * 删除审核
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteArtistReviewById")
    @ResponseBody
    @RequiresPermissions("show:remove")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteArtistReviewById(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("删除审核-------------->>/artistReview/deleteArtistReviewById");
        String id =request.getParameter("id");
        artistReviewService.deleteArtistReviewById(id);
        return new ResponseData();
    }


}
