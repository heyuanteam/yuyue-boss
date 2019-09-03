package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemPermission implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
//    权限名称
    private String  permissionName;
//    权限key
    private String  permissionKey;
//    上级ID
    private String  parentId;
//    权限code
    private String  permissionCode;
//    权限路径
    private String  permissionAction;
    private String  createTime;
}
