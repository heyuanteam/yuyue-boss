package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YuYueSite implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    //现场标题
    private String title;
    //图片地址（海报）
    private String imageUrl;
    //现场地址
    private String siteAddr;
    //负责人
    private String mainPerson;
    //现场总人数
    private String personTotal;
    //申请人数
    private String personSum;
    //二维码路径
    private String qrCodePath;
    //入场时间
    private String admissionTime;
    //开始时间
    private String startTime;
    //开始时间
    private String endTime;
    //现场状态
    private String status;
    //极光状态
    private String jPushStatus;
    //现场节目
    private List<SiteShow> siteShow;

}
