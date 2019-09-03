package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMenu implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
//    菜单名称
    private String  menuName;
//    菜单code
    private String  menuCode;
//    上级ID
    private String  parentId;
//    是否启用
    private String  status;
//    请求路径
    private String  menuAction;
//    菜单权限
    private String  permissionKey;
    private String  createTime;
}
