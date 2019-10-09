package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMenuVo extends SystemMenu implements Serializable {
    private static final long serialVersionUID = 1L;
//    菜单权限
    private String  menuKey;
}
