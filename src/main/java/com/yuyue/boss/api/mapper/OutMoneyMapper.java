package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.OutMoney;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OutMoneyMapper extends MyBaseMapper<OutMoney> {

    List<OutMoney> getOutMoneyList(@Param("id")String id,@Param("tradeType") String tradeType,@Param("status") String status,
                                   @Param("realName")String realName,@Param("startTime") String startTime,
                                   @Param("endTime") String endTime,@Param("outNo") String outNo,@Param("userName") String userName);
    @Transactional
    void updateOutMoney(@Param("id")String id,@Param("tradeType") String tradeType,@Param("money") String money,
                        @Param("status") String status, @Param("realName")String realName);

    @Transactional
    @Delete("DELETE FROM yuyue_out_money WHERE id =#{id} ")
    void delOutMoney(@Param("id")String id);
}
