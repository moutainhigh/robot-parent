package com.robot.core.function.base;

import com.robot.core.http.request.CustomHeaders;
import com.robot.core.http.request.ICustomEntity;
import com.robot.core.robot.manager.RobotWrapper;
import com.robot.core.task.execute.IFunctionProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author mrt
 * @Date 2020/5/19 19:34
 * @Version 2.0
 */
@Data
@AllArgsConstructor
public class ExecuteProperty implements IFunctionProperty {

    /**
     * 域名等级,默认：1
     */
    @NotNull
    private int rank;

    /**
     * 动作
     */
    @NotNull
    private IActionEnum action;

    /**
     * 自定义请求头
     */
    private CustomHeaders headers;

    /**
     * 请求体
     */
    private ICustomEntity entity;

    /**
     * 是否检查掉线，默认：true
     */
    @NotNull
    private boolean isCheckLost;

    /**
     * ResultParse
     */
    @NotNull
    private IResultParse resultParse;

    /**
     * 机器人包装类
     */
    @NotNull
    private RobotWrapper robotWrapper;

}
