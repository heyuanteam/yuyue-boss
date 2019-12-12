package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.UploadFile;

import java.util.List;

public interface VideoService {


    UploadFile selectById(String tableName,String id);

    List<UploadFile> getVideoInfoList();

    List<UploadFile> getVideoByVideoIds(List list);

    /*修改视频表中的状态及举报状态*/
    void updateReportStatus(String authorId,String videoId, String reportStatus,String status);

    List<UploadFile> getVideoListPlayAmount();


    List<UploadFile> searchVideoInfo(String id, String categoryId, String startTime, String endTime,
                                     String title, String status, String nickName);

    void insertVideo(UploadFile uploadFile);

    //void updateVideo(UploadFile uploadFile);

    void updateVideo(String id, String authorId, String categoryId, String status);

    void deleteVideoById(String tableName, String id);




}
