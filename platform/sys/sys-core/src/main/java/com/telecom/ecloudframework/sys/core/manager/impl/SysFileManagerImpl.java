package com.telecom.ecloudframework.sys.core.manager.impl;

import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.core.id.IdUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.sys.core.dao.SysFileDao;
import com.telecom.ecloudframework.sys.upload.IUploader;
import com.telecom.ecloudframework.sys.upload.UploaderFactory;
import com.telecom.ecloudframework.sys.core.manager.SysFileManager;
import com.telecom.ecloudframework.sys.core.model.SysFile;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统附件 Manager处理实现类
 *
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-06-07 23:54:49
 */
@Service("sysFileManager")
public class SysFileManagerImpl extends BaseManager<String, SysFile> implements SysFileManager {
	@Resource
    SysFileDao sysFileDao;
	@Resource
	UserService userService;
	@Override
	public SysFile upload(InputStream is, String fileName) {
		String ext = fileName.substring(fileName.lastIndexOf('.'));
		String id = IdUtil.getSuid();

		IUploader uploader = UploaderFactory.getDefault() ;
		// 1 先上传文件
		String path = uploader.upload(is, id + ext);//以id为文件名保证不重复

		// 2 建立SysFile数据
		SysFile sysFile = new SysFile();
		sysFile.setId(id);
		sysFile.setName(fileName);
		sysFile.setUploader(uploader.type());
		sysFile.setPath(path);
		create(sysFile);

		return sysFile;
	}

	@Override
	public void upload(InputStream is, SysFile sysFile) {
		String fileName = sysFile.getName() ;
		String uploaderType = sysFile.getUploader() ;
		String ext = fileName.substring(fileName.lastIndexOf('.'));
		String id = IdUtil.getSuid();

		IUploader uploader = UploaderFactory.getDefault() ;
		if(StringUtil.isNotEmpty(uploaderType)){
			uploader = UploaderFactory.getUploader(uploaderType) ;
		}else{
			sysFile.setUploader(uploader.type());
		}

		// 1 先上传文件
		String path = uploader.upload(is, id + ext);//以id为文件名保证不重复

		// 2 建立SysFile数据
		sysFile.setId(id);
		sysFile.setPath(path);
		create(sysFile);
	}

	@Override
	public void modify(InputStream inputStream, SysFile newFileInfo, String fileId) {
		// 上传新真实文件
		SysFile oldFileInfo = get(fileId);
		String oldUploaderStr = oldFileInfo.getUploader(), newUploaderStr = newFileInfo.getUploader();
		IUploader oldUploader = UploaderFactory.getUploader(oldUploaderStr), newUploader;
		if (StringUtil.isNotEmpty(newUploaderStr)) {
			newUploader = UploaderFactory.getUploader(newUploaderStr);
			oldFileInfo.setUploader(newUploaderStr);
		} else {
			newUploader = oldUploader;
		}
		String newFileInfoName = newFileInfo.getName();
		String ext = newFileInfoName.substring(newFileInfoName.lastIndexOf('.'));
		String oldPath = oldFileInfo.getPath();
		String path = newUploader.modify(inputStream, fileId + ext, oldPath);

		// 删除旧真实文件
		if (!oldPath.equals(path)){
			// 当保存路径不一致时，说面旧文件没有被覆盖修改，需要删除旧文件，并保存新路径
		    oldUploader.remove(oldFileInfo.getPath());
            oldFileInfo.setPath(path);
		}

		// 更新文件信息表
		// TODO 其他信息暂未更新，需要根据场景判断更新逻辑
		oldFileInfo.setName(newFileInfoName);
		update(oldFileInfo);
	}

	@Override
	public Map uploadUrl(String fileName, String remark, String uploaderType) {

		IUploader uploader = UploaderFactory.getDefault() ;
		if(StringUtil.isNotEmpty(uploaderType)){
			uploader = UploaderFactory.getUploader(uploaderType) ;
		}

		String id = IdUtil.getSuid();
		SysFile sysFile = new SysFile();
		sysFile.setId(id);
		sysFile.setName(fileName);
		sysFile.setUploader(uploader.type());
		sysFile.setRemark(remark);

		String url = uploader.uploadUrl(sysFile);
		create(sysFile);
		Map<String, String> uploadMap = new HashMap<>();
		uploadMap.put("fileid" , sysFile.getId()) ;
		uploadMap.put("path" , sysFile.getPath()) ;
		uploadMap.put("url" , url) ;
		return uploadMap;
	}

	@Override
	public String downloadUrl(String fileId) {
		SysFile sysFile = get(fileId);
		IUploader uploader = UploaderFactory.getUploader(sysFile.getUploader());
		return uploader.downloadUrl(sysFile);
	}

	@Override
	public InputStream download(String fileId) {
		SysFile sysFile = get(fileId);
		IUploader uploader = UploaderFactory.getUploader(sysFile.getUploader());
		return uploader.take(sysFile.getPath());
	}

	@Override
	public void delete(String fileId) {
		SysFile sysFile = get(fileId);
		IUploader uploader = UploaderFactory.getUploader(sysFile.getUploader());
		uploader.remove(sysFile.getPath());
		remove(fileId);
		// 做逻辑删除
//        sysFile.setDelete(true);
//        update(sysFile);
	}

	@Override
	public void updateInstid(String instId, String fileId) {
		sysFileDao.updateInstid(instId, fileId);
	}

	@Override
	public List<SysFile> getFileByInstId(String instId) {
		return sysFileDao.getFileByInstId(instId);
	}

	@Override
	public List<SysFile> query(QueryFilter queryFilter){
		List<SysFile> sysFiles = dao.query(queryFilter);
		sysFiles.forEach(sysFile -> {
			IUser user = userService.getUserById(sysFile.getCreateBy());
			if (user != null){
				sysFile.setCreator(user.getFullname());
			}
		});
		return sysFiles;

	}

	@Override
	 public SysFile get(String id) {
		return sysFileDao.get(id);
	}
}
