package com.telecom.ecloudframework.sys.core.manager;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.core.model.DataDict;

/**
 * 数据字典 Manager处理接口
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-05-16 14:39:58
 */
public interface DataDictManager extends Manager<String, DataDict>{

	List<DataDict> getDictNodeList(String dictKey, Boolean hasRoot);

	JSONArray getDictTree();

	Integer countDictByTypeId(String typeId);
	
}
