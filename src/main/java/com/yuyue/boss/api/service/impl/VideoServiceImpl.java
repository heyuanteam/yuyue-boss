package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.UploadFile;
import com.yuyue.boss.api.mapper.VideoMapper;
import com.yuyue.boss.api.service.VideoService;
import com.yuyue.boss.utils.ResultJSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "VideoService")
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;


    @Override
    public UploadFile selectById(String tableName, String id) {
        return videoMapper.selectById(tableName,id);
    }

    @Override
    public void updateReportStatus(String authorId,String videoId, String reportStatus,String status) {
        videoMapper.updateReportStatus(ResultJSONUtils.getHashValue("yuyue_upload_file_",authorId),videoId,reportStatus,status);
    }

    @Override
    public List<UploadFile> getVideoInfoList() {

            return  videoMapper.getVideoInfoList();
    }

    @Override
    public List<UploadFile> getVideoByVideoIds(List list) {
        return videoMapper.getVideoByVideoIds(list);
    }

    @Override
    public List<UploadFile> getVideoListPlayAmount() {
        return videoMapper.getVideoListPlayAmount();
    }

    @Override
    public List<UploadFile> searchVideoInfo(String id, String categoryId, String startTime, String endTime, String title, String status, String nickName) {

        return  videoMapper.searchVideoInfo(id,categoryId, startTime, endTime, title, status,nickName);
    }

    @Override
    public void insertVideo(UploadFile uploadFile) {
        videoMapper.insertVideo(uploadFile);
    }

    /* @Override
     public void updateVideo(UploadFile uploadFile) {
         videoMapper.updateVideo(uploadFile,ResultJSONUtils.getHashValue("yuyue_upload_file_", uploadFile.getAuthorId()));
     }*/
    @Override
    public void updateVideo(String id,String authorId,String categoryId,String status){
        videoMapper.updateVideo(id, ResultJSONUtils.getHashValue("yuyue_upload_file_",authorId),categoryId,status);
    }

    @Override
    public void deleteVideoById(String tableName,String id) {
        System.out.println(tableName);
       /* videoMapper.deleteVideoById(tableName,id);
        videoMapper.deleteBarrage(id);
        videoMapper.deleteComment(id);*/
        videoMapper.updateVideo(id, tableName,"","10C");

    }
}
