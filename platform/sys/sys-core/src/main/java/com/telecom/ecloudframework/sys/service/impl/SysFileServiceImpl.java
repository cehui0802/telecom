package com.telecom.ecloudframework.sys.service.impl;

import com.telecom.ecloudframework.base.core.util.BeanCopierUtils;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.sys.api.model.dto.SysFileDTO;
import com.telecom.ecloudframework.sys.api.service.SysFileService;
import com.telecom.ecloudframework.sys.core.manager.SysFileManager;
import com.telecom.ecloudframework.sys.core.model.SysFile;
import cn.hutool.core.util.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysFileServiceImpl implements SysFileService {
	@Autowired
	private SysFileManager sysFileMananger;

	@Override
	public SysFileDTO upload(InputStream is, String fileName) {
		SysFile file = sysFileMananger.upload(is, fileName);
		return BeanCopierUtils.transformBean(file, SysFileDTO.class);
	}

	@Override
	public SysFileDTO upload(InputStream is, SysFileDTO sysFileDTO) {
		SysFile sysFile = BeanCopierUtils.transformBean(sysFileDTO, SysFile.class);
		sysFileMananger.upload(is, sysFile) ;
		return BeanCopierUtils.transformBean(sysFile, SysFileDTO.class);
	}

	@Override
	public InputStream download(String fileId) {
		return sysFileMananger.download(fileId);
	}

	@Override
	public void delete(String ... fileId) {
		if(ArrayUtil.isEmpty(fileId))return;
		
		for(String id : fileId) {
			sysFileMananger.delete(id);
		}
	}

	@Override
	public void updateInstid(String instId, String fileId) {
		if(StringUtil.isEmpty(instId) || StringUtil.isEmpty(fileId))return;

		sysFileMananger.updateInstid(instId,fileId);
	}

	@Override
	public List<SysFileDTO> getFileByInstId(String instanceId) {
		List<SysFile> sysFiles=sysFileMananger.getFileByInstId(instanceId);
		List<SysFileDTO> dtos=new ArrayList<>(0);
		if(!CollectionUtils.isEmpty(sysFiles)){
			for (SysFile sysFile:sysFiles){
				dtos.add(BeanCopierUtils.transformBean(sysFile, SysFileDTO.class));
			}
		}
		return dtos;
	}

	@Override
	public SysFileDTO get(String id){
		SysFile sysFile=sysFileMananger.get(id);
		if(null!=sysFile){
			return BeanCopierUtils.transformBean(sysFileMananger.get(id), SysFileDTO.class);
		}else{
			return null;
		}

	}

}
