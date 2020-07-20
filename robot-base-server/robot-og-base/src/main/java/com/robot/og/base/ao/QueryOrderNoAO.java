package com.robot.og.base.ao;

import lombok.Data;
import lombok.ToString;

/**
 * <p>
 *  查询注单参数
 * </p>
 *
 * @author tanke
 * @date 2020/7/17
 */
@Data
public class QueryOrderNoAO {

	private String type;
	private String accountId;
	private String bettingCode;
	private String platform;
	private String startDate;
	private String lastDate;
	private String pageNo;
	private String pageSize;

}