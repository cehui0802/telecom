package com.telecom.ecloudframework.sys.upload;

import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.core.util.AppUtil;
import com.telecom.ecloudframework.base.core.util.PropertyUtil;

import java.util.Map;
import java.util.Map.Entry;

/**
 * <pre>
 * 描述：上传器工厂
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2018年6月6日
 * 版权:summer
 * </pre>
 */
public class UploaderFactory {
	private UploaderFactory() {

	}

	/**
	 * <pre>
	 * 获取上传器
	 * </pre>
	 * 
	 * @param type
	 * @return
	 */
	public static IUploader getUploader(String type) {
		Map<String, IUploader> map = AppUtil.getImplInstance(IUploader.class);
		for (Entry<String, IUploader> entry : map.entrySet()) {
			if (entry.getValue().type().equals(type)) {
				return entry.getValue();
			}
		}
		throw new BusinessException(String.format("找不到类型[%s]的上传器的实现类", type));
	}

	/**
	 * <pre>
	 * 返回默认的上传器
	 * </pre>
	 * 
	 * @return
	 */
	public static IUploader getDefault() {
		return getUploader(PropertyUtil.getProperty("ecloud.uploader.default"));
	}
}
