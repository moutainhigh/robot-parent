package com.robot.jiuwu.base.function;
import com.robot.center.mq.MqSenter;
import com.robot.code.response.Response;
import com.robot.core.function.base.AbstractFunction;
import com.robot.core.function.base.IPathEnum;
import com.robot.core.function.base.IResultHandler;
import com.robot.core.function.base.ParamWrapper;
import com.robot.core.http.request.IEntity;
import com.robot.core.http.request.JsonEntity;
import com.robot.core.http.response.StanderHttpResponse;
import com.robot.core.robot.manager.RobotWrapper;
import com.robot.jiuwu.base.basic.PathEnum;
import com.robot.jiuwu.base.ao.PayAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Created by mrt on 11/14/2019 8:06 PM
 * 付款
 */
@Slf4j
@Service
public class PayFunction extends AbstractFunction<PayAO,String,Object> {

    @Autowired
    private MqSenter mqSenter;


    @Override
    public Response<Object> doFunction(ParamWrapper<PayAO> paramWrapper, RobotWrapper robotWrapper) throws Exception {
        Response<Object> response = super.doFunction(paramWrapper, robotWrapper);
        PayAO payFinalAO = paramWrapper.getObj();
        mqSenter.topicPublic("",payFinalAO.getExteralNo(),response,new BigDecimal(payFinalAO.getAmount()));
        return response;
    }

    @Override
    protected IPathEnum getPathEnum() {
        return PathEnum.PAY;
    }


    @Override
    protected IEntity getEntity(PayAO payAO, RobotWrapper robotWrapper) {
        return JsonEntity.custom(12)
                .add("amount", payAO.getAmount()) // 金额
                .add("gameids", payAO.getGameId()) // 游戏ids
                .add("password", payAO.getPassword()) // 密码
                .add("remark", payAO.getMemo()) // 备注
                .add("type","2") // 0人工充值 1线上补单 2活动彩金 3补单 6其他
                ;
    }

    @Override
    protected IResultHandler<String, Object> getResultHandler() {
        return ResultHandler.INSTANCE;
    }

    /**
     * 响应转换
     * 登录响应：
     * {"IsSuccess":true,"WaitingTime":"\/Date(1588801054323)\/","SendAddress":"+861*******785"}
     */
    private static final class ResultHandler implements IResultHandler<String, Object> {
        private static final String SUCCESS = "true";
        private static final ResultHandler INSTANCE = new ResultHandler();

        private ResultHandler() {
        }

        @Override
        public Response parse2Obj(StanderHttpResponse<String, Object> shr) {
            String result = shr.getOriginalEntity();
            log.info("打款功能响应：{}", result);
            if (StringUtils.isEmpty(result)) {
                log.info("打款未有任何响应");
                return Response.FAIL("打款未有任何响应：" + result);
            }
            if (SUCCESS.equals(result)) {
                return Response.SUCCESS("打款成功");
            } else {
                return Response.FAIL("打款失败：" + result);
            }
        }
    }
}