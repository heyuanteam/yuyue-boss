package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.JPush;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SendMapper extends MyBaseMapper<JPush> {

    @Select("SELECT * FROM yuyue_jpush b WHERE b.valid = '10A' ")
    List<JPush> getValid();

    @Transactional
    @Update("UPDATE yuyue_jpush c SET c.valid = #{status} WHERE c.id = #{id} ")
    void updateValid(@Param("status") String status, @Param("id") String id);
}
