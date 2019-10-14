package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.ArtistReview;
import com.yuyue.boss.api.mapper.ArtistReviewMapper;
import com.yuyue.boss.api.service.ArtistReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "/ArtistReviewService")
public class ArtistReviewServiceImpl implements ArtistReviewService {

    @Autowired
    private ArtistReviewMapper artistReviewMapper;
    @Override
    public List<ArtistReview> searchArtistReviewList(ArtistReview artistReview) {

       /* return artistReviewMapper.searchArtistReviewList(artistReview,startDate,endDate);*/
        return artistReviewMapper.searchArtistReviewList(artistReview);
    }

    @Override
    public List<ArtistReview> getArtistReviewList() {
        return artistReviewMapper.getArtistReviewList();
    }

    @Override
    public void updateArtistReviewStatus(String id, String status) {
        artistReviewMapper.updateArtistReviewStatus(id,status);
    }

    @Override
    public void deleteArtistReviewById(String id) {
        artistReviewMapper.deleteArtistReviewById(id);
    }
}
