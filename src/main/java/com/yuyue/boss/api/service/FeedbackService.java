package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.Feedback;

import java.util.List;

public interface FeedbackService {

    List<Feedback> getFeedback(String startDate, String endDate, String status);

  //  List<Feedback> getAllFeedback();

    void deleteFeedback(String id);

    void updateFeedback(String id,String status);
}
