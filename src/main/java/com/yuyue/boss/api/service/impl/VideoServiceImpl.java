package com.yuyue.boss.api.service.impl;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yuyue.boss.api.domain.UploadFile;
import com.yuyue.boss.api.mapper.VideoMapper;
import com.yuyue.boss.api.service.VideoService;
import com.yuyue.boss.utils.ResultJSONUtils;
import com.yuyue.boss.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "VideoService")
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;


    @Override
    public List<UploadFile> getVideoInfoList() {

            return  videoMapper.getVideoInfoList();
    }

    @Override
    public List<UploadFile> searchVideoInfo(String id,String categoryId, String startTime, String endTime, String title, String status) {

        return  videoMapper.searchVideoInfo(id,categoryId, startTime, endTime, title, status);
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
    public void updateVideo(String id,String authorId,String status){
        videoMapper.updateVideo(id,ResultJSONUtils.getHashValue("yuyue_upload_file_",authorId),status);
    }

    @Override
    public void deleteVideoById(String tableName,String id) {
        System.out.println(tableName);
        videoMapper.deleteVideoById(tableName,id);
        videoMapper.deleteBarrage(id);
        videoMapper.deleteComment(id);
    }
}
