package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ly
 */
@Repository
public interface LoginMapper extends MyBaseMapper<SystemUser> {

    SystemUser getSystemUserMsg(@Param("loginName") String loginName, @Param("password") String password,@Param("id") String id);

    @Select("SELECT h.permissionKey FROM yuyue_system_user b,yuyue_system_role c,yuyue_system_menu d,yuyue_system_permission h" +
            " WHERE b.id = #{systemUserId} AND b.`status` = '10B' AND d.id = c.menuId AND d.`status` = '10B'" +
            "   AND h.id = c.permissionId AND h.`status` = '10B' AND c.`status` = '10B' ORDER BY h.permissionCode")
    List<String> getSystemUserVO(@Param("systemUserId") String systemUserId);

    List<SystemMenu> getMenuList(@Param("loginName") String loginName, @Param("password") String password);

    List<SystemMenu> getMenu(@Param("type") String type, @Param("id") String id);
}
