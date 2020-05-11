package com.robot.gpk.activity.function;

import com.alibaba.fastjson.JSON;
import com.bbin.common.constant.RabbitMqConstants;
import com.bbin.common.response.CommonCode;
import com.bbin.common.response.ResponseResult;
import com.robot.center.execute.IActionEnum;
import com.robot.center.execute.IResultParse;
import com.robot.center.function.FunctionBase;
import com.robot.center.function.ParamWrapper;
import com.robot.center.httpclient.CustomHttpMethod;
import com.robot.center.httpclient.ICustomEntity;
import com.robot.center.httpclient.JsonCustomEntity;
import com.robot.center.httpclient.StanderHttpResponse;
import com.robot.center.mq.MqSenter;
import com.robot.center.pool.RobotWrapper;
import com.robot.center.util.MoneyUtil;
import com.robot.code.entity.TenantRobotAction;
import com.robot.gpk.base.basic.ActionEnum;
import com.robot.gpk.base.dto.PayMoneyDTO;
import com.robot.gpk.base.function.DepositTokenServer;
import com.robot.gpk.base.function.PayFinalServer;
import com.robot.gpk.base.function.QueryUserServer;
import com.robot.gpk.base.vo.PayResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Created by mrt on 11/14/2019 8:06 PM
 * 打款
 */
@Slf4j
@Service
public class PayServer extends FunctionBase<PayMoneyDTO> {

    @Autowired
    private DepositTokenServer depositTokenServer;

    @Autowired
    private PayFinalServer payFinalServer;

    @Override
    protected ResponseResult doFunctionFinal(ParamWrapper<PayMoneyDTO> paramWrapper, RobotWrapper robotWrapper, TenantRobotAction action) throws Exception {
        PayMoneyDTO payMoneyDTO = paramWrapper.getObj();
        ResponseResult responseResult = depositTokenServer.doFunction(paramWrapper, robotWrapper);
        if (!responseResult.isSuccess()) {
            return ResponseResult.FAIL("打款前：获取 depositToken失败");
        }
        payMoneyDTO.setDepositToken(responseResult.getMessage());
        ResponseResult responseResult1 = payFinalServer.doFunction(paramWrapper, robotWrapper);
        return responseResult;
    }

    @Override
    public IActionEnum getActionEnum() {
        return ActionEnum.PAY;
    }
}
