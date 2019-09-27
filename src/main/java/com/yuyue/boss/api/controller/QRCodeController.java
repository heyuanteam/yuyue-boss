package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.QRCodeUtil;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/qrCode", produces = "application/json; charset=UTF-8")
public class QRCodeController extends BaseController{


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
        String qrCodeName = request.getParameter("siteTitle")+request.getParameter("startTime").split(" ")[0];
        int size=Integer.parseInt(request.getParameter("size"));
        String url=System.getProperty("user.dir");

        log.info("system---------------------"+url);
        //logo本地路径
        //String logoLocalPath=System.getProperty("user.dir")+"/src/main/resources/logo/logo.png";
        //服务器路径
        String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        log.info(path);
        String logoPath = path+"static/logo/logo.png";
        log.info(logoPath);
        String s = QRCodeUtil.zxingCodeCreate(content, qrCodeName, size, logoPath);
        log.info("二维码路径："+s);
        return new ResponseData(s);
    }

}
