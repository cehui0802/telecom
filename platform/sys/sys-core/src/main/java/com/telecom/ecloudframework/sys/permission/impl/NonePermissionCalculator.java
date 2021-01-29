package com.telecom.ecloudframework.sys.permission.impl;

import com.telecom.ecloudframework.sys.api.permission.IPermissionCalculator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * <pre>
 * 描述：无人
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2018年5月8日
 * 版权:summer
 * </pre>
 */
@Service
public class NonePermissionCalculator implements IPermissionCalculator {

	@Override
	public String getTitle() {
		return "无";
	}

	@Override
	public String getType() {
		return "none";
	}

	@Override
	public boolean haveRights(JSONObject json) {
		return false;
	}

}
