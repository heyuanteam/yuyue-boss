package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemRole implements Serializable {
    private static final long serialVersionUID = 1L;
    private String  id;
    private String  systemUserId;
    private String  menuId;
    private String  permissionId;
    private String  status;
    private String  createTime;
}
