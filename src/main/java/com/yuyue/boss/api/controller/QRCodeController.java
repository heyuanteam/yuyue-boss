package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.YuYueSite;
import com.yuyue.boss.api.service.YuYueSiteService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.QRCodeUtil;
import com.yuyue.boss.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
        /*String siteTitle=request.getParameter("id");
        String startTime=request.getParameter("startTime");
        String size=request.getParameter("size");
        String qrCodeName = request.getParameter("siteTitle")+request.getParameter("startTime").split(" ")[0];
        */
        int size = 500;
        if (StringUtils.isEmpty(content)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"现场id为空！！");
        }
        List<YuYueSite> yuYueSite = yuYueSiteService.getYuYueSiteInfo(content,1,2);
        if(StringUtils.isEmpty(yuYueSite)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"传入的现场id错误！！");
        }
        String qrCodeName = yuYueSite.get(0).getTitle()+yuYueSite.get(0).getStartTime().split(" ")[0];

        /*String url=System.getProperty("user.dir");
        log.info("system---------------------"+url);*/
        //logo本地路径
        //String logoLocalPath=System.getProperty("user.dir")+"/src/main/resources/logo/logo.png";
        //服务器路径
        /*String path = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        log.info(path);
        String logoPath = path+"static/logo/logo.png";
        log.info(logoPath);*/
        QRCodeUtil.zxingCodeCreate(content, qrCodeName, size, "http://101.37.252.177:8088/qrcode_image/logo.png");
        String qrCodePath="http://101.37.252.177:8088/qrcode_image/"+qrCodeName+".jpg";
        yuYueSiteService.insertQRCodePath(content,qrCodePath);
        return new ResponseData(qrCodePath);
    }


}
