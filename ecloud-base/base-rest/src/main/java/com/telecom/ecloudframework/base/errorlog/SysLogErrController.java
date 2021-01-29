package com.telecom.ecloudframework.base.errorlog;

import com.telecom.ecloudframework.base.errorlog.mode.LogErr;
import com.telecom.ecloudframework.base.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base/sysLogErr/")
public class SysLogErrController extends BaseController<LogErr> {

	@Override
	protected String getModelDesc() {
		return "系统异常日志";
	}

}
