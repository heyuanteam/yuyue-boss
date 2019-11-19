package com.yuyue.boss.api.controller;

import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.*;
import com.yuyue.boss.api.service.AppUserService;
import com.yuyue.boss.api.service.ChangeMoneyService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
import com.yuyue.boss.utils.BeanUtil;
import com.yuyue.boss.utils.PageUtil;
import com.yuyue.boss.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 账户流水
 */
@Slf4j
@RestController
@RequestMapping(value = "/changeMoney" ,produces = "application/json; charset=UTF-8")
public class ChangeMoneyController extends BaseController {

    @Autowired
    private ChangeMoneyService changeMoneyService;
    @Autowired
    private AppUserService appUserService;

    /**
     * 获取账户流水记录
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getChangeMoneyList")
    @ResponseBody
    @RequiresPermissions("profit:menu")
    @LoginRequired
    public ResponseData getChangeMoneyList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取账户流水记录---------->>/changeMoney/getChangeMoneyList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<ChangeMoney> changeMoneyList = changeMoneyService.getChangeMoneyList("",parameterMap.get("changeNo"),parameterMap.get("sourceName"),
                    parameterMap.get("tradeType"), parameterMap.get("mobile"),parameterMap.get("status"),parameterMap.get("note"),
                    parameterMap.get("yiName"),parameterMap.get("startTime"),parameterMap.get("endTime"));
            PageUtil pageUtil = new PageUtil(changeMoneyList);
            if (CollectionUtils.isNotEmpty(changeMoneyList)) {
                for (ChangeMoney changeMoney: changeMoneyList) {
                    AppUser appUserMsg = appUserService.getAppUserMsg(changeMoney.getSourceId(),"");
                    if (StringUtils.isNotNull(appUserMsg) && StringUtils.isNotEmpty(appUserMsg.getRealName())) {
                        changeMoney.setSourceName(appUserMsg.getRealName());
                    }
                }
            }
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取账户流水记录失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取账户流水记录失败！");
        }
    }

    /**
     * 修改账户流水
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editChangeMoney")
    @ResponseBody
    @RequiresPermissions("profit:save")
    @LoginRequired
    public ResponseData editChangeMoney(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改收益----------->>/changeMoney/editChangeMoney");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("mobile"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "收益手机号不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "收益状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("note"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "备注不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "收益Id不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("money"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "收益钱不可以为空！");
        }
        List<ChangeMoney> changeMoneyList = changeMoneyService.getChangeMoneyList(parameterMap.get("id"),
                "","","","","","","","","");
        if (CollectionUtils.isEmpty(changeMoneyList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "账户流水记录不存在！");
        }

        try {
            changeMoneyService.updateChangeMoney(parameterMap.get("id"),parameterMap.get("money"),
                    parameterMap.get("note"),parameterMap.get("status"),parameterMap.get("mobile"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改账户流水成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>修改账户流水失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改账户流水失败！");
        }
    }

    /**
     * 删除账户流水记录
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delChangeMoney")
    @ResponseBody
    @RequiresPermissions("profit:remove")
    @LoginRequired
    public ResponseData delOutMoney(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除收益记录----------->>/changeMoney/delChangeMoney");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "账户流水记录id不可以为空！");
        }
        List<ChangeMoney> changeMoneyList = changeMoneyService.getChangeMoneyList(parameterMap.get("id"),
                "","","","","","","","","");
        if (CollectionUtils.isEmpty(changeMoneyList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "账户流水记录不存在！");
        }
        try {
            changeMoneyService.delOutMoney(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除账户流水记录成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除账户流水记录失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除账户流水记录失败！");
        }
    }

}
