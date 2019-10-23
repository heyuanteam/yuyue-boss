package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.ChangeMoney;
import com.yuyue.boss.api.domain.Order;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChangeMoneyMapper extends MyBaseMapper<ChangeMoney> {

    List<ChangeMoney> getChangeMoneyList(@Param("id") String id,@Param("changeNo") String changeNo,
                                         @Param("sourceName") String sourceName, @Param("tradeType") String tradeType,
                                         @Param("mobile") String mobile,@Param("status") String status,
                                         @Param("note") String note,@Param("yiName") String yiName,
                                         @Param("startTime") String startTime,@Param("endTime") String endTime);

    void updateChangeMoney(@Param("id") String id,@Param("money") String money, @Param("note") String note,
                           @Param("status") String status,@Param("mobile") String mobile);

    @Transactional
    @Delete("DELETE FROM yuyue_change_money WHERE id =#{id} ")
    void delOutMoney(@Param("id") String id);
}
