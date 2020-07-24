package com.robot.gpk.activity.server;

import com.bbin.common.dto.order.OrderNoQueryDTO;
import com.bbin.common.util.DateUtils;
import com.robot.code.response.Response;
import com.robot.core.function.base.IAssemFunction;
import com.robot.core.function.base.ParamWrapper;
import com.robot.core.robot.manager.RobotWrapper;
import com.robot.gpk.base.ao.BarIdAO;
import com.robot.gpk.base.ao.JuQueryAO;
import com.robot.gpk.base.bo.JuQueryBO;
import com.robot.gpk.base.function.BarIdFunction;
import com.robot.gpk.base.function.JuQueryFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 局查询（所有平台均可使用）
 * 使用：查询注单
 * 注意：局查询页面必须是：
 *      请选择：注单编码
 * @Author mrt
 * @Date 2020/6/2 16:34
 * @Version 2.0
 *
 */
@Service
public class OrderQueryServer implements IAssemFunction<OrderNoQueryDTO> {

    @Autowired
    private BarIdFunction barIdFunction;

    @Autowired
    private JuQueryFunction juQueryFunction;

    private static final String SELECT = "注单编号";

    @Override
    public Response doFunction(ParamWrapper<OrderNoQueryDTO> paramWrapper, RobotWrapper robotWrapper) throws Exception {
        //查询 barid
        OrderNoQueryDTO queryDTO = paramWrapper.getObj();

        Response<Map<String, String>> barIdResult = barIdFunction.doFunction(barIDParams(queryDTO), robotWrapper);  //barIDParams(queryDTO)
        if (!barIdResult.isSuccess()) {
            return barIdResult;
        }

        //局查询  
        Response<JuQueryBO> response = juQueryFunction.doFunction(juQueryAO(queryDTO, barIdResult.getObj().get(SELECT)), robotWrapper);
        if (!response.isSuccess()) {
            return response;
        }
        // 校验日期
        JuQueryBO juQueryBO = response.getObj();
        if (juQueryBO.getOrderTime().isBefore(queryDTO.getStartDate())) {
            return Response.FAIL("订单已过期,订单号："+juQueryBO.getPlatFormOrderNo());
        }
        // 校验会员账号
        if (!juQueryBO.getUserName().equals(queryDTO.getUserName())) {
            return Response.FAIL("会员账号不匹配，传入：" + queryDTO.getUserName() + " 实际：" + juQueryBO.getUserName());
        }
        return response;
    }
    /**
     *  查询BarID所用参数组装
     */
    private ParamWrapper<BarIdAO> barIDParams(OrderNoQueryDTO queryDTO) {
        BarIdAO barIdAO = new BarIdAO();
        barIdAO.setGameKind(queryDTO.getGameCode());
        barIdAO.setSearchData("BetQuery");
        barIdAO.setDate_start(LocalDateTime.now().format(DateUtils.DF_3));
        barIdAO.setDate_end(LocalDateTime.now().format(DateUtils.DF_3));
        return new ParamWrapper<BarIdAO>(barIdAO);
    }

    /**
     * 查询 局查询参数组装
     */
    private ParamWrapper<JuQueryAO> juQueryAO(OrderNoQueryDTO queryDTO, String barID) {
        JuQueryAO juQueryAO = new JuQueryAO();
        juQueryAO.setGameKind(queryDTO.getGameCode());
        juQueryAO.setOrderNo(queryDTO.getOrderNo());
        juQueryAO.setBarId(barID);
        return new ParamWrapper<JuQueryAO>(juQueryAO);
    }

}