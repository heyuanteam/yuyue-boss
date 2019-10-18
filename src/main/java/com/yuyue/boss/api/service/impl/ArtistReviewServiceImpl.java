package com.yuyue.boss.api.service.impl;

import com.yuyue.boss.api.domain.AppUser;
import com.yuyue.boss.api.domain.ArtistReview;
import com.yuyue.boss.api.mapper.ArtistReviewMapper;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.api.service.ArtistReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "/ArtistReviewService")
public class ArtistReviewServiceImpl implements ArtistReviewService {

    @Autowired
    private ArtistReviewMapper artistReviewMapper;
    @Autowired
    private AppUserService appUserService;

    /**
     * 艺人搜索
     * @param artistReview
     * @return
     */
    @Override
    public List<ArtistReview> searchArtistReviewList(ArtistReview artistReview) {

       /* return artistReviewMapper.searchArtistReviewList(artistReview,startDate,endDate);*/
        return artistReviewMapper.searchArtistReviewList(artistReview);
    }

    /**
     * 获取艺人列表
     * @return
     */
    @Override
    public List<ArtistReview> getArtistReviewList() {
        return artistReviewMapper.getArtistReviewList();
    }

    /**
     * 艺人审核
     * @param id
     * @param status
     */
    @Override
    public void updateArtistReviewStatus(String id, String status) {
        artistReviewMapper.updateArtistReviewStatus(id,status);

    }

    /**
     * 删除艺人审核
     * @param id
     */
    @Override
    public void deleteArtistReviewById(String id) {
        artistReviewMapper.deleteArtistReviewById(id);
    }
}
