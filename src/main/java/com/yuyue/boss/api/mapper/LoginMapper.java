package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemPermission;
import com.yuyue.boss.api.domain.SystemUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ly
 */
@Repository
public interface LoginMapper extends MyBaseMapper<SystemUser> {

    SystemUser getSystemUserMsg(@Param("loginName") String loginName, @Param("password") String password,@Param("id") String id,
                                @Param("phone") String phone);

    @Select("SELECT h.permissionKey FROM yuyue_system_user b,yuyue_system_role c,yuyue_system_menu d,yuyue_system_permission h" +
            " WHERE b.id = #{systemUserId} AND b.`status` = '10B' AND d.id = c.menuId AND d.`status` = '10B'" +
            "   AND h.id = c.permissionId AND h.`status` = '10B' AND c.`status` = '10B' ORDER BY h.permissionCode")
    List<String> getSystemUserVO(@Param("systemUserId") String systemUserId);

    List<SystemMenu> getMenuList(@Param("loginName") String loginName, @Param("password") String password);

    List<SystemMenu> getMenu(@Param("id") String id,@Param("sort") Integer sort);

    @Transactional
    @Insert("replace into yuyue_system_menu (id,menuName,menuCode,menuAction,sort,role)  values  " +
            "(#{id},#{menuName},#{menuCode},#{menuAction},#{sort},#{role})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertSystemMenu(SystemMenu systemMenu);

    @Transactional
    void updateSystemMenu(@Param("id")String id,@Param("upSort") int upSort,@Param("status")String status);

    @Transactional
    @Insert("replace into yuyue_system_permission (id,permissionName,permissionKey,parentId,permissionCode)  values  " +
            "(#{id},#{permissionName},#{permissionKey},#{parentId},#{permissionCode})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertSystemPermission(@Param("id") String id,@Param("permissionName") String permissionName,
                                @Param("permissionKey") String permissionKey, @Param("parentId") String parentId,
                                @Param("permissionCode") String permissionCode);

    @Transactional
    @Delete("  ")
    void delMenu(String id);
}
