package com.robot.og.base.server;

import com.bbin.common.pojo.TaskAtomDto;
import com.robot.code.response.Response;
import com.robot.core.function.base.IAssemFunction;
import com.robot.core.function.base.ParamWrapper;
import com.robot.core.robot.manager.RobotWrapper;

import com.robot.og.base.ao.PayAO;
import com.robot.og.base.bo.QueryUserResultBO;
import com.robot.og.base.function.PayFunction;
import com.robot.og.base.function.QueryUserFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 打款：先查后打
 * @Author mrt
 * @Date 2020/6/15 15:12
 * @Version 2.0
 */
@Service
public class PayServer implements IAssemFunction<TaskAtomDto> {

    @Autowired
    private QueryUserFunction queryUserFunction;

    @Autowired
    private PayFunction payFunction;

    @Override
    public Response doFunction(ParamWrapper<TaskAtomDto> paramWrapper, RobotWrapper robotWrapper) throws Exception {
        Response<String> response = queryUserFunction.doFunction(createQueryUserParams(paramWrapper), robotWrapper);
        if (!response.isSuccess()) {
            return response;
        }
        return payFunction.doFunction(createPayParams(paramWrapper.getObj(), response.getObj(),robotWrapper), robotWrapper);
    }

    /**
     * 组装查询用户参数
     * @param paramWrapper
     * @return
     */
    private ParamWrapper<String> createQueryUserParams(ParamWrapper<TaskAtomDto> paramWrapper) {
        TaskAtomDto taskAtomDto = paramWrapper.getObj();
        return new ParamWrapper<String>(taskAtomDto.getUsername());
    }

    /**
     * 组装打款参数
     * @param moneyDTO
     * @param
     * @return
     * @throws Exception
     */
    private ParamWrapper<PayAO> createPayParams(TaskAtomDto moneyDTO, String account  , RobotWrapper robotWrapper) throws Exception {
        PayAO payDTO = new PayAO();
        payDTO.setAmount(moneyDTO.getPaidAmount());
        payDTO.setGameId(account);
        payDTO.setPassword(DigestUtils.md5DigestAsHex(robotWrapper.getPlatformPassword().getBytes()));
        payDTO.setRemark(moneyDTO.getMemo());
        payDTO.setType("2");
        payDTO.setCodingDouble("1");
        return new ParamWrapper<PayAO>(payDTO);
    }
}