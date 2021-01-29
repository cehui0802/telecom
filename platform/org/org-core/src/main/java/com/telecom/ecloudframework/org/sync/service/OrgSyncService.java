package com.telecom.ecloudframework.org.sync.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.core.util.BeanCopierUtils;
import com.telecom.ecloudframework.base.core.util.RestTemplateUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.core.dao.GroupDao;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.manager.UserManager;
import com.telecom.ecloudframework.org.core.model.Group;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.org.core.model.User;
import com.telecom.ecloudframework.org.sync.orm.GroupJsonOrm;
import com.telecom.ecloudframework.org.sync.orm.UserGroupRelJsonOrm;
import com.telecom.ecloudframework.org.sync.orm.UserJsonOrm;
import com.telecom.ecloudframework.sys.util.SysPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrgSyncService {
	private static Logger LOG = LoggerFactory.getLogger(RestTemplateUtil.class);
	@Resource
	private GroupManager groupMananger;
	@Resource
    GroupDao groupDao;
	@Resource
	private UserManager userMananger;
	@Resource
	private OrgRelationManager relationManager;
	
	/**
	 * 每日零时完整的 主动形式组织同步
	 * 1.删除用户 form != system  的用户，创建同步用户
	 * 2.删除所有组织，创建同步的组织
	 * 3.删除所有 userOrg 用户组关系，创建同步的组织关系
	 */
//	@AbScheduled(cron="0 0 0 * * ? *")
	@Transactional
	public void fullSyncOrg(){
		LOG.info("=============== 开始同步组织用户！===============");
		//同步用户
		syncFullUser();
		
		//同步组织
		syncFullOrg();
		
		//同步 组织用户
		syncFullRelation();
		
		LOG.info("============同步组织用户成功！===============");
	}


	private void syncFullUser() {
		List<UserJsonOrm> userList  = this.getRemoteData("user_full_sync_url", UserJsonOrm.class,"users");
		
		// 删除掉其他system的所有用户
		userMananger.removeOutSystemUser();
		
		//保存用户
		userList.forEach(userOrm ->{
			User user = BeanCopierUtils.transformBean(userOrm, User.class);
			userMananger.create(user);
		});
	}

	private void syncFullOrg() {
		
		List<GroupJsonOrm> orgList  = this.getRemoteData("group_full_sync_url", GroupJsonOrm.class,"orgList");
		
		// 删除掉其他system的所有用户
		groupMananger.removeAll();
		
		//保存用户
		orgList.forEach(group ->{
			Group g = BeanCopierUtils.transformBean(group, Group.class);
			groupDao.create(g);
		});
	}
	
	private void syncFullRelation() {
		List<UserGroupRelJsonOrm> relationList  = this.getRemoteData("relation_full_sync_url", UserGroupRelJsonOrm.class,"relation");
		
		// 删除掉其他system的所有用户
		relationManager.removeAllRelation("groupUser");
		
		//保存用户
		relationList.forEach(rel ->{
			OrgRelation relation = BeanCopierUtils.transformBean(rel, OrgRelation.class);
			relationManager.create(relation);
		});
	}
	
	
	private <T> List<T> getRemoteData(String urlPropertyAlias,Class<T> dataType,String jsonKey) {
		//获取系统属性中url定义
		String url = SysPropertyUtil.getByAlias(urlPropertyAlias);
		if(StringUtil.isEmpty(url)) {
			LOG.warn("同步失败，同步URL未配置："+urlPropertyAlias);
			throw new BusinessException("同步失败");
		}
		
		//请求获取数据，若需要token，请自行实现RestTemplate请求方法
		JSONObject json  = null; //RestTemplateUtil.get(url, null,JSONObject.class);
		
		if(!json.containsKey("success") || !json.containsKey("data")) {
			LOG.warn("同步失败：" +json);
			throw new BusinessException("同步失败");
		}
		
		JSONArray jsonArray = json.getJSONObject("data").getJSONArray(jsonKey);
		List<T> list = JSON.parseArray(jsonArray.toJSONString(), dataType);
		
		//获取数据
		return list;
	}
	
	

	

}
