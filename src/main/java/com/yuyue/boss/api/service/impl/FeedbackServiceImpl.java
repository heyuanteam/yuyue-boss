package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.Feedback;
import com.yuyue.boss.api.mapper.FeedbackMapper;
import com.yuyue.boss.api.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "FeedbackService")
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public List<Feedback> getFeedback(String startDate, String endDate, String status) {
        return feedbackMapper.getFeedback(startDate,endDate,status);
    }

//    @Override
//    public List<Feedback> getAllFeedback() {
//        return feedbackMapper.getAllFeedback();
//    }

    @Override
    public void deleteFeedback(String id) {
        feedbackMapper.deleteFeedback(id);
    }

    @Override
    public void updateFeedback(String id, String status) {
        feedbackMapper.updateFeedback(id,status);
    }
}
