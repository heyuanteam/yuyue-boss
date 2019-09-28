package com.yuyue.boss.utils;

import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;

public class PageUtil {

    public static ResponseData getBeginPage(String page) {
        if (StringUtils.isEmpty(page)) page="1";
        if (!page.matches("[0-9]+")){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(),"传入的page类型错误！！");
        }

        int limit =15;
        int begin = (Integer.parseInt(page)-1)*limit;

        return new ResponseData(begin);
    }
}
