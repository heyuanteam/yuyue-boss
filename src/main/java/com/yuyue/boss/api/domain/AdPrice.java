package com.yuyue.boss.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdPrice implements Serializable {
    private static final long serialVersionUID = 1L;
    private String priceId;
    //配置类型
    private String adDuration;
    //类型码
    private String adTotalPrice;
    //配置状态
    private String adDiscount;
    //创建时间
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
