package com.yuyue.boss.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yuyue.boss.api.domain.SystemMenu;
import com.yuyue.boss.api.domain.SystemPermission;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.domain.UserVO;
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
    public List<SystemMenu> getMenu(String id,Integer sort){
        return loginMapper.getMenu(id,sort);
    }

    @Override
    public void insertSystemMenu(SystemMenu systemMenu) { loginMapper.insertSystemMenu(systemMenu); }

    @Override
    public void updateSystemMenu(String id, int upSort,String status) { loginMapper.updateSystemMenu(id,upSort,status); }

    @Override
    public void insertSystemPermission(String id, String permissionName, String permissionKey, String parentId, String permissionCode) {
       loginMapper.insertSystemPermission(id,permissionName,permissionKey,parentId,permissionCode); }

    @Override
    public void delMenu(String id) { loginMapper.delMenu(id);}
}
