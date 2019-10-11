package com.yuyue.boss.api.mapper;


import com.yuyue.boss.api.domain.ArtistReview;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArtistReviewMapper extends MyBaseMapper<ArtistReview> {

  /*  List<ArtistReview> searchArtistReviewList(ArtistReview artistReview,@Param(value = "startDate") String startDate,@Param(value = "endDate") String endDate);*/
  List<ArtistReview> searchArtistReviewList(ArtistReview artistReview);

    List<ArtistReview> getArtistReviewList();


    void updateArtistReviewStatus(@Param(value = "id") String id, @Param(value = "status") String status);


}
