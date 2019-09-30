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
    @Autowired
    private FastFileStorageClient storageClient;



    @Override
    public List<UploadFile> getVideoInfoList(String id, String status,String authorId, int begin, int limit) {
        if (StringUtils.isEmpty(id)){
            return videoMapper.getVideoInfoList(ResultJSONUtils.getHashValue("yuyue_upload_file_", authorId), id,status, begin, limit);
        }

        else{
            List<UploadFile> uploadFiles = videoMapper.getVideoInfoList("yuyue_upload_file_0", "", status, begin, limit);
            uploadFiles.addAll(videoMapper.getVideoInfoList("yuyue_upload_file_1", "", status, begin, limit));
            return uploadFiles;
        }
    }

    @Override
    public List<UploadFile> searchVideoInfo(String categoryId, String startTime, String endTime, String title, String status, int begin, int limit) {
        List<UploadFile> uploadFiles = videoMapper.searchVideoInfo("yuyue_upload_file_0",categoryId, startTime, endTime, title, status, begin, limit);
        uploadFiles.addAll( videoMapper.searchVideoInfo("yuyue_upload_file_1",categoryId, startTime, endTime, title, status, begin, limit));
        return uploadFiles;
    }

    @Override
    public void insertVideo(UploadFile uploadFile) {

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
    public void deleteVideoById(String id, String authorId) {
        List<UploadFile> uploadFile = videoMapper.getVideoInfoList(ResultJSONUtils.getHashValue("yuyue_upload_file_", authorId), id, "",-1, -1);
        if (StringUtils.isEmpty(uploadFile))return;
        else{
            try {
                videoMapper.deleteVideoById(ResultJSONUtils.getHashValue("yuyue_upload_file_",authorId),id);
                String[] split = uploadFile.get(0).getFilesPath().split("/");
                this.storageClient.deleteFile(split[1] +"/"+ split[2] +"/"+ split[3] +"/"+ split[4] +"/"+ split[5]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
