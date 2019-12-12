package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.ReportVideo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface ReportVideoService {

    //获取举报列表
    List<ReportVideo> getReportVideos(String status,String authorId,String videoId);

    //获取举报列表
    void updateReportStatus(String videoId ,String status);

    List<String> getAuthorIds();

    List<ReportVideo> getVideoIds();
}
