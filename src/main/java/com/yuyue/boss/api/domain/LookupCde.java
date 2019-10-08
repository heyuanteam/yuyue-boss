package com.yuyue.boss.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LookupCde implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
//    字典名称
    private String  typeName;
//    字典code
    private int  sort;
//    是否启用
    private String  status;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date  updateTime;
}
