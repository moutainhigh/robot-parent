package com.robot.jiuwu.server;

import com.bbin.common.dto.robot.VipTotalAmountDTO;
import com.bbin.common.vo.VipTotalAmountVO;
import com.bbin.utils.project.MyBeanUtil;
import com.robot.center.util.MoneyUtil;
import com.robot.code.response.Response;
import com.robot.core.function.base.IAssemFunction;
import com.robot.core.function.base.ParamWrapper;
import com.robot.core.robot.manager.RobotWrapper;
import com.robot.jiuwu.base.dto.TotalRechargeDTO;
import com.robot.jiuwu.base.function.TotalRechargeDetailFunction;
import com.robot.jiuwu.base.vo.RechargeData;
import com.robot.jiuwu.base.vo.RechargeResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mrt on 11/14/2019 8:06 PM
 * 所有游戏打码总量之和
 */
@Slf4j
@Service
public class TotalRechargeServer implements IAssemFunction<VipTotalAmountDTO> {

    @Autowired
    private TotalRechargeDetailFunction rechargeDetailServer;


    @Override
    public Response doFunction(ParamWrapper<VipTotalAmountDTO> paramWrapper, RobotWrapper robotWrapper) throws Exception {
        VipTotalAmountDTO vipTotalAmountDTO = paramWrapper.getObj();
        TotalRechargeDTO rechargeDTO = MyBeanUtil.copyProperties(vipTotalAmountDTO, TotalRechargeDTO.class);

        Response rechargeDetailResult = rechargeDetailServer.doFunction(new ParamWrapper<TotalRechargeDTO>(rechargeDTO), robotWrapper);
        if (!rechargeDetailResult.isSuccess()) {
            return rechargeDetailResult;
        }

        RechargeResultVO rechargeResultVO = (RechargeResultVO) rechargeDetailResult.getObj();
        List<RechargeData> rechargeDataList = rechargeResultVO.getData();

        // 累加打码金额
        VipTotalAmountVO vipTotalAmountVO = new VipTotalAmountVO();
        vipTotalAmountVO.setTotalAmount(BigDecimal.ZERO);
        if (!CollectionUtils.isEmpty(rechargeDataList)) {
            rechargeDataList.forEach(o->{
                vipTotalAmountVO.setTotalAmount(vipTotalAmountVO.getTotalAmount().add(o.getGrade()));
            });
        }
        // 分转元
        vipTotalAmountVO.setTotalAmount(MoneyUtil.convertToYuan(vipTotalAmountVO.getTotalAmount()));
        return Response.SUCCESS(vipTotalAmountVO);
    }

    //参数组装
}