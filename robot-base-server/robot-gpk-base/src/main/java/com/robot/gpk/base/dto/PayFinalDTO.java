package com.robot.gpk.base.dto;

import com.bbin.common.pojo.TaskAtomDto;
import lombok.Data;

/**
 * @Author mrt
 * @Date 2020/5/15 14:02
 * @Version 2.0
 */
@Data
public class PayFinalDTO {
    private TaskAtomDto taskAtomDto;
    private String depositToken;
}
