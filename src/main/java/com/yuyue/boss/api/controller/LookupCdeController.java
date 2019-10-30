package com.yuyue.boss.api.controller;

import com.beust.jcommander.internal.Maps;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.LookupCde;
import com.yuyue.boss.api.domain.LookupCdeConfig;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.LookupCdeService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.RandomSaltUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 字典管理
 */

@Slf4j
@CrossOrigin
@RestController
@RequestMapping(value = "/lookupCde", produces = "application/json; charset=UTF-8")
public class LookupCdeController extends BaseController {

    @Autowired
    private LookupCdeService lookupCdeService;

    /**
     * 获取全部字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getSystemList")
    @ResponseBody
    @LoginRequired
    public ResponseData getSystemList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取全部字典----------->>/lookupCde/getSystemList");
        getParameterMap(request, response);
        try {
            List<LookupCde> lookupCdeList = lookupCdeService.getLookupCdeSystem("", "","");
            List list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(lookupCdeList)){
                for (LookupCde lookupCde: lookupCdeList) {
                    Map<String,Object> map = Maps.newHashMap();
                    map.put("id",lookupCde.getId());
                    map.put("typeName",lookupCde.getTypeName());
                    List<LookupCdeConfig> lookupCdeConfigList = lookupCdeService.getLookupCdeList(lookupCde.getId(),"");
                    map.put("content",lookupCdeConfigList);
                    list.add(map);
                }
            }
            return new ResponseData(list);
        } catch (Exception e) {
            log.info("===========>>>>>>获取全部字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取全部字典失败！");
        }
    }

    /**
     * 获取系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getLookupCdeSystem")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:menu")
    @LoginRequired
    public ResponseData getLookupCdeSystem(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取系统字典----------->>/lookupCde/getSystem");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<LookupCde> menuList = lookupCdeService.getLookupCdeSystem(parameterMap.get("status"), parameterMap.get("typeName"),"");
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统字典失败！");
        }
    }

    /**
     * 添加系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addLookupCdeSystem")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData addLookupCdeSystem(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加系统字典----------->>/lookupCde/addLookupCdeSystem");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("typeName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典名称不可以为空！");
        }
        List<LookupCde> LookupCdeList = lookupCdeService.getLookupCdeSystem("", parameterMap.get("typeName"),"");
        if (CollectionUtils.isNotEmpty(LookupCdeList)){
            for (LookupCde lookupCde:LookupCdeList) {
                if (parameterMap.get("typeName").equals(lookupCde.getTypeName())){
                    return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典名称已经存在！");
                }
            }
        }
        try {
            List<LookupCde> list = lookupCdeService.getLookupCdeSystem("", "","");
            int sort = list.get(list.size()-1).getSort();
            sort += 1;
            LookupCde lookupCde = new LookupCde();
            lookupCde.setId(RandomSaltUtil.generetRandomSaltCode(32));
            lookupCde.setTypeName(parameterMap.get("typeName"));
            lookupCde.setSort(sort);
            lookupCdeService.insertLookupCde(lookupCde);
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>添加系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加系统字典失败！");
        }
    }

    /**
     * 修改系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editLookupCdeSystem")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData editLookupCdeSystem(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改系统字典----------->>/lookupCde/editLookupCdeSystem");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("typeName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典名称不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典ID不可以为空！");
        }
        List<LookupCde> list = lookupCdeService.getLookupCdeSystem("", "", parameterMap.get("id"));
        if (CollectionUtils.isEmpty(list)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改字典不存在！");
        }
        if (!parameterMap.get("typeName").equals(list.get(0).getTypeName())) {
            List<LookupCde> LookupCdeList = lookupCdeService.getLookupCdeSystem("", parameterMap.get("typeName"),"");
            if (CollectionUtils.isNotEmpty(LookupCdeList)){
                for (LookupCde lookupCde:LookupCdeList) {
                    if (parameterMap.get("typeName").equals(lookupCde.getTypeName())){
                        return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典名称已经存在！");
                    }
                }
            }
        }
        try {
            lookupCdeService.updateLookupCde(parameterMap.get("id"),parameterMap.get("typeName"),parameterMap.get("status"));
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>修改系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改系统字典失败！");
        }
    }

    /**
     * 删除系统字典
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delLookupCde")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:remove")
    @LoginRequired
    public ResponseData delLookupCde(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除系统字典---------->>/lookupCde/delLookupCde");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典id不可以为空！");
        }
        try {
            List<LookupCde> list = lookupCdeService.getLookupCdeSystem("", "", parameterMap.get("id"));
            if (CollectionUtils.isEmpty(list)){
                return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "修改字典不存在！");
            }
            lookupCdeService.delLookupCdeSystem(parameterMap.get("id"));
            List<LookupCdeConfig> menuList = lookupCdeService.getLookupCdeList(parameterMap.get("id"),"");
            if (CollectionUtils.isNotEmpty(menuList)){
                for (LookupCdeConfig lookupCdeConfig: menuList) {
                    lookupCdeService.delLookupCdeList(lookupCdeConfig.getId());
                }
            }
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>删除系统字典失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除系统字典失败！");
        }
    }

    /**
     * 获取系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:menu")
    @LoginRequired
    public ResponseData getLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取系统字典下级----------->>/lookupCde/getLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典id不可以为空！");
        }
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<LookupCdeConfig> menuList = lookupCdeService.getLookupCdeList(parameterMap.get("id"),"");
            PageUtil pageUtil = new PageUtil(menuList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取系统字典下级失败！");
        }
    }

    /**
     * 添加系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData addLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("添加系统字典下级----------->>/lookupCde/addLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("type"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("systemId"))) {
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典systemId不可以为空！");
        }
        try {
            LookupCdeConfig lookupCdeConfig = new LookupCdeConfig();
            lookupCdeConfig.setId(RandomSaltUtil.generetRandomSaltCode(32));
            lookupCdeConfig.setStatus(parameterMap.get("status"));
            lookupCdeConfig.setType(parameterMap.get("type"));
            lookupCdeConfig.setSystemId(parameterMap.get("systemId"));
            lookupCdeService.insertLookupCdeConfig(lookupCdeConfig);
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>添加系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"添加系统字典下级失败！");
        }
    }

    /**
     * 修改系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:save")
    @LoginRequired
    public ResponseData editLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改系统字典下级----------->>/lookupCde/editLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("type"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典ID不可以为空！");
        }
        try {
            lookupCdeService.updateLookupCdeList(parameterMap.get("id"),parameterMap.get("type"),parameterMap.get("status"));
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>修改系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改系统字典下级失败！");
        }
    }

    /**
     * 删除系统字典下级
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delLookupCdeList")
    @ResponseBody
    @RequiresPermissions("LookupCdeManager:remove")
    @LoginRequired
    public ResponseData delLookupCdeList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除系统字典下级----------->>/lookupCde/delLookupCdeList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "字典id不可以为空！");
        }
        List<LookupCdeConfig> menuList = lookupCdeService.getLookupCdeList("",parameterMap.get("id"));
        if (CollectionUtils.isEmpty(menuList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "删除系统字典下级不存在！");
        }
        try {
            lookupCdeService.delLookupCdeList(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.info("===========>>>>>>删除系统字典下级失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除系统字典下级失败！");
        }
    }

}
