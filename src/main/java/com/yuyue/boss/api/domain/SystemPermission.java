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
//    系统用户ID
    private String  systemUserId;
//   菜单ID
    private String  menuId;
//    查看权限
    private String  menuKey;
//    保存权限
    private String  saveKey;
//    删除权限
    private String  removeKey;
//    权限路径
    private String  permissionAction;
//    是否启用
    private String  status;
    private String  createTime;
    private String  updateTime;
}
