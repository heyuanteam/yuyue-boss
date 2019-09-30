package com.yuyue.boss.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import lombok.Data;

import java.util.List;

@Data
public class PageUtil{

    //当前页
    private int pageNum = 1;
    //页码数
    private int pageSize = 10;
    //总页数
    private int pages = 1;
    //总条数
    private long total = 0L;
    //数据
    private List list;

    public PageUtil(List list) {
        PageInfo pageInfo = new PageInfo<>(list);
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.pages = pageInfo.getPages();
        this.total = pageInfo.getTotal();
        this.list = list;
    }

    public static void getPage(String page) {
        if (StringUtils.isEmpty(page)){
            page="1";
        }
        if (page.matches("[0-9]+")){
            int pageSize = 10;
            int pageNum = (Integer.parseInt(page) - 1) * pageSize;
            //这是把页码和条数写了上方便在浏览器上写
            PageHelper.startPage(pageNum,pageSize);
        }
    }

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
