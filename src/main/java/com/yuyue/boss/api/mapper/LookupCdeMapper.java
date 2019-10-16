package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ly
 */
@Repository
public interface LookupCdeMapper extends MyBaseMapper<LookupCde> {

    List<LookupCde> getLookupCdeSystem(@Param("status") String status, @Param("typeName") String typeName, @Param("id") String id);

    @Transactional
    @Insert("INSERT into yuyue_system (id,typeName,sort) values (#{id},#{typeName},#{sort})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertLookupCde(LookupCde lookupCde);

    @Transactional
    @Insert("INSERT into yuyue_system_config (id,type,systemId,status) values (#{id},#{type},#{systemId},#{status})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertLookupCdeConfig(LookupCdeConfig lookupCdeConfig);

    @Transactional
    void updateLookupCde(@Param("id") String id, @Param("typeName") String typeName, @Param("status") String status);

    List<LookupCdeConfig> getLookupCdeList(@Param("systemId") String systemId, @Param("id") String id);


    void updateLookupCdeList(@Param("id") String id, @Param("type") String type, @Param("status") String status);

    @Transactional
    @Delete("DELETE FROM yuyue_system_config WHERE id =#{id} ")
    void delLookupCdeList(@Param("id") String id);

    @Transactional
    @Delete("DELETE FROM yuyue_system WHERE id =#{id} ")
    void delLookupCdeSystem(@Param("id") String id);
}
