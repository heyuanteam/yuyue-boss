package com.yuyue.boss.api.service;

import com.yuyue.boss.api.domain.ArtistReview;

import java.util.List;

public interface ArtistReviewService {

    List<ArtistReview> searchArtistReviewList(ArtistReview artistReview);

    List<ArtistReview> getArtistReviewList();

    void updateArtistReviewStatus(String id,  String status);

    void deleteArtistReviewById(String id);
}
