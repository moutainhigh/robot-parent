package com.robot.og.base.server;


import com.robot.code.dto.LoginDTO;
import com.robot.code.response.Response;
import com.robot.core.function.base.IAssemFunction;
import com.robot.core.function.base.ParamWrapper;
import com.robot.core.robot.manager.RobotWrapper;
import com.robot.og.base.bo.LoginResultVO;
import com.robot.og.base.function.LoginFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录
 * @Author tanke
 * @Date 2020/6/15 15:12
 * @Version 2.0
 */
@Service
public class LoginServer implements IAssemFunction<LoginDTO> {


    @Autowired
    private LoginFunction loginFunction;

    @Override
    public Response doFunction(ParamWrapper<LoginDTO> paramWrapper, RobotWrapper robotWrapper) throws Exception {


        return loginFunction.doFunction(paramWrapper  ,robotWrapper);
    }



}
