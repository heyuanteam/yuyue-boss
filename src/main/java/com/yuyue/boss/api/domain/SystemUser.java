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

}
