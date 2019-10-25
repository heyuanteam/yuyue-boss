package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.Feedback;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FeedbackMapper extends MyBaseMapper<Feedback> {




    List<Feedback> getFeedback(@Param("startDate") String startDate, @Param("endDate") String endDate,
                               @Param("status") String status);

//    @Select("SELECT * FROM yuyue_feedback ")
//    List<Feedback> getAllFeedback();

    @Transactional
    @Delete("delete from yuyue_feedback where id = #{id}")
    Feedback deleteFeedback(@Param("id") String id);

    @Transactional
    @Update("update   yuyue_feedback  set status = #{status} where id = #{id}")
    Feedback updateFeedback(@Param("id") String id,@Param("status")String status);
}
