package com.telecom.ecloudframework.sys.api.service;

import com.telecom.ecloudframework.sys.api.model.dto.DataDictDTO;

import java.util.List;

/**
 * 数据字典 Manager处理接口
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-05-16 14:39:58
 */
public interface DataDictService {
	
	List<DataDictDTO> getDictNodeList(String dictKey, Boolean hasRoot);
	
}
