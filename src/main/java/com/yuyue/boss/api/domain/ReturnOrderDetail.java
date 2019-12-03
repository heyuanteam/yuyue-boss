package com.yuyue.boss.api.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private AppUser appUser;
    private MallAddress mallAddress;
}
