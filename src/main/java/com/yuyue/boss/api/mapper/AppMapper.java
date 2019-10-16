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
public interface AppMapper extends MyBaseMapper<AppVersion> {

    List<AppVersion> getVersionList(@Param("systemType") String systemType, @Param("versionNo") String versionNo,
                                    @Param("appVersionId") String appVersionId);

    @Transactional
    @Insert("INSERT into yuyue_app_version (id,system_type,version_no,update_user,program_description,number) values " +
            "(#{appVersionId},#{systemType},#{versionNo},#{updateUser},#{programDescription},#{number})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertAppVersion(AppVersion appVersion);

    @Transactional
    void updateAppVersion(@Param("appVersionId") String appVersionId, @Param("versionNo") String versionNo,
                          @Param("systemType") String systemType, @Param("programDescription") String programDescription,
                          @Param("status") String status);

    @Transactional
    @Delete("DELETE FROM yuyue_app_version WHERE id =#{appVersionId} ")
    void delVersion(@Param("appVersionId")String appVersionId);

    List<VideoCategory> getAPPMenuList(@Param("id") String id, @Param("category") String category, @Param("status") String status,
                                       @Param("number") int number);

    @Transactional
    @Insert("INSERT into yuyue_category (id,category,url,category_no) values " +
            "(#{id},#{category},#{url},#{categoryNo})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertVideoCategory(VideoCategory videoCategory);

    @Transactional
    void updateAPPMenu(@Param("id") String id, @Param("sort") int sort, @Param("status") String status,
                       @Param("category") String category, @Param("url") String url);

    @Transactional
    @Delete("DELETE FROM yuyue_category WHERE id =#{id} ")
    void delAPPMenu(@Param("id") String id);

    List<Banner> getBannerList(@Param("id")String id,@Param("name") String name,@Param("status") String status,@Param("sort") int sort);

    @Transactional
    @Insert("INSERT into yuyue_banner (id,name,url,sort) values " +
            "(#{id},#{name},#{url},#{sort})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertBanner(Banner banner);

    @Transactional
    void updateBanner(@Param("id")String id,@Param("sort") int sort,@Param("name") String name,
                      @Param("status") String status,@Param("url") String url);

    @Transactional
    @Delete("DELETE FROM yuyue_banner WHERE id =#{id} ")
    void delBanner(@Param("id")String id);
}
