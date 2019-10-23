package com.yuyue.boss.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 艺人演出申请类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistReview implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //    用户ID
    private String userId;
    //    姓名或团队名称
    private String teamName;
    //    人数
    private String size;
    //    现住地
    private String address;
    //    分类ID
    private String categoryId;
    //    节目名称
    private String description;
    //    手机
    private String phone;
    //    视频地址
    private String videoAddress;
    //视频图片路径
    private String imageAddress;
    //    邮箱
    private String mail;
    //    微信
    private String weChat;
    //    演出申请状态
    private String status;
    //开始时间（该属性用于搜索）
    private String startDate;
    //结束时间（该属性用于搜索）
    private String endDate;

    //
    private AppUser appUser;
}
