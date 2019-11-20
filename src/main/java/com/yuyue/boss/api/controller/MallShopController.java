package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.AdPrice;
import com.yuyue.boss.api.domain.MallShop;
import com.yuyue.boss.api.domain.Specification;
import com.yuyue.boss.api.domain.UploadFile;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.api.service.MallShopService;
import com.yuyue.boss.api.service.PayService;
import com.yuyue.boss.api.service.VideoService;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/mallShop" , produces = "application/json; charset=UTF-8")
public class MallShopController extends BaseController {

    private static  final  java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
    //public static Map<String,BigDecimal> addMoneyToMerchantMap = new HashMap<>();


    @Autowired
    private MallShopService mallShopService;
    @Autowired
    private SendController sendController;

    @Autowired
    private PayService payService;
    @Autowired
    private VideoService videoService;

    @Autowired
    private LoginService loginService;




    /**
     * (通过shopId查询商铺详情)
     * @param request
     * @param response
     * @return
     */

    @RequestMapping(value = "getMyMallShop")
    @ResponseBody
    @RequiresPermissions("hotGoods:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    public ResponseData getMyMallShop(HttpServletRequest request, HttpServletResponse response){
        ResponseData responseData = new ResponseData();
        log.info("查询我的商铺-------------->>/mallShop/getMyMallShop");
        getParameterMap(request, response);
        String shopId = request.getParameter("shopId");
        if (StringUtils.isEmpty(shopId)){
            responseData.setMessage("商铺id为空");
            return responseData;
        }
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        if (StringUtils.isNull(myMallShop)){
            responseData.setMessage("未查询到该商铺");
            return responseData;
        }
        List<Specification> specification = mallShopService.getSpecification(shopId);
        if (StringUtils.isEmpty(specification)){
            specification = new ArrayList<>();
        }
        myMallShop.setSpecifications(specification);
        responseData.setMessage("返回成功！");

        return new ResponseData(myMallShop);
    }


    /**
     * (通过userId查询商铺详情)查询我的商铺（我的广告）
     * @param
     * @param response
     * @return
     */
   /* @RequestMapping(value = "getMyMallShops")
    @ResponseBody
    @LoginRequired
    public ResponseData getMyMallShops(@CurrentUser AppUser user, HttpServletRequest request, HttpServletResponse response){
        ResponseData responseData = new ResponseData();
        log.info("查询我的商铺（我的广告）-------------->>/mallShop/getMyMallShops");
        getParameterMap(request, response);

        List<MallShop> myMallShops = mallShopService.getMyMallShops(user.getId());
        if (StringUtils.isEmpty(myMallShops)){
            responseData.setMessage("未查询到商铺");
            return responseData;
        }

        for (MallShop myShop:myMallShops
             ) {
            if ("10A".equals(myShop.getStatus())){
                Order order = payService.getOrderId(myShop.getOrderId());
                if ("10B".equals(order.getStatus())){
                    myShop.setStatus("10B");
                    mallShopService.updateMyMallShopInfo(myShop);
                }
            }
            //规格
            myShop.setImages(mallShopService.getShopImage(myShop.getShopId()));
            myShop.setAdPrice(myService.getAdvertisementFeeInfo(myShop.getPriceId()).get(0));
            List<Specification> specification = mallShopService.getSpecification(myShop.getShopId());
            myShop.setSpecifications(specification);
        }
        responseData.setMessage("返回成功！");
        return responseData;
    }

    */
    /**
     * 查询所有商铺(搜索)
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getAllMallShop")
    @RequiresPermissions("hotGoods:menu")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    @ResponseBody
    public ResponseData getAllMallShop(HttpServletRequest request, HttpServletResponse response){
        //ResponseData responseData = new ResponseData();
        log.info("查询所有符合条件的商铺-------------->>/mallShop/getAllMallShop");
        getParameterMap(request, response);
        String status = request.getParameter("status");//区域
        String page = request.getParameter("page");
        String pageSize = request.getParameter("pageSize");
        String content = request.getParameter("content");//分类、名称、详情
        String isRevise = request.getParameter("isRevise");

        if (StringUtils.isEmpty(page) || !page.matches("[0-9]+"))
            page = "1";
        if (StringUtils.isEmpty(pageSize) || !pageSize.matches("[0-9]+"))
            pageSize = "10";
        PageHelper.startPage(Integer.parseInt(page), Integer.parseInt(pageSize));
        List<MallShop> allMallShop = mallShopService.getAllMallShop(status,content,isRevise);
        PageInfo<MallShop> pageInfo=new PageInfo<>(allMallShop);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        int currentPage = Integer.parseInt(page);
        return new ResponseData(allMallShop, currentPage,(int) total,pages);

    }

    /**
     * 修改商铺状态
     * @param
     * @param request
     * @param response
     * @return
     */

    @RequestMapping(value = "updateMallShopStatus")
    @RequiresPermissions("hotGoods:save")//具有 user:detail 权限的用户才能访问此方法
    @LoginRequired
    @ResponseBody
    public ResponseData updateMallShopStatus(HttpServletRequest request,
                                             HttpServletResponse response){
        log.info("修改商铺状态------------->>/mallShop/updateMallShopStatus");
        getParameterMap(request, response);
        ResponseData responseData = new ResponseData();
        String shopId = request.getParameter("shopId");
        String status = request.getParameter("status");
        String isRevise = request.getParameter("isRevise");


        if(StringUtils.isEmpty(shopId)) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"爆款Id为空r！！");
        }
        //获取商铺信息
        MallShop mallShopByShopId = mallShopService.mallShopByShopId(shopId);
        if(StringUtils.isNotEmpty(isRevise)){
            mallShopByShopId.setIsRevise(isRevise);
            mallShopService.updateMyMallShopInfo(mallShopByShopId);
            return  responseData;
        }
        System.out.println(mallShopByShopId);
        if (StringUtils.isNull(mallShopByShopId)){
            return new ResponseData("未查询该数据a！！");
        }
        if (StringUtils.isEmpty(mallShopByShopId.getPriceId())){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"价格id为空b！");
        }
        AdPrice adFee = mallShopService.getAdFee(mallShopByShopId.getPriceId());
        if (StringUtils.isNull(adFee)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"价格对象中未查询到推广的时长c！！");
        }
        mallShopByShopId.setAdPrice(adFee);
        if (StringUtils.isNotEmpty(status)){
            if ("10A".equals(status)   || "10B".equals(status)   || "10C".equals(status)
                    || "10D".equals(status)   || "10E".equals(status ) ){
                if ("10C".equals(status)){
                    mallShopByShopId = spreadToVideo(mallShopByShopId);
                    sendController.sendMallShopInfoJPush(mallShopByShopId,mallShopByShopId.getStatus());
                }
            }
        }else {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"状态输入错误！d");
        }
        mallShopByShopId.setStatus(status);
        mallShopService.updateMyMallShopInfo(mallShopByShopId);
        return responseData;
    }

    public MallShop spreadToVideo(MallShop mallShop){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        //设置开始时间结束时间
        String adDuration = mallShop.getAdPrice().getAdDuration();

        //设置开始时间结束时间
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        String start = simpleDateFormat.format(instance.getTime());
        mallShop.setStartTime(start);
        instance.add(Calendar.MONTH, Integer.parseInt(adDuration));
        String end = simpleDateFormat.format(instance.getTime());
        mallShop.setEndTime(end);
        //插入到视频中
        List<UploadFile> videoInfoList = videoService.getVideoInfoList();

        for (UploadFile uploadFile:videoInfoList
        ) {
            //判断该视频下的爆款是否多余5条
            List<MallShop> mallShopByVideoId = mallShopService.getMallShopByVideoId(uploadFile.getId());
            if (mallShopByVideoId.size() >= 5)continue;
            else {
                mallShop.setVideoId(uploadFile.getId());
                break;
            }
        }

        return mallShop;
    }

    /**
     * 获取爆款
     * @param request
     * @param response
     * @return
     */
/*    @RequestMapping(value = "getMallShopByVideo")
    @ResponseBody
    public ResponseData getMallShopByVideo(HttpServletRequest request, HttpServletResponse response){
        log.info("获取爆款------------->>/mallShop/getMallShopByVideo");
        getParameterMap(request, response);
        ResponseData responseData = new ResponseData();
        String videoId = request.getParameter("videoId");
        if (StringUtils.isEmpty(videoId)){
            responseData.setMessage("视频id不可为空！");
            return responseData;
        }
        List<MallShop> mallShopByVideoId = mallShopService.getMallShopByVideoId(videoId);
        responseData.setMessage("返回成功！");
        return responseData;
    }*/


    /**
     * 添加商铺
     * @param user
     * @param request
     * @param response
     * @return
     */
 /*   @RequestMapping(value = "insertMyMallShop")
    @ResponseBody
    @LoginRequired
    public ResponseData insertMyMallShop(@CurrentUser AppUser user, HttpServletRequest request, HttpServletResponse response) {
        ResponseData responseData = new ResponseData();
        log.info("添加商铺-------------->>/mallShop/insertMyMallShop");
        getParameterMap(request, response);
        String shopId = request.getParameter("shopId");
        if (StringUtils.isEmpty(shopId)){
            responseData.setMessage("商铺id为空");
            return responseData;
        }
        *//*---------------------------------生成订单-------------------------------*//*
        String priceId = request.getParameter("priceId");
        String tradeType = request.getParameter("tradeType");
        List<AdPrice> advertisementFeeInfo = myService.getAdvertisementFeeInfo(priceId);
        if (StringUtils.isEmpty(advertisementFeeInfo)){
            responseData.setMessage("价格id传入错误！！");
            return responseData;
        }

        AdPrice adPrice = advertisementFeeInfo.get(0);
        BigDecimal bigDecimal = new BigDecimal(adPrice.getAdTotalPrice()).multiply(new BigDecimal(adPrice.getAdDiscount()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        Order order = new Order();
        order.setTradeType(tradeType);
        order.setMoney(bigDecimal);
        //传入商品id重新支付
        JSONObject jsonObject = null;
        *//*---------------------------------生成订单结束-------------------------------*//*
        MallShop myMallShop = mallShopService.getMyMallShop(shopId);
        //商铺已存在情况下重新支付，---------> 未支付状态或是 支付超时状态
        System.out.println(StringUtils.isNull(myMallShop));
        if (StringUtils.isNotNull(myMallShop) && StringUtils.isNotEmpty(myMallShop.getStatus())){
            //商铺未支付状态或是 支付超时状态
            if ("10A".equals(myMallShop.getStatus())  || "10D".equals(myMallShop.getStatus())){
                if(StringUtils.isNotEmpty(myMallShop.getOrderId())){
                    Order getOrder = payService.getOrderId(myMallShop.getOrderId());
                    if (StringUtils.isNull(getOrder)){
                        responseData.setMessage("未查询该订单！！");
                        return responseData;
                    }else if("10B".equals(getOrder.getStatus()) && "10A".equals(myMallShop.getStatus()) ){
                        //修改商铺状态
                        myMallShop.setStatus("10B");
                        mallShopService.insertMyMallShop(myMallShop);
                        return responseData;
                    }
                    //订单未支付状态     --->  去支付
                    else if("10A".equals(getOrder.getStatus())){
                        if ("GGWX".equals(order.getTradeType())) {
                            try {
                                jsonObject = payController.payWX(getOrder);
                                responseData.setStatus(Boolean.TRUE);
                                responseData.setMessage("添加成功！");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if ("GGZFB".equals(order.getTradeType())) {
                            try {
                                jsonObject = payController.payZFB(getOrder);
                                // String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                                responseData.setStatus(Boolean.TRUE);
                                //ResponseData.setMessage(jsonObject.getString("result"));
                                responseData.setMessage("添加成功！");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            ResponseData.setMessage("支付类型错误！");
                        }
                        return responseData;
                    }
                    //支付超时状态    支付失败   商铺到期   -->重新生成新的订单
                    else if ("10D".equals(myMallShop.getStatus())  ||
                                 "10C".equals(getOrder.getStatus()) ||
                                     "10D".equals(getOrder.getStatus())){

                        try {
                            jsonObject = payController.payYuYue(order, user);
                            //生成订单
                            if ("true".equals(jsonObject.getString("status"))){
                                //成功生成新的订单，获取订单ID
                                String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                                if (StringUtils.isEmpty(orderId)){
                                    responseData.setMessage("订单Id为空！！");
                                    return responseData;
                                }
                                myMallShop.setOrderId(orderId);
                                Order newOrder = payService.getOrderId(orderId);
                                if ("10B".equals(newOrder.getStatus()))
                                    myMallShop.setStatus("10B");
                                else
                                    myMallShop.setStatus("10A");
                                //ResponseData.setResult(jsonObject.get("result"));
                                responseData.setMessage("订单重新生成，等待审核！！");
                                responseData.setStatus(Boolean.TRUE);
                                mallShopService.insertMyMallShop(myMallShop);
                                return responseData;
                            }else {
                                responseData.setMessage("订单生成失败！！");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                responseData.setStatus(Boolean.TRUE);
                return responseData;
            }
            if ("10B".equals(myMallShop.getStatus())){
                responseData.setMessage("已添加,待审核！");
                responseData.setStatus(Boolean.TRUE);
            }
            if ("10C".equals(myMallShop.getStatus())){
                responseData.setMessage("该订单正在发布！");
                responseData.setStatus(Boolean.TRUE);
            }
            return responseData;
        }
        *//*---------------------------------------------新的商铺申请--------------------------------------------*//*
        else {
            //新的商铺申请
            if (StringUtils.isEmpty(shopId)){
                responseData.setMessage("商铺id不能为空！");
                return responseData;
            }else if (StringUtils.isEmpty(request.getParameter("category"))){
                responseData.setMessage("商品/服务分类不能为空！");
                return responseData;
            }else if (StringUtils.isEmpty(request.getParameter("commodityName"))){
                responseData.setMessage("商品/服务名称不能为空！");
                return responseData;
            }else if (StringUtils.isEmpty(request.getParameter("detail"))){
                responseData.setMessage("商品/服务介绍不能为空！");
                return responseData;
            }else if (StringUtils.isEmpty(request.getParameter("serviceType"))){
                responseData.setMessage("服务方式不能为空！");
                return responseData;
            }
            else if (StringUtils.isEmpty(request.getParameter("fare"))){
                responseData.setMessage("运费不能为空！");
                return responseData;
            }
            else if (StringUtils.isEmpty(request.getParameter("commodityPrice"))){
                responseData.setMessage("商铺价格不能为空！");
                return responseData;
            }
            //验证金额格式
            java.util.regex.Matcher match=pattern.matcher(request.getParameter("fare").toString());
            java.util.regex.Matcher pMatch=pattern.matcher(request.getParameter("commodityPrice").toString());
            if(match.matches()==false) {
                responseData.setMessage("运费格式输入错误！！！");
                return responseData;
            }if(pMatch.matches()==false) {
                responseData.setMessage("商铺价格输入错误！！！");
                return responseData;
            }
//            else if (StringUtils.isEmpty(request.getParameter("businessTime"))){
//                ResponseData.setMessage("营业时间不能为空！");
//                return ResponseData;
//            }
            else if (StringUtils.isEmpty(request.getParameter("merchantAddr"))){
                responseData.setMessage("商家地址不能为空！");
                return responseData;
            }else if (StringUtils.isEmpty(request.getParameter("merchantPhone"))){
                responseData.setMessage("商家电话不能为空！");
                return responseData;
            }
            MallShop mallShop =new MallShop();
            mallShop.setShopId(shopId);
            mallShop.setMerchantId(user.getId());
            mallShop.setCategory(request.getParameter("category"));
            mallShop.setCommodityName(request.getParameter("commodityName"));
            String commodityImage = request.getParameter("images");
            if (StringUtils.isNotEmpty(commodityImage)){
                String[] images = commodityImage.split(";");

                for ( Byte i = 0 ; i < images.length ; i++) {
                    ShopImage shopImage = new ShopImage();
                    shopImage.setImagePath(images[i]);
                    shopImage.setImageSort(i);
                    shopImage.setShopId(shopId);
                    System.out.println(images[i]);
                    mallShopService.insertShopImage(shopImage);
                }
            }

            mallShop.setDetail(request.getParameter("detail"));

//            String specifications= request.getParameter("specifications");
//            if (StringUtils.isNotEmpty(specifications)){
//                String[] specificationsArray = specifications.split(";");
//                for (String specification:specificationsArray
//                ) {
//
//                }
//            }

            mallShop.setServiceType(request.getParameter("serviceType"));
            mallShop.setFare(new BigDecimal(request.getParameter("fare")));
            mallShop.setCommodityPrice(new BigDecimal(request.getParameter("commodityPrice")));
            mallShop.setVideoPath(request.getParameter("remark"));
            mallShop.setBusinessTime(request.getParameter("businessTime"));
            mallShop.setBusinessStatus(request.getParameter("businessStatus"));
            mallShop.setMerchantAddr(request.getParameter("merchantAddr"));
            mallShop.setMerchantPhone(request.getParameter("merchantPhone"));
            mallShop.setServiceArea(request.getParameter("serviceArea"));
            mallShop.setFeeArea(request.getParameter("feeArea"));
            mallShop.setVideoPath(request.getParameter("videoPath"));
            mallShop.setRemark(request.getParameter("remark"));
            mallShop.setRemark("N");
            mallShopService.insertMyMallShop(mallShop);
            *//*----------------------------------------接支付------------------------------------------------*//*
            //新的商品推广申请
               try {
                  jsonObject = payController.payYuYue(order, user);
                  //生成订单
                   if ("true".equals(jsonObject.getString("status"))){
                       String orderId = JSON.parseObject(jsonObject.getString("result")).getString("orderId");
                       if (StringUtils.isEmpty(orderId)){
                           responseData.setMessage("订单Id为空！！");
                           return responseData;
                       }
                            mallShop.setOrderId(orderId);
                            Order getOrder = payService.getOrderId(orderId);
                            if ("10B".equals(getOrder.getStatus()))
                                mallShop.setStatus("10B");
                            else
                                mallShop.setStatus("10A");
                       responseData.setMessage("订单生成，等待审核！！");
                       responseData.setStatus(Boolean.TRUE);
                       responseData.setResult(jsonObject.get("result"));
                            mallShopService.insertMyMallShop(mallShop);
                            return responseData;
                        }else {
                       responseData.setMessage("订单生成失败！！");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            return responseData;
        }

    }
*/


    /**
     * 修改我的商铺状态
     * @param mallShop
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping(value = "updateMyMallShopStatus")
//    @LoginRequired
//    @ResponseBody
//    public ResponseData updateMyMallShopStatus(MallShop mallShop,HttpServletRequest request, HttpServletResponse response){
//        log.info("修改我的商铺状态------------->>/mallShop/updateMyMallShopStatus");
//        getParameterMap(request, response);
//        ResponseData responseData = new ResponseData();
//        String businessStatus = request.getParameter("businessStatus");
//        String shopId = request.getParameter("shopId");
//        if ("rest".equals(businessStatus) || "open".equals(businessStatus)){
//            mallShopService.updateMyMallShopStatus(businessStatus,shopId);
//            responseData.setMessage("修改成功！");
//        }else {
//            responseData.setMessage("状态错误！");
//        }
//
//
//        return responseData;
//    }


    /**
     * 修改商铺图片
     * @param request
     * @param response
     * @return
     */
/*    @RequestMapping(value = "insertShopImage")
    @ResponseBody
    @LoginRequired

    public ResponseData insertShopImage(HttpServletRequest request, HttpServletResponse response){
        log.info("删除商铺图片------------->>/mallShop/deleteShopImage");
        getParameterMap(request, response);
        ResponseData responseData = new ResponseData();
        String commodityImage = request.getParameter("images");
        String shopId = request.getParameter("shopId");
        if (StringUtils.isEmpty(shopId)){
            responseData.setMessage("商铺id为空！");
            return responseData;
        }
        if (StringUtils.isEmpty(commodityImage)){
            responseData.setMessage("图片路径为空！");
            return responseData;
        }else {
            String[] images = commodityImage.split(";");

            for ( Byte i = 0 ; i < images.length ; i++) {
                ShopImage shopImage = new ShopImage();
                shopImage.setImagePath(images[i]);
                shopImage.setImageSort(i);
                shopImage.setShopId(shopId);
                System.out.println(images[i]);
                mallShopService.insertShopImage(shopImage);
            }
            responseData.setMessage("图片修改成功！");
            return responseData;
        }
    }
*/

    /**
     * 删除商铺图片
     * @param request
     * @param response
     * @return
     */
/*
    @RequestMapping(value = "deleteShopImage")
    @ResponseBody
    @LoginRequired
    public ResponseData deleteShopImage(HttpServletRequest request, HttpServletResponse response){
        log.info("删除商铺图片------------->>/mallShop/deleteShopImage");
        getParameterMap(request, response);
        ResponseData ResponseData = new ResponseData();
        String imagePath = request.getParameter("imagePath");
        if (StringUtils.isEmpty(imagePath)){
            ResponseData.setMessage("图片路径为空！");
            return ResponseData;
        }
        mallShopService.deleteShopImage(imagePath);
        ResponseData.setMessage("图片删除成功！");
        ResponseData.setStatus(Boolean.TRUE);
        return ResponseData;
    }

*/

    /**
     * 通过商铺id -->查询商品规格
     * @param request
     * @param response
     * @return
     */
/*
    @RequestMapping(value = "getSpecification")
    @ResponseBody
    public ResponseData getSpecification(HttpServletRequest request, HttpServletResponse response){
        log.info("通过商铺id查询规格------------->>/mallShop/getSpecification");
        getParameterMap(request, response);
        ResponseData ResponseData = new ResponseData();
        String shopId = request.getParameter("shopId");
        if (StringUtils.isEmpty(shopId)){
            ResponseData.setMessage("商品id不能为空！！");
            return ResponseData;
        }
        List<Specification> specification = mallShopService.getSpecification(shopId);
        ResponseData.setStatus(Boolean.TRUE);
        ResponseData.setResult(specification);
        ResponseData.setMessage("返回成功");
        return ResponseData;

    }
*/

    /**
     * 通过规格id  -->查询规格
     * @param request
     * @param response
     * @return
     */
 /*   @RequestMapping(value = "getSpecificationById")
    @ResponseBody
    public ResponseData getSpecificationById(HttpServletRequest request, HttpServletResponse response){
        ResponseData ResponseData = new ResponseData();
        log.info("通过规格id查询规格------------->>/mallShop/getSpecificationById");
        getParameterMap(request, response);
        String commodityId = request.getParameter("commodityId");
        if (StringUtils.isEmpty(commodityId)){
            ResponseData.setMessage("规格id不能为空！！");
            return ResponseData;
        }
        Specification specification = mallShopService.getSpecificationById(commodityId);
        ResponseData.setStatus(Boolean.TRUE);
        ResponseData.setResult(specification);
        ResponseData.setMessage("返回成功");
        return ResponseData;

    }

    */
    /**
     * 编辑规格(添加修改)
     * @param commodityId
     * @param shopId
     * @param commodityDetail
     * @param commoditySize
     * @param commodityPrice
     * @param commodityReserve
     * @param imagePath
     * @param status
     * @param request
     * @param response
     * @return
     */
/*
    @RequestMapping(value = "editSpecification")
    @ResponseBody
    @LoginRequired
    public ResponseData editSpecification(String commodityId,String shopId ,String commodityDetail,String commoditySize,
                                          String commodityPrice,String commodityReserve,
                                          String imagePath,String status,
                                          HttpServletRequest request, HttpServletResponse response){
        ResponseData ResponseData = new ResponseData();
        Specification specification = new Specification();
        getParameterMap(request, response);

        if (StringUtils.isEmpty(commodityPrice)){
            ResponseData.setMessage("价格不能为空！");
            return ResponseData;
        }
        // 判断小数点后2位的数字的正则表达式
        java.util.regex.Matcher match=pattern.matcher(commodityPrice);
        if(match.matches()==false)
        {
            ResponseData.setMessage("价格输入错误！");
            return ResponseData;
        }else {
            specification.setCommodityPrice(new BigDecimal(commodityPrice));
        }if (StringUtils.isEmpty(commodityReserve)){
            ResponseData.setMessage("库存不能为空！");
            return ResponseData;
        }else if(commodityReserve.matches("[0-9]+")){
            specification.setCommodityReserve(Integer.parseInt(commodityReserve));
        }else {
            ResponseData.setMessage("库存不是整数类型！");
            return ResponseData;
        }if ("10A".equals(status)  || "10B".equals(status)){
            specification.setStatus(status);
        }else {
            ResponseData.setMessage("状态输入错误！");
            return ResponseData;
        }
        specification.setCommodityDetail(commodityDetail);
        specification.setCommodityReserve(Integer.parseInt(commodityReserve));
        specification.setCommoditySize(commoditySize);
        specification.setImagePath(imagePath);
        if (StringUtils.isEmpty(commodityId)){
            if (StringUtils.isEmpty(shopId)){
                ResponseData.setMessage("商铺id不能为空！");
                return ResponseData;
            }
            specification.setShopId(shopId);
            specification.setCommodityId(UUID.randomUUID().toString().replace("-","").toUpperCase());
            mallShopService.insertSpecification(specification);
            ResponseData.setMessage("规格添加成功！");

        }else {
            specification.setCommodityId(commodityId);
            mallShopService.updateSpecification(specification);
            ResponseData.setMessage("规格修改成功！");
        }
        ResponseData.setStatus(Boolean.TRUE);
        return ResponseData;
    }

*/


    /**
     * 添加商品规格
     * @param shopId
     * @param commodityDetail
     * @param commodityPrice
     * @param commodityReserve
     * @param imagePath
     * @param status
     * @param user
     * @param request
     * @param response
     * @return
     */
 /*     @RequestMapping(value = "insertSpecification")
    @ResponseBody
    @LoginRequired
  public ResponseData insertSpecification(String shopId ,String commodityDetail,String commoditySize,
                                            String commodityPrice,String commodityReserve,
                                            String imagePath,String status,
                                            @CurrentUser AppUser user,
                                            HttpServletRequest request, HttpServletResponse response){
        ResponseData ResponseData = new ResponseData();
        Specification specification = new Specification();
        specification.setCommodityId(UUID.randomUUID().toString().replace("-","").toUpperCase());
        // 判断小数点后2位的数字的正则表达式
       java.util.regex.Matcher match=pattern.matcher(commodityPrice);
        if(match.matches()==false)
        {
            ResponseData.setMessage("价格输入错误！");
            return ResponseData;
        }
        else {
            specification.setCommodityPrice(new BigDecimal(commodityPrice));
        }
        if(commodityReserve.matches("[0-9]+")){
            specification.setCommodityReserve(Integer.parseInt(commodityReserve));
        }else {
            ResponseData.setMessage("库存不是整数类型！");
            return ResponseData;
        }if ("10A".equals(status)  || "10B".equals(status)){
            specification.setStatus(status);
        }else {
            ResponseData.setMessage("状态输入错误！");
            return ResponseData;
        }
        specification.setShopId(shopId);
        specification.setCommodityDetail(commodityDetail);
        specification.setCommoditySize(commoditySize);
        specification.setImagePath(imagePath);
        mallShopService.insertSpecification(specification);
        ResponseData.setStatus(Boolean.TRUE);
        ResponseData.setResult(specification);
        ResponseData.setMessage("规格添加成功！");
        return ResponseData;
    }

    */
    /**
     * 删除规格
     * @param request
     * @param response
     * @return
     */
/*    @RequestMapping(value = "deleteSpecificationById")
    @ResponseBody
    @LoginRequired
    public ResponseData deleteSpecificationById(HttpServletRequest request, HttpServletResponse response){
        ResponseData ResponseData = new ResponseData();
        log.info("删除规格------------->>/mallShop/deleteSpecificationById");
        getParameterMap(request, response);
        String commodityId = request.getParameter("commodityId");
        if (StringUtils.isEmpty(commodityId)){
            ResponseData.setMessage("规格id不能为空！！");
            return ResponseData;
        }
        mallShopService.deleteSpecification(commodityId);
        ResponseData.setStatus(Boolean.TRUE);
        ResponseData.setMessage("删除成功！");
        return ResponseData;

    }

    */
    /**
     * 修改规格
     * @param request
     * @param response
     * @return
     */
/*
    @RequestMapping(value = "updateSpecification")
    @ResponseBody
    @LoginRequired
    public ResponseData updateSpecification(Specification specification,HttpServletRequest request, HttpServletResponse response){
        ResponseData ResponseData = new ResponseData();
        log.info("修改规格------------->>/mallShop/updateSpecification");
        getParameterMap(request, response);
        String commodityId = request.getParameter("commodityId");
        if (StringUtils.isEmpty(commodityId)){
            ResponseData.setMessage("规格id不能为空！");
            return ResponseData;
        }
        mallShopService.updateSpecification(specification);
        ResponseData.setStatus(Boolean.TRUE);
        ResponseData.setMessage("修改成功！");
        return ResponseData;

    }

*/

    /**
     *商户获取所有订单
     * @param appUser
     * @param request
     * @param response
     * @return
     */
 /*   @RequestMapping(value = "getOrderByShopId")
    @ResponseBody
    @LoginRequired
    public ResponseData getOrderByShopId(@CurrentUser  AppUser appUser,
                                  HttpServletRequest request, HttpServletResponse response){

        ResponseData ResponseData = new ResponseData();
        log.info("商户获取所有订单------------->>/mallShop/getOrderByShopId");
        getParameterMap(request, response);
        //String orderId = request.getParameter("orderId");
        //订单状态
        String status = request.getParameter("status");
        String orderNo = request.getParameter("orderId");

        //通过shopId获取该商铺的全部订单
        List<String> orderByShopId = null;
        //将结果打包
        List<ReturnOrder> returnOrders = new ArrayList<>();
        //获取我的商铺id
        List<MallShop> mallShops = mallShopService.myMallShopInfo(appUser.getId());
        if (StringUtils.isEmpty(mallShops)){
            ResponseData.setMessage("暂无商铺！");
            ResponseData.setStatus(Boolean.TRUE);
            return ResponseData;
        }
        for (MallShop mallShop:mallShops
             ) {
            String shopId = mallShop.getShopId();
            //获取商铺订单列表
            if (StringUtils.isEmpty(orderNo)){
                orderByShopId = mallShopService.getOrderToItem(shopId,"",status);
            }else {
                orderByShopId.add(orderNo);
            }if (StringUtils.isEmpty(orderByShopId)){
                continue;
            }else {
                for (String orderId:orderByShopId
                ) {
                    Order order = payService.getOrderId(orderId);
                    if ("10A".equals(order.getStatus()) ||"10C".equals(order.getStatus()) || "10D".equals(order.getStatus())){
                        continue;
                    }
                    ReturnOrder returnOrder =new ReturnOrder();
                    //根据订单id获取订单项
                    List<OrderItem> mallOrderItems = mallShopService.getMallOrderItem(orderId,shopId,"");
                    List<Specification> commodities = new ArrayList<>();
                    String  payAmount ="0";
                    for (OrderItem orderItem:mallOrderItems
                    ) {
                        Specification specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
                        //设置规格价格
                        specificationById.setCommodityPrice(orderItem.getCommodityPrice());
                        //设置规格购买数量
                        specificationById.setCommodityNum(orderItem.getCommodityNum());
                        //设置订单状态
                        specificationById.setStatus(orderItem.getStatus());
                        payAmount = orderItem.getCommodityPrice().multiply(BigDecimal.valueOf(orderItem.getCommodityNum())).toString();
                        commodities.add(specificationById);
                    }
                    //去订单号

                    AppUser appUserMsg = loginService.getAppUserMsg("", "",  mallOrderItems.get(0).getConsumerId());
                    //消费者名
                    returnOrder.setConsumerName(appUserMsg.getNickName());
                    returnOrder.setConsumerPhone(appUserMsg.getPhone());
                    returnOrder.setOrderNo(order.getOrderNo());
                    returnOrder.setOrderId(order.getId());
                    returnOrder.setCommodities(commodities);
                    returnOrder.setPayAmount(new BigDecimal(payAmount).add(mallOrderItems.get(0).getFare()));
                    returnOrder.setFare(mallOrderItems.get(0).getFare());
                    //下单时间
                    returnOrder.setCreateTime(mallOrderItems.get(0).getCreateTime());
                    //支付类型
                    returnOrder.setTradeType(order.getTradeType());
                    returnOrder.setStatus(order.getStatus());
                    returnOrders.add(returnOrder);
                }
            }
        }



        ResponseData.setMessage("查询成功！");
        ResponseData.setStatus(Boolean.TRUE);
        ResponseData.setResult(returnOrders);
        return ResponseData;


    }

*/
    /**
     *商户 获取 订单详情
     * @param appUser
     * @param request
     * @param response
     * @return
     */
/*
    @RequestMapping(value = "getOrderDetailByOrderId")
    @ResponseBody
    @LoginRequired
    public ResponseData getOrderDetailByOrderId(@CurrentUser  AppUser appUser,
                                         HttpServletRequest request, HttpServletResponse response){

        ResponseData ResponseData = new ResponseData();
        log.info("商户 获取 订单详情------------->>/mallShop/getOrderDetailByOrderId");
        getParameterMap(request, response);
        String orderId = request.getParameter("orderId");
        Order order = payService.getOrderId(orderId);
        if (StringUtils.isNull(order)){
            ResponseData.setMessage("查无该订单！");
            return ResponseData;
        }
        //获取商铺订单列表
        ReturnOrderDetail returnOrderDetail=new  ReturnOrderDetail();

        returnOrderDetail.setOrderId(orderId);
        returnOrderDetail.setOrderNo(order.getOrderNo());
        String createTime = order.getCreateTime();
        System.out.println(createTime);
        if (StringUtils.isNotNull(createTime)){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date= null;
            try {
                date = formatter.parse(createTime);
                returnOrderDetail.setCreateTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        List<Specification> commodities = new ArrayList<>();
        List<OrderItem> mallOrderItems = mallShopService.getMallOrderItem(orderId, "","");
        String addressId = mallOrderItems.get(0).getAddressId();
        MallAddress mallAddress = mallShopService.getMallAddress(addressId);
        returnOrderDetail.setMallAddress(mallAddress);
        for (OrderItem orderItem:mallOrderItems
                ) {
                    Specification specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
                    //设置规格价格
                    specificationById.setCommodityPrice(orderItem.getCommodityPrice());
                    //设置规格购买数量
                    specificationById.setCommodityNum(orderItem.getCommodityNum());
                    commodities.add(specificationById);
                }

        returnOrderDetail.setCommodities(commodities);
        ResponseData.setMessage("查询成功！");
        ResponseData.setStatus(Boolean.TRUE);
        ResponseData.setResult(returnOrderDetail);
        return ResponseData;

    }
*/


    /**
     *消费者 获取 订单列表
     * @param appUser
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping(value = "getOrderByConsumerId")
//    @ResponseBody
//    @LoginRequired
//    public ResponseData getOrderByConsumerId(@CurrentUser  AppUser appUser,
//                                                HttpServletRequest request, HttpServletResponse response){
//
//        ResponseData ResponseData = new ResponseData();
//        log.info("消费者 获取 订单列表------------->>/mallShop/getOrderByConsumerId");
//        getParameterMap(request, response);
//
//        String status = request.getParameter("status");
//        String consumerId = appUser.getId();
//        //获取商城中我的订单列表
//        List<Order> scOrder = payService.getSCOrder(consumerId, status);
//        List<ReturnOrderDetail> returnOrderDetailList = new ArrayList<>();
//        if (StringUtils.isNull(scOrder)){
//            ResponseData.setMessage("暂无订单！");
//            ResponseData.setStatus(Boolean.TRUE);
//            ResponseData.setResult(scOrder);
//            return ResponseData;
//        }
//        //订单
//        for (Order order:scOrder
//             ) {
//            //订单状态为10A的不展示了（老板不要）
//            if ("10A".equals(order.getStatus())){
//                continue;
//            }
//            String orderId = order.getId();
//            ReturnOrderDetail returnOrderDetail = new ReturnOrderDetail();
//            String createTime = order.getCreateTime();
//            if (StringUtils.isNotNull(createTime)){
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date= null;
//                try {
//                    date = formatter.parse(createTime);
//                    returnOrderDetail.setCreateTime(date);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//            List<Specification> commodities = new ArrayList<>();
//            //获取订单中每个订单项
//            List<OrderItem> orderItems= mallShopService.getMallOrderItem(orderId,"","");
//            Specification specificationById = null;
//            for (OrderItem orderItem:orderItems
//                 ) {
//
//                specificationById = mallShopService.getSpecificationById(orderItem.getCommodityId());
//                if ("10B".equals(order.getStatus()) && "10A".equals(orderItem.getStatus())){
//                    orderItem.setStatus("10B");
//                    mallShopService.editMallOrderItem(orderItem);
//                }
//                //设置规格购买数量
//                specificationById.setCommodityNum(orderItem.getCommodityNum());
//                //设置规格价格
//                specificationById.setCommodityPrice(orderItem.getCommodityPrice());
//                //设置订单的状态
//                specificationById.setStatus(order.getStatus());
//                commodities.add(specificationById);
//            }
//
//            returnOrderDetail.setOrderId(order.getId());
//            returnOrderDetail.setOrderNo(order.getOrderNo());
//
//            returnOrderDetail.setPayAmount(order.getMoney());
//            MallShop myMallShop = mallShopService.getMyMallShop(orderItems.get(0).getShopId());
//            returnOrderDetail.setCommodities(commodities);
//            returnOrderDetail.setFare(orderItems.get(0).getFare());
//            returnOrderDetail.setStatus(orderItems.get(0).getStatus());
//            returnOrderDetail.setTradeType(order.getTradeType());
//            returnOrderDetail.setMerchantAddr(myMallShop.getMerchantAddr());
//            returnOrderDetail.setMerchantPhone(myMallShop.getMerchantPhone());
//            returnOrderDetailList.add(returnOrderDetail);
//        }
//
//        ResponseData.setMessage("查询成功！");
//        ResponseData.setStatus(Boolean.TRUE);
//        ResponseData.setResult(returnOrderDetailList);
//        return ResponseData;
//
//    }
//
//
//


}
