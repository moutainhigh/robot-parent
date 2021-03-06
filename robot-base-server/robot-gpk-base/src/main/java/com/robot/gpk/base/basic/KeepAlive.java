package com.robot.gpk.base.basic;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbin.common.response.ResponseResult;
import com.robot.center.function.ParamWrapper;
import com.robot.center.pool.RobotKeepAliveBase;
import com.robot.center.pool.RobotWrapper;
import com.robot.code.entity.TenantRobot;
import com.robot.gpk.base.function.LoginInFinalServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by mrt on 11/14/2019 7:45 PM
 */
@Service
public class KeepAlive extends RobotKeepAliveBase {

    @Autowired
    private LoginInFinalServer loginInServer;

    @Override
    protected ResponseResult loginExe(ParamWrapper paramWrapper,RobotWrapper robotWrapper) throws Exception {
        return loginInServer.doFunction(paramWrapper, robotWrapper);
    }

    @Override
    protected LambdaQueryWrapper<TenantRobot> get() {
        return null;
    }
}
