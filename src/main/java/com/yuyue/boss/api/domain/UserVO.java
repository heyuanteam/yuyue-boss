package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Create by lujun.chen on 2018/09/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO extends SystemUserVO implements Serializable {
    private static final long serialVersionUID = 1L;
//    令牌
    private String token;

    /**
     * 权限列表
     */
    private List<String> permissions;

}
