package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.Commodity;
import com.yuyue.boss.api.service.CommodityService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/commodity" ,produces = "application/json; charset=UTF-8")
public class CommodityController extends BaseController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SendController sendController;

    /**
     * 获取爆款列表及搜索
     * @param commodity
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getCommodityList")
    @ResponseBody
    @RequiresPermissions("explosive:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getCommodityList(Commodity commodity,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        String page=request.getParameter("page");
        String commodityId =request.getParameter("commodityId");
        String commodityName =request.getParameter("commodityName");
        String category = request.getParameter("category");
        String status = request.getParameter("status");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";

        PageHelper.startPage(Integer.parseInt(page), 10);
        System.out.println(commodity);
        List<Commodity> commodityInfo = commodityService.getCommodityInfo(commodityId,commodityName,category,status,startTime,endTime);
        PageInfo<Commodity> pageInfo=new PageInfo<>(commodityInfo);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(commodityInfo, currentPage,(int) total,pages);
    }

    /**
     * 获取爆款详情
     * @param id
     * @return
     */
    @RequestMapping("/getCommodityInfoById")
    @ResponseBody
    @RequiresPermissions("explosive:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getCommodityInfoById(String id){
        if (StringUtils.isNull(id)){
            return new ResponseData(CodeEnum.E_90003);
        }
        Commodity commodityInfoById = commodityService.getCommodityInfoById(id);
        return new ResponseData(commodityInfoById);
    }


    /**
     * 删除爆款
     * @param id
     * @return
     */
    @RequestMapping("/deleteCommodity")
    @ResponseBody
    @RequiresPermissions("explosive:remove")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData deleteCommodity(String id){
        if (StringUtils.isNull(id)){
            return new ResponseData(CodeEnum.E_90003);
        }
        Commodity commodityInfoById = commodityService.getCommodityInfoById(id);
        if (StringUtils.isNull(commodityInfoById)){
            return new ResponseData("未查询该数据！！");
        }
        else commodityService.deleteCommodity(id);
        return new ResponseData();
    }


    /**
     * 修改商品状态及发布时间
     * @param commodity
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/updateCommodityInfo")
    @ResponseBody
    @RequiresPermissions("explosive:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData updateCommodityInfo(Commodity commodity,HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        if (StringUtils.isNull(commodity.getCommodityId())){
            return new ResponseData(CodeEnum.E_90003);
        }
        Commodity commodityInfoById = commodityService.getCommodityInfoById(commodity.getCommodityId());
        if (StringUtils.isNull(commodityInfoById)){
            return new ResponseData("未查询该数据！！");
        }
        if (StringUtils.isEmpty(commodity.getStatus()))
            return new ResponseData(CodeEnum.E_90003);
        else if("10E".equals(commodity.getStatus()))
            commodityService.updateCommodityInfo(commodity);
        else if("10C".equals(commodity.getStatus())){
            sendController.sendCommodityInfoJPush(commodityInfoById,commodity.getStatus());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            String start = simpleDateFormat.format(instance.getTime());
            commodity.setStartDate(start);
            instance.add(Calendar.MONTH, 3);
            String end = simpleDateFormat.format(instance.getTime());
            commodity.setEndDate(end);

 /*           Date parseStart = null;
            Date parseEnd=null;
            try {
                 parseStart = simpleDateFormat.parse(start);

                instance.add(Calendar.MONTH, 3);

                String end = simpleDateFormat.format(instance.getTime());
                parseEnd = simpleDateFormat.parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
*/
            System.out.println("开始时间："+commodity.getStartDate());
            System.out.println("结束时间："+commodity.getEndDate());

            commodityService.updateCommodityInfo(commodity);
        }
        return new ResponseData();
    }

}
