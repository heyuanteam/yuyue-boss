package com.yuyue.boss.shiro;

import com.yuyue.boss.enums.Constants;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Create by lujun.chen on 2018/09/29
 */
public class ShiroSessionManager extends DefaultSessionManager implements WebSessionManager {

  private static final Logger log = LoggerFactory.getLogger(ShiroSessionManager.class);

  private JedisCluster jedisCluster;

  public JedisCluster getSessionRedis() {
    if (null == jedisCluster) {
      jedisCluster = (JedisCluster) SpringContextHelper.getBean(JedisCluster.class);
    }
    return jedisCluster;
  }

  public ShiroSessionManager(JedisCluster sessionRedis) {
    this.jedisCluster = sessionRedis;
  }

  public void setSessionRedis(JedisCluster sessionRedis) {
    this.jedisCluster = sessionRedis;
  }

  /**
   * Template method that allows subclasses to react to a new session being created.
   * <p/>
   * This method is invoked <em>before</em> any session listeners are notified.
   *
   * @param session the session that was just {@link #createSession created}.
   * @param context the {@link SessionContext SessionContext} that was used to start the session.
   */
  @Override
  protected void onStart(Session session, SessionContext context) {
    super.onStart(session, context);
    if (!WebUtils.isHttp(context)) {
      log.debug("SessionContext argument is not HTTP compatible or does not have an HTTP request " +
          "pair. No session ID Access-Token  will be set.");
      return;
    }
    HttpServletRequest request = WebUtils.getHttpRequest(context);
    String accessToken = getAccessToken(request);
    if (null != session && null != accessToken) {
      String key = Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + accessToken;
      String id = session.getId().toString();
      String token = getSessionRedis().get(key);
      if (id != null && !StringUtils.isEmpty(token)) {
        getSessionRedis().setex(key, Constants.REDIS_SHIRO_TOKEN_EXPIRES, id);
      }
      if (id != null) {
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
        //automatically mark it valid here.  If it is invalid, the
        //onUnknownSession method below will be invoked and we'll remove the attribute at that time.
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
      }
    }
  }

  /**
   * get access token
   *
   * @param request
   * @return
   */
  private String getAccessToken(HttpServletRequest request) {
    String accessTokenr = request.getHeader(Constants.HTTP_REQUEST_HEADER_TOKEN_KEY);
    return accessTokenr != null ? accessTokenr : "";
  }

  @Override
  protected Serializable getSessionId(SessionKey sessionKey) {
    Serializable id = super.getSessionId(sessionKey);
    if (null == id && WebUtils.isWeb(sessionKey)) {
      HttpServletRequest request = WebUtils.getHttpRequest(sessionKey);
      String accessToken = getAccessToken(request);
      if (null != accessToken && accessToken.length() > 0) {
        //byte[] bytes = sessionRedis.opsForValue().get(DEFAULT_TOKEN_KEY + accessToken);
        //id = (Serializable) SerializationUtils.deserialize(bytes);
        id = getSessionRedis().get(Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + accessToken);
      }
    }
    return id;
  }

  @Override
  protected void onExpiration(Session s, ExpiredSessionException ese, SessionKey key) {
    super.onExpiration(s, ese, key);
    onInvalidation(key);
  }

  @Override
  protected void onInvalidation(Session s, InvalidSessionException ise, SessionKey key) {
    super.onInvalidation(s, ise, key);
    onInvalidation(key);
  }

  private void onInvalidation(SessionKey key) {
    ServletRequest request = WebUtils.getRequest(key);
    if (request != null) {
      request.removeAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID);
    }
    if (WebUtils.isHttp(key)) {
      log.debug("Referenced session was invalid.  Removing session ID Access-Token.");
      HttpServletRequest httpRequest = WebUtils.getHttpRequest(key);
      String accessToken = getAccessToken(httpRequest);
      if (null != accessToken && accessToken.length() > 0) {
        getSessionRedis().del(Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + accessToken);
      }
    } else {
      log.debug(
          "SessionKey argument is not HTTP compatible or does not have an HTTP request/response " +
              "pair. Session ID Access-Token will not be removed due to invalidated session.");
    }
  }

  @Override
  protected void onStop(Session session, SessionKey key) {
    super.onStop(session, key);
    if (WebUtils.isHttp(key)) {
      HttpServletRequest request = WebUtils.getHttpRequest(key);
      log.debug(
          "Session has been stopped (subject logout or explicit stop).  Removing session ID Access-Token.");
      String accessToken = getAccessToken(request);
      if (null != accessToken && accessToken.length() > 0) {
        getSessionRedis().del(Constants.REDIS_KEY_PREFIX_SHIRO_TOKEN + accessToken);
      }
    } else {
      log.debug(
          "SessionKey argument is not HTTP compatible or does not have an HTTP request/response " +
              "pair. Session ID Access-Token will not be removed due to stopped session.");
    }
  }


  /**
   * Returns {@code true} if session management and storage is managed by the underlying Servlet
   * container or {@code false} if managed by Shiro directly (called 'native' sessions).
   * <p/>
   * If sessions are enabled, Shiro can make use of Sessions to retain security information from
   * request to request.  This method indicates whether Shiro would use the Servlet container
   * sessions to fulfill its needs, or if it would use its own native session management instead
   * (which can support enterprise features - like distributed caching - in a container-independent
   * manner).
   *
   * @return {@code true} if session management and storage is managed by the underlying Servlet
   * container or {@code false} if managed by Shiro directly (called 'native' sessions).
   */
  @Override
  public boolean isServletContainerSessions() {
    return false;
  }
}
