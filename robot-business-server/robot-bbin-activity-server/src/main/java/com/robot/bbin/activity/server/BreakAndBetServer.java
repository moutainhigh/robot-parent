package com.robot.bbin.activity.server;

import com.bbin.common.dto.order.OrderNoQueryDTO;
import com.bbin.common.vo.OrderNoQueryVO;
import com.robot.bbin.base.bo.TotalBetGameBO;
import com.robot.code.response.Response;
import com.robot.core.function.base.IAssemFunction;
import com.robot.core.function.base.ParamWrapper;
import com.robot.core.robot.manager.RobotWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 使用：查询消消除游戏和其投注金额
 * @Author mrt
 * @Date 2020/6/2 16:34
 * @Version 2.0
 */
@Service
public class BreakAndBetServer implements IAssemFunction<OrderNoQueryDTO> {

    @Autowired
    private BreakServer breakServer;

    @Autowired
    private QueryBetListServer queryBetList;

    @Override
    public Response doFunction(ParamWrapper<OrderNoQueryDTO> paramWrapper, RobotWrapper robotWrapper) throws Exception {
        Response<Integer> breakResult = breakServer.doFunction(paramWrapper, robotWrapper);
        if (!breakResult.isSuccess()) {
            return breakResult;
        }
        OrderNoQueryVO breakerQueryVO = new OrderNoQueryVO();
        breakerQueryVO.setAccumulativeWins(breakResult.getObj());

        Response<List<TotalBetGameBO>> betListResult = queryBetList.doFunction(paramWrapper, robotWrapper);

        if (!betListResult.isSuccess()) {
            return betListResult;
        }
        List<TotalBetGameBO> list = betListResult.getObj();
        TotalBetGameBO totalBetGameBO = filterByGameName(list, breakerQueryVO.getGameName());
        if (null == totalBetGameBO) {
            return Response.FAIL("未查询到游戏对应的总投注金额");
        }
        breakerQueryVO.setTotalBetAmount(totalBetGameBO.getTotalBetByGame());
        return Response.SUCCESS(breakerQueryVO);
    }

    /**
     * 筛选特定的游戏
     * @param list
     * @param gameName
     * @return
     */
    private TotalBetGameBO filterByGameName(List<TotalBetGameBO> list, String gameName) {
        for (TotalBetGameBO totalBetGameVO : list) {
            if (gameName.equals(totalBetGameVO.getGameName())) {
                return totalBetGameVO;
            }
        }
        return null;
    }

}