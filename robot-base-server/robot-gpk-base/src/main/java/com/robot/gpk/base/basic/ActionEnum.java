package com.robot.gpk.base.basic;

import com.robot.center.execute.IActionEnum;

/**
 * Created by mrt on 11/14/2019 7:50 PM
 * 注意：登录是不需要的
 * 登录使用 CommonActionEnum
 */
public enum ActionEnum implements IActionEnum {
    QUERY_USER("query_user", "查询用户"),
    PAY("pay", "充值"),
    DEPOSIT_TOKEN("DepositToken","打款前token，防表单提交"),
    VALIDATE_SMS("ValidateSms","登录短信校验"),

    ;
    private final String actionCode;
    private final String message;

    private ActionEnum(String actionCode, String message) {
        this.actionCode = actionCode;
        this.message = message;
    }

    @Override
    public String getActionCode() {
        return this.actionCode;
    }
}
