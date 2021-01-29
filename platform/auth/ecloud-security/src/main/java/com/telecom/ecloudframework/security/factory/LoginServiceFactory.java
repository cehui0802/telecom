package com.telecom.ecloudframework.security.factory;

import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.security.service.ILoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * <pre>
 * 描述：登录服务工厂
 * </pre>
 * @author 谢石
 * @date 2020-12-3
 */
public class LoginServiceFactory {
	protected static Logger LOG = LoggerFactory.getLogger(LoginServiceFactory.class);

	/**
	 * 根据登录类型获取登录服务
	 * @param type
	 * @return
	 * @author 谢石
	 * @date 2020-12-3
	 */
	public static ILoginService buildLoginService(String type) {
		List<ILoginService> lstLoginService = AppUtil.getImplInstanceArray(ILoginService.class);
		ILoginService loginService = lstLoginService.stream().filter(temp->temp.getType().equals(type)).findFirst().orElse(null);
		if(null==loginService){
			throw new BusinessException("类型："+type+"的登录服务不存在");
		}
		return loginService;
	}

}
