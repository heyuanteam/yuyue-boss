package com.yuyue.boss.token;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

public class FastJsonHttpMessageConverterEx extends FastJsonHttpMessageConverter {
    public FastJsonHttpMessageConverterEx() {}

    @Override
    public boolean supports(Class<?> clazz) {
        return super.supports(clazz);
    }
}
