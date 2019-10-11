package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.Advertisement;
import com.yuyue.boss.api.domain.UploadFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AdReviewMapper extends MyBaseMapper<Advertisement> {

    List<Advertisement> getAdReviewList(@Param(value = "merchantName") String merchantName,
                                        @Param(value = "merchantAddr") String merchantAddr,
                                        @Param(value = "phone") String phone,
                                        @Param(value = "status") String status, @Param(value = "applicationStartTime") String applicationStartTime,
                                        @Param(value = "applicationEndTime")String applicationEndTime);


    void updateAdReviewStatus(@Param(value = "id")String id,@Param(value = "status")String status);


}
