package com.yuyue.boss.api.mapper;


import com.yuyue.boss.api.domain.ArtistReview;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface ArtistReviewMapper extends MyBaseMapper<ArtistReview> {

    /*  List<ArtistReview> searchArtistReviewList(ArtistReview artistReview,@Param(value = "startDate") String startDate,@Param(value = "endDate") String endDate);*/
    List<ArtistReview> searchArtistReviewList(ArtistReview artistReview);

    List<ArtistReview> getArtistReviewList();
    @Transactional
    void updateArtistReviewStatus(@Param(value = "id") String id, @Param(value = "status") String status);

    @Transactional
    @Delete("DELETE FROM yuyue_show_name WHERE id = #{id}")
    void deleteArtistReviewById(@Param(value = "id") String id);


    @Select("SELECT count(*) FROM yuyue_show_name where `status` = '10A' ")
    int getArtistReviewNum();


}
