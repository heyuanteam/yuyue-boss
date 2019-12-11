package com.yuyue.boss.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnValue implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String value;
}
