package com.telecom.ecloudframework.sys.rest.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.sys.api.constant.RightsObjectConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.sys.core.manager.SysAuthorizationManager;
import com.telecom.ecloudframework.sys.core.model.SysAuthorization;


 
@RestController
@RequestMapping("/sys/authorization")
public class SysAuthorizationController extends ControllerTools{
	@Resource
	SysAuthorizationManager sysAuthorizationManager;
	
	/**
	 * 保存授权结果
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@CatchErr("对通用资源授权配置操作失败")
	@RequestMapping("saveAuthorization")
	public ResultMsg<String> saveAuthorization(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String targetId = RequestUtil.getString(request, "rightsTarget");
		String targetObject = RequestUtil.getString(request, "rightsObject");
		String authorizationJson = RequestUtil.getString(request, "authorizationJson");
			
		RightsObjectConstants.getByKey(targetObject);
		
		List<SysAuthorization> sysAuthorizationList = JSON.parseArray(authorizationJson, SysAuthorization.class);
		
		sysAuthorizationManager.createAll(sysAuthorizationList,targetId,targetObject);

		return  getSuccessResult("授权成功");
	}
	
	/**
	 * 获取授权结果用来初始化
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getAuthorizations")
	public ResultMsg<List<SysAuthorization>> getAuthorizations(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String rightsTarget = request.getParameter("rightsTarget");
		String rightsTargetObject = RequestUtil.getString(request, "rightsObject");
		
		List<SysAuthorization> list = sysAuthorizationManager.getByTarget(RightsObjectConstants.valueOf(rightsTargetObject), rightsTarget);

		return  getSuccessResult(list);
	}
}
