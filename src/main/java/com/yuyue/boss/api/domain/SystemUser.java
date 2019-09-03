package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author ly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemUser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
//    登录名
    private String  loginName;
//    密码
    private String  password;
//    实名姓名
    private String  realName;
//    最后的登录时间
    private String  lastLoginTime;
//    手机号
    private String  phone;
//    是否启用
    private String  status;
//    创建人的ID
    private String  createUserId;

}
