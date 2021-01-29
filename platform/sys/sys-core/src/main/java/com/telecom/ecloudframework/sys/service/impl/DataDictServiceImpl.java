package com.telecom.ecloudframework.sys.service.impl;

import com.telecom.ecloudframework.base.core.util.BeanCopierUtils;
import com.telecom.ecloudframework.sys.api.model.dto.DataDictDTO;
import com.telecom.ecloudframework.sys.api.service.DataDictService;
import com.telecom.ecloudframework.sys.core.dao.DataDictDao;
import com.telecom.ecloudframework.sys.core.manager.SysTreeManager;
import com.telecom.ecloudframework.sys.core.manager.SysTreeNodeManager;
import com.telecom.ecloudframework.sys.core.model.DataDict;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典 处理实现类
 * @author xianggang
 * @date 2019年9月3日 下午4:55:31
 */
@Service("dataDictService")
public class DataDictServiceImpl implements DataDictService {
	@Resource
    DataDictDao dataDictDao;
	@Resource
	SysTreeNodeManager sysTreeNodeMananger;
	@Resource
	SysTreeManager sysTreeMananger;
	
	/**
	 * 
	 * @param dictKey
	 * @param hasRoot
	 * @return
	 * @author xianggang
	 * @date 2019年9月3日 下午4:55:31
	 */
	@Override
	public List<DataDictDTO> getDictNodeList(String dictKey, Boolean hasRoot) {
		List<DataDict> dictNodeList = dataDictDao.getDictNodeList(dictKey, hasRoot);
		if (dictNodeList == null) {
			return null;
		}
		List<DataDictDTO> dictList = new ArrayList<DataDictDTO>();
		for (DataDict dataDict : dictNodeList) {
			DataDictDTO dto = new DataDictDTO();
			BeanCopierUtils.copyProperties(dataDict, dto);
			dictList.add(dto);
		}
		return dictList;
	}
	
}
