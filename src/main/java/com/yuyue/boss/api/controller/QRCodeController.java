package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.YuYueSiteService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.QRCodeUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/qrCode", produces = "application/json; charset=UTF-8")
public class QRCodeController extends BaseController{

    @Autowired
    private YuYueSiteService yuYueSiteService;
    /**
     * 生成二维码
     * @param
     * @param request
     */
    @RequestMapping("/getQRCodeWithLogo")
    @ResponseBody
    public ResponseData getMenuList(HttpServletRequest request, HttpServletResponse response){
        getParameterMap(request,response);
        log.info("生成二维码------------>>/qrCode/getQRCodeWithLogo");
        String content=request.getParameter("id");
        String qrCodePath=null;
        /*
        //现场标题标题
        String siteTitle=request.getParameter("siteTitle");
        //开始时间
        String startTime=request.getParameter("startTime");
        //设置二维码尺寸
        String size=request.getParameter("size");
        String qrCodeName = request.getParameter("siteTitle")+request.getParameter("startTime").split(" ")[0];
        */
        try {
        int size = 500;
        if (StringUtils.isEmpty(content)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        }
        List<YuYueSite> yuYueSite = yuYueSiteService.getYuYueSiteInfo(content);
        if(StringUtils.isEmpty(yuYueSite)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"传入的现场id错误！！");
        }

        String qrCodeName = yuYueSite.get(0).getStartTime().split(" ")[0]+yuYueSite.get(0).getTitle();
        //获取logo路径
        QRCodeUtil.zxingCodeCreate(content, qrCodeName, size, System.getProperty("user.dir").replace("bin", "webapps")+"/qrcode_image/logo.png");
        qrCodePath="http://101.37.252.177:28088/qrcode_image/"+qrCodeName+".jpg";
        yuYueSiteService.insertQRCodePath(content,qrCodePath);
        }catch (Exception e){
            return new ResponseData(CodeEnum.PARAM_ERROR);
        }
        return new ResponseData(qrCodePath);
    }


}
