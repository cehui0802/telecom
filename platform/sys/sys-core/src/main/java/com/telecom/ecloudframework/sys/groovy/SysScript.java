package com.telecom.ecloudframework.sys.groovy;

import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.api.model.IGroup;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.sys.api.groovy.IScript;
import com.telecom.ecloudframework.sys.api.service.SerialNoService;
import com.telecom.ecloudframework.sys.api.service.SysFileService;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import com.telecom.ecloudframework.sys.util.SysPropertyUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 系统脚本
 * 常用的系统功能的脚本
 */
@Component
public class SysScript implements IScript {
	@Resource
	SerialNoService serialNoService;

	@Resource
	SysFileService sysFileService;
	
	/**
	 * 获取系统流水号
	 * @param alias
	 * @return
	 */
	public String getNextSerialNo(String alias) {
		return serialNoService.genNextNo(alias);
	}
	
	/**
	 * 
	 * @param key 获取系统属性
	 * @return
	 */
	public String getProperty(String key) {
		return SysPropertyUtil.getByAlias(key);
	}
	
	
	public IUser getCurrentUser() {
		IUser user = ContextUtil.getCurrentUser();
		return user;
	}
	
	public String getCurrentGroupName() {
		 IGroup iGroup =ContextUtil.getCurrentGroup();
        if (iGroup!= null) {
            return iGroup.getGroupName();
        } else {
            return "";
        }
	}
	
	public String getCurrentUserName() {
		return ContextUtil.getCurrentUser().getFullname();
	}

	public void updateFileInstID(String instId,String... files){
		if(StringUtil.isEmpty(instId) || ArrayUtil.isEmpty(files))
			return;

		for(String fileJson : files) {
			JSONArray objects = JSON.parseArray(fileJson);
			if(objects != null && !objects.isEmpty()){
				objects.forEach(obj -> {
					String fileId = ((JSONObject) obj).getString("id") ;
					sysFileService.updateInstid(instId,fileId);
				});
			}
		}
	}
}
