package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.UploadFile;

import java.util.List;

public interface VideoService {
    List<UploadFile> getVideoInfoList(String id,String authorId,String status,int begin,int limit);


    List<UploadFile> searchVideoInfo(String categoryId, String startTime,String endTime,
                                        String title,String status,int begin,int limit);

    void insertVideo(UploadFile uploadFile);

    //void updateVideo(UploadFile uploadFile);

    void updateVideo(String id,String authorId,String status);

    void deleteVideoById(String id,String authorId);




}
