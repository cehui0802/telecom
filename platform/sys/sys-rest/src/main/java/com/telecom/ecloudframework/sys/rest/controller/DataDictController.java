package com.telecom.ecloudframework.sys.rest.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.tree.DeleteAuth;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.alibaba.fastjson.JSONArray;
import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.sys.core.manager.DataDictManager;
import com.telecom.ecloudframework.sys.core.model.DataDict;


/**
 * 数据字典 控制器类<br/>
 * @author  aschs
 */
@RestController
@RequestMapping("/sys/dataDict")
public class DataDictController extends BaseController<DataDict> implements DeleteAuth {
	@Resource
	DataDictManager dataDictManager;
	
	
	@Override
	protected String getModelDesc(){
		return "数据字典";
	}
	
	@RequestMapping("getDictData")
	public ResultMsg<List<DataDict>> getByDictKey(@RequestParam String dictKey,@RequestParam(defaultValue="false") Boolean hasRoot) throws Exception{
		if(StringUtil.isEmpty(dictKey)) return null;
		
		List<DataDict> dict = dataDictManager.getDictNodeList(dictKey,hasRoot);
		return getSuccessResult(dict);
	}
	
	@RequestMapping("getDictList")
	@OperateLog
	public PageResult<List<DataDict>> getDictList(HttpServletRequest request) throws Exception{
		QueryFilter filter = getQueryFilter(request);
		filter.addFilter("dict_type_", DataDict.TYPE_DICT, QueryOP.EQUAL);
		List<DataDict> dict = dataDictManager.query(filter);
		return new PageResult(dict);
	}
	
	/**
	 * 获取所有数据字典，以tree的形式
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getDictTree")
	@CatchErr("获取数据字典失败")
	public ResultMsg<JSONArray> getDictTree() throws Exception{
		JSONArray dict = dataDictManager.getDictTree();
		return getSuccessResult(dict);
	}

	@Override
	public Boolean deleteAuth(String typeId) {
		//根据分类ID，查找该分类是否存在字典，若存在字典则不能删除。
		Integer count = dataDictManager.countDictByTypeId(typeId);
		if(count > 0){
			return false;
		}
		return true;
	}

	/**
	 * 保存
	 */
	@RequestMapping("save")
	@CatchErr
	@OperateLog
	public ResultMsg<String> save(@RequestBody DataDict t) {
		String desc;
		if (StringUtil.isEmpty(t.getId())) {
			desc = "添加%s成功";
			dataDictManager.create(t);
		} else {
			dataDictManager.update(t);
			desc = "更新%s成功";
		}
		return getSuccessResult(t.getId(),String.format(desc, getModelDesc()));
	}

	/**
	 * 批量删除
	 */
	@RequestMapping("remove")
	@CatchErr
	@OperateLog
	public ResultMsg<String> remove(@RequestParam String id) {
		String[] aryIds = StringUtil.getStringAryByStr(id);
		dataDictManager.removeByIds(aryIds);
		return getSuccessResult(String.format("删除%s成功", getModelDesc()));
	}
}
