package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.UploadFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface VideoMapper extends MyBaseMapper<UploadFile> {

    /**
     * 获取视频详情
     * @param tableName
     * @param id
     * @return
     */
    @Select("select *,DATE_FORMAT(UPLOAD_TIME,'%Y-%m-%d %H:%i:%s') uploadTime from ${tableName} where id = #{id} ")
    UploadFile selectById(@Param("tableName")String tableName,@Param("id")String id);

    List<UploadFile> getVideoInfoList();

    List<UploadFile> getVideoByVideoIds(List list);

    List<UploadFile> getVideoListPlayAmount();

    List<UploadFile> searchVideoInfo(@Param(value = "id") String id, @Param(value = "categoryId") String categoryId,
                                     @Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime,
                                     @Param(value = "title") String title, @Param(value = "status") String status,
                                     @Param(value = "nickName") String nickName);



    /**
     * 修改举报状态
     * @param tableName
     * @param id
     */
    @Transactional
    void updateReportStatus(@Param("tableName")String tableName,@Param("id")String id,
                            @Param("reportStatus")String reportStatus,@Param("status")String status);

    @Transactional
    void insertVideo(UploadFile uploadFile);

    // void updateVideo(UploadFile uploadFile,@Param(value = "tableName") String tableName);

    @Transactional
    void updateVideo(@Param(value = "id") String id, @Param(value = "tableName") String tableName,
                     @Param(value = "categoryId") String categoryId, @Param(value = "status") String status);

    @Transactional
    void deleteVideoById(@Param(value = "tableName") String tableName, @Param(value = "id") String id);

    @Transactional
    @Delete("delete from yuyue_video_barrage where VIDEO_ID  = #{videoId}")
    void deleteBarrage(@Param(value = "videoId") String videoId);

    @Transactional
    @Delete("delete from yuyue_user_comment where VIDEO_ID  = #{videoId}")
    void deleteComment(@Param(value = "videoId") String videoId);


    @Select("SELECT COUNT(*) FROM (SELECT id FROM yuyue_upload_file_0 WHERE `status` = '10A' UNION SELECT id FROM yuyue_upload_file_1 WHERE `status` = '10A')V")
    int getVideoReviewNum();
}
