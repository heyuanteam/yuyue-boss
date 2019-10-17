package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.UploadFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface VideoMapper extends MyBaseMapper<UploadFile> {

    List<UploadFile> getVideoInfoList();


    List<UploadFile> searchVideoInfo(@Param(value = "id")String id,@Param(value = "categoryId") String categoryId,
                                     @Param(value = "startTime")String startTime,@Param(value = "endTime")String endTime,
                                     @Param(value = "title")String title,@Param(value = "status")String status);

    @Transactional
    void insertVideo(UploadFile uploadFile);

    // void updateVideo(UploadFile uploadFile,@Param(value = "tableName") String tableName);

    @Transactional
    void updateVideo(@Param(value = "id")String id,@Param(value = "tableName") String tableName,@Param(value = "status")String status);

    @Transactional
    void deleteVideoById(@Param(value = "tableName")String tableName,@Param(value = "id")String id);
}
