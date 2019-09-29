package com.yuyue.boss.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.mapper.LoginMapper;
import com.yuyue.boss.api.service.LoginService;
import com.yuyue.boss.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author ly
 */
@Service(value = "LoginService")
public class LoginServiceImpl implements LoginService {
    private Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public SystemUser getSystemUserMsg(String loginName,String password,String id,String phone) { return loginMapper.getSystemUserMsg(loginName,password,id,phone); }


    @Override
    public UserVO getUser(String loginName, String password) {
        SystemUser systemUser = getSystemUserMsg(loginName, password,"","");
        UserVO userVO = BeanUtil.copyProperties(systemUser, UserVO.class);
        userVO.setPermissions(loginMapper.getSystemUserVO(systemUser.getId()));
        userVO.setToken(getToken(userVO));
        return userVO;
    }

    @Override
    public String getToken(UserVO systemUser) {
        String token = "";
        try {
            token = JWT.create()
                    .withAudience(systemUser.getId())          // 将 user id 保存到 token 里面
                    .sign(Algorithm.HMAC256(systemUser.getPassword()));   // 以 password 作为 token 的密钥
        } catch (UnsupportedEncodingException ignore) {
            ignore.printStackTrace();
            log.info("token生成错误！" );
        }
        return token;
    }

    @Override
    public List<SystemMenu> getMenuList(String loginName, String password){
        return loginMapper.getMenuList(loginName,password);
    }

    @Override
    public List<SystemMenu> getMenu(String id,Integer sort,String role,String menuName,String status){
        return loginMapper.getMenu(id,sort,role,menuName,status);
    }

    @Override
    public void insertSystemMenu(SystemMenu systemMenu) { loginMapper.insertSystemMenu(systemMenu); }

    @Override
    public void updateSystemMenu(String id, int upSort,String status,String menuName) { loginMapper.updateSystemMenu(id,upSort,status,menuName); }

    @Override
    public void insertSystemPermission(String id, String permissionName, String permissionKey, String parentId, String permissionCode) {
       loginMapper.insertSystemPermission(id,permissionName,permissionKey,parentId,permissionCode); }

    @Override
    public void delMenu(String id) { loginMapper.delMenu(id);}

    @Override
    public List<SystemPermission> getSystemPermission(String parentId, String permissionCode,String id) { return loginMapper.getSystemPermission(parentId,permissionCode,id); }

    @Override
    public void delSystemPermission(String id) { loginMapper.delSystemPermission(id);}

    @Override
    public List<SystemUser> getSystemUser(String status, String systemName,String loginName,String id) { return loginMapper.getSystemUser(status,systemName,loginName,id); }

    @Override
    public void updateSystemUser(String id, String loginName, String password, String systemName, String phone,String status) {
        loginMapper.updateSystemUser(id,loginName,password,systemName,phone,status);
    }

    @Override
    public void delSystemUser(String id) { loginMapper.delSystemUser(id); }

    @Override
    public void delSystemRole(String systemUserId) { loginMapper.delSystemRole(systemUserId); }

    @Override
    public List<SystemRole> getSystemRole(String systemUserId) { return loginMapper.getSystemRole(systemUserId); }
}
