package com.robot.jiuwu.activity.controller;

import com.alibaba.fastjson.JSON;
import com.bbin.common.client.BetQueryDto;
import com.bbin.common.dto.robot.BreakThroughDTO;
import com.bbin.common.dto.robot.VipTotalAmountDTO;
import com.bbin.common.pojo.TaskAtomDto;
import com.bbin.common.response.ResponseResult;
import com.bbin.common.util.DateUtils;
import com.bbin.common.util.ThreadLocalUtils;
import com.robot.center.dispatch.ITaskPool;
import com.robot.center.function.ParamWrapper;
import com.robot.center.util.MoneyUtil;
import com.robot.jiuwu.base.basic.FunctionEnum;
import com.robot.jiuwu.base.controller.JiuWuController;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

/**
 * Created by mrt on 11/14/2019 3:59 PM
 */
@Slf4j
@RestController
public class JiuWuActivityController extends JiuWuController {
    @Autowired
    private ITaskPool taskPool;

    // 获取vip和总打码量
    @PostMapping("/getVipAndTotalAmount")
    public ResponseResult getVipAndTotalAmount(@RequestBody VipTotalAmountDTO vipTotalAmountDTO) throws Exception{
        if (StringUtils.isEmpty(vipTotalAmountDTO.getUserName())) {
            return ResponseResult.FAIL("未传入UserName");
        }
        if (StringUtils.isEmpty(vipTotalAmountDTO.getBeginDate())) {
            return ResponseResult.FAIL("未传入BeginDate");
        }
        if (StringUtils.isEmpty(vipTotalAmountDTO.getEndDate())) {
            return ResponseResult.FAIL("未传入EndDate");
        }
        return distribute(new ParamWrapper<VipTotalAmountDTO>(vipTotalAmountDTO), FunctionEnum.QUERY_VIP_AMOUNT_SERVER);
    }

    // 查询总打码量
    @PostMapping("/QueryTotalRecharge")
    public ResponseResult QueryTotalRecharge(@RequestBody VipTotalAmountDTO vipTotalAmountDTO) throws Exception {
        if (StringUtils.isEmpty(vipTotalAmountDTO.getUserName())) {
            return ResponseResult.FAIL("未传入gameid");
        }
        if (StringUtils.isEmpty(vipTotalAmountDTO.getEndDate())) {
            return ResponseResult.FAIL("未传入endTime");
        }
        if (StringUtils.isEmpty(vipTotalAmountDTO.getBeginDate())) {
            return ResponseResult.FAIL("未传入startTime");
        }
        return distribute(new ParamWrapper<VipTotalAmountDTO>(vipTotalAmountDTO), FunctionEnum.TOTAL_RECHARGE_SERVER);
    }

    @ApiOperation("机器人：获取实际投注详细")
    @PostMapping("/getBetDetail")
    public ResponseResult getBetDetail(
            @RequestBody BetQueryDto betQueryDto
    ) throws Exception {
        BreakThroughDTO br = new BreakThroughDTO();
        br.setUserName(betQueryDto.getUserName());
        br.setBeginDate(DateUtils.format(betQueryDto.getStartDate()));
        br.setEndDate(DateUtils.format(betQueryDto.getEndDate()));

        return getTotalAmount(br);
    }

    @ApiOperation("机器人：获取投注、亏损、充值信息")
    @PostMapping("/getTotalAmount")
    public ResponseResult getTotalAmount(@RequestBody BreakThroughDTO dto) throws Exception {
        if (StringUtils.isEmpty(dto.getUserName())) {
            return ResponseResult.FAIL("userName为空");
        }
        if (StringUtils.isEmpty(dto.getBeginDate())) {
            return ResponseResult.FAIL("起始时间为空");
        }
        if (StringUtils.isEmpty(dto.getEndDate())) {
            return ResponseResult.FAIL("结束时间为空");
        }

        return distribute(new ParamWrapper<BreakThroughDTO>(dto), FunctionEnum.BET_AMOUNT_AND_RECHARGE_SERVER);
    }

    // 测试打款
    @PostMapping("/tempPay")
    public ResponseResult tempPay(@RequestBody TaskAtomDto taskAtomDto) throws Exception {
        if (null == taskAtomDto
                || StringUtils.isEmpty(taskAtomDto.getUsername())
                || null == taskAtomDto.getPaidAmount()
                || StringUtils.isEmpty(taskAtomDto.getMemo())
                || StringUtils.isEmpty(taskAtomDto.getOutPayNo())
        ) ResponseResult.FAIL("参数不全");
        log.info("mq打款入参：{}", JSON.toJSONString(taskAtomDto));

        if (taskAtomDto.getPaidAmount().compareTo(BigDecimal.ZERO) <= 0) {
            ResponseResult.FAIL("金额不能小于等于0");
        }

        taskAtomDto.setPaidAmount(MoneyUtil.formatYuan(taskAtomDto.getPaidAmount()));
        taskAtomDto.setUsername(taskAtomDto.getUsername().trim());
        String externalNo = taskAtomDto.getOutPayNo();
        if (StringUtils.isNotBlank(externalNo)) {
            boolean isRedo = isRedo(externalNo);
            if (isRedo) {
                log.info("该外部订单号已经存在,将不执行,externalNo:{},功能参数:{}", externalNo, JSON.toJSONString(taskAtomDto));
                return ResponseResult.FAIL("重复打款");
            }
        }
        return distribute(new ParamWrapper<TaskAtomDto>(taskAtomDto), FunctionEnum.PAY_TEMPSERVER);
    }
}
