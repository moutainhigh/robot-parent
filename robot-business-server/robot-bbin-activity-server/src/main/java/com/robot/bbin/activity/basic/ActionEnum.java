package com.robot.bbin.activity.basic;

import com.robot.center.execute.IPathEnum;

/**
 * Created by mrt on 11/14/2019 7:50 PM
 */
public enum ActionEnum implements IPathEnum {
    QUERY_BALANCE("query_balance","查询会员余额"),
    JU_QUERY("ju_query","局查询"),
    JU_QUERY_DETAIL("ju_query_detail","局查询:窗口信息"),
    PAY_ORDER("pay_order","出款"),
    QUERY_LEVEL("query_level","查询层级"),
    PAY("pay","充值"),
    TOTAL_BET_BY_GAME("total_bet_by_game","查询消消除单个游戏投注总金额"),
    ;
    private final String pathCode;
    private final String message;

    private ActionEnum(String pathCode,String message) {
        this.pathCode = pathCode;
        this.message = message;
    }

    @Override
    public String getpathCode() {
        return this.pathCode;
    }
}
