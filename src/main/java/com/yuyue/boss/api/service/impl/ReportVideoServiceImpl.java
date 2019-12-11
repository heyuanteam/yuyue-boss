package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.ReportVideo;
import com.yuyue.boss.api.mapper.ReportVideoMapper;
import com.yuyue.boss.api.service.ReportVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "ReportVideoService")
public class ReportVideoServiceImpl implements ReportVideoService {
    @Autowired
    private ReportVideoMapper reportVideoMapper;

    @Override
    public List<ReportVideo> getReportVideos(String status,String authorId,String videoId) {
        return reportVideoMapper.getReportVideos(status,authorId,videoId);
    }

    @Override
    public void updateReportStatus(String id, String status) {
        reportVideoMapper.updateReportStatus(id,status);
    }
}
