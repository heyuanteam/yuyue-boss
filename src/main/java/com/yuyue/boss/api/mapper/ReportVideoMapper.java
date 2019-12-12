package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.ReportVideo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface ReportVideoMapper extends MyBaseMapper<ReportVideo> {


    //获取举报列表
    List<ReportVideo> getReportVideos(@Param(value = "status") String status,
                                      @Param(value = "authorId") String authorId,
                                      @Param(value = "videoId") String videoId);



    //获取举报列表
    @Update("update yuyue_video_report set status = #{status} where video_id = #{videoId}")
    void updateReportStatus(@Param(value = "videoId") String videoId ,@Param(value = "status") String status);

    @Select("SELECT DISTINCT(author_id) FROM yuyue_video_report ")
    List<String> getAuthorIds();

    @Select("SELECT DISTINCT(video_id),author_id FROM yuyue_video_report ")
    List<ReportVideo> getVideoIds();
}
