package com.yuyue.boss.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yuyue.boss.annotation.CurrentUser;
import com.yuyue.boss.annotation.LoginRequired;
import com.yuyue.boss.api.domain.AppVersion;
import com.yuyue.boss.api.domain.Banner;
import com.yuyue.boss.api.domain.OutMoney;
import com.yuyue.boss.api.domain.SystemUser;
import com.yuyue.boss.api.service.OutMoneyService;
import com.yuyue.boss.enums.CodeEnum;
import com.yuyue.boss.enums.ResponseData;
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
import java.util.List;
import java.util.Map;

/**
 * 提现管理
 */
@RestController
@RequestMapping(value = "/outMoney" ,produces = "application/json; charset=UTF-8")
@Slf4j
public class OutMoneyController extends BaseController {

    @Autowired
    private OutMoneyService outMoneyService;

    /**
     * 获取提现记录
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getOutMoneyList")
    @ResponseBody
    @RequiresPermissions("money:menu")
    @LoginRequired
    public ResponseData getOutMoneyList(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("获取提现记录----------->>/system/getOutMoneyList");
        Map<String, String> parameterMap = getParameterMap(request, response);
        try {
            PageUtil.getPage(parameterMap.get("page"));
            List<OutMoney> outMoneyList = outMoneyService.getOutMoneyList("",parameterMap.get("tradeType"), parameterMap.get("status"),
                    parameterMap.get("realName"),parameterMap.get("startTime"),parameterMap.get("endTime"),parameterMap.get("outNo"),
                    parameterMap.get("userName"));
            PageUtil pageUtil = new PageUtil(outMoneyList);
            return new ResponseData(pageUtil);
        } catch (Exception e) {
            log.info("===========>>>>>>获取提现记录失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"获取提现记录失败！");
        }
    }

    /**
     * 修改提现记录
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/editOutMoney")
    @ResponseBody
    @RequiresPermissions("money:save")
    @LoginRequired
    public ResponseData editOutMoney(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("修改提现记录---------->>/system/editOutMoney");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "提现id不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("tradeType"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "提现类型不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("money"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "提现金额不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("status"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "提现状态不可以为空！");
        } else if (StringUtils.isEmpty(parameterMap.get("realName"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "账户真实名称不可以为空！");
        }
        List<OutMoney> outMoneyList = outMoneyService.getOutMoneyList(parameterMap.get("id"),
                "","","","","","","");
        if (CollectionUtils.isEmpty(outMoneyList)){
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "提现记录不存在！");
        }
        try {
            outMoneyService.updateOutMoney(parameterMap.get("id"),parameterMap.get("tradeType"),
                    parameterMap.get("money"),parameterMap.get("status"),parameterMap.get("realName"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "修改提现记录成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>修改提现记录失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"修改提现记录失败！");
        }
    }

    /**
     * 删除提现记录
     * @param systemUser
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/delOutMoney")
    @ResponseBody
    @RequiresPermissions("money:remove")
    @LoginRequired
    public ResponseData delOutMoney(@CurrentUser SystemUser systemUser, HttpServletRequest request, HttpServletResponse response) {
        log.info("删除提现记录----------->>/system/delOutMoney");
        Map<String, String> parameterMap = getParameterMap(request, response);
        if (StringUtils.isEmpty(parameterMap.get("id"))){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "提现记录id不可以为空！");
        }
        List<OutMoney> outMoneyList = outMoneyService.getOutMoneyList(parameterMap.get("id"),"","","",
                "","","","");
        if (CollectionUtils.isEmpty(outMoneyList)){
            return new ResponseData(CodeEnum.PARAM_ERROR.getCode(), "提现记录存在！");
        }
        try {
            outMoneyService.delOutMoney(parameterMap.get("id"));
            return new ResponseData(CodeEnum.SUCCESS.getCode(), "删除提现记录成功！");
        } catch (Exception e) {
            log.info("===========>>>>>>删除提现记录失败！");
            return new ResponseData(CodeEnum.E_400.getCode(),"删除提现记录失败！");
        }
    }

}
