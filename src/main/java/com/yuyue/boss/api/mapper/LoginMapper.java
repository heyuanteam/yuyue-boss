package com.yuyue.boss.api.mapper;

import com.yuyue.boss.api.domain.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ly
 */
@Repository
public interface LoginMapper extends MyBaseMapper<SystemUser> {

    List<SystemUser> getSystemUserMsg(@Param("loginName") String loginName, @Param("password") String password,@Param("id") String id,
                                @Param("phone") String phone);

    @Select("SELECT c.menuKey,c.saveKey,c.removeKey FROM yuyue_system_user b,yuyue_system_menu d,yuyue_system_permission c" +
            " WHERE b.id =#{systemUserId} AND d.id = c.menuId " +
            "  AND b.`status` = '10B' AND d.`status` = '10B' AND c.`status` = '10B' ORDER BY d.sort")
    List<SystemPermission> getSystemUserVO(@Param("systemUserId") String systemUserId);

    List<SystemMenu> getMenuList(@Param("loginName") String loginName, @Param("password") String password);

    List<SystemMenu> getMenu(@Param("id") String id,@Param("sort") Integer sort,@Param("role") String role,
                             @Param("menuName")String menuName,@Param("status")String status);

    @Transactional
    @Insert("INSERT into yuyue_system_menu (id,menuName,menuAction,sort,role)  values  " +
            "(#{id},#{menuName},#{menuAction},#{sort},#{role})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertSystemMenu(SystemMenu systemMenu);

    @Transactional
    void updateSystemMenu(@Param("id")String id,@Param("upSort") int upSort,@Param("status")String status,@Param("menuName")String menuName);

    @Transactional
    @Insert("INSERT into yuyue_system_permission (id,systemUserId,menuId,menuKey,saveKey,removeKey)  values  " +
            "(#{id},#{systemUserId},#{menuId},#{menuKey},#{saveKey},#{removeKey})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertSystemPermission(@Param("id") String id,@Param("systemUserId") String systemUserId, @Param("menuId") String menuId,
                                @Param("menuKey") String menuKey, @Param("saveKey") String saveKey, @Param("removeKey") String removeKey);

    @Transactional
    @Delete("DELETE FROM yuyue_system_menu WHERE id =#{id} ")
    void delMenu(@Param("id") String id);

    List<SystemPermission> getSystemPermission(@Param("menuId")String menuId,@Param("systemUserId")String systemUserId,
                                               @Param("id") String id);

    @Transactional
    @Delete("DELETE FROM yuyue_system_permission WHERE id =#{id} ")
    void delSystemPermission(@Param("id")String id);

    List<SystemUser> getSystemUser(@Param("status") String status,@Param("systemName") String systemName,@Param("loginName") String loginName,
                                   @Param("id") String id);

    void updateSystemUser(@Param("id") String id,@Param("loginName") String loginName,@Param("password") String password,
                          @Param("systemName") String systemName,@Param("phone") String phone,@Param("status") String status);

    @Transactional
    @Delete("DELETE FROM yuyue_system_user WHERE id =#{id} ")
    void delSystemUser(@Param("id")String id);

    List<SystemUserVO> getAppUserMsg(@Param("loginName")String loginName,@Param("password") String password);

    List<SystemMenu> getMenuString();

    @Transactional
    @Insert("INSERT into yuyue_system_user (id,loginName,password,systemName,phone,createUserId)  values  " +
            "(#{id},#{loginName},#{password},#{systemName},#{phone},#{createUserId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertSystemUser(SystemUser systemUser);
}
