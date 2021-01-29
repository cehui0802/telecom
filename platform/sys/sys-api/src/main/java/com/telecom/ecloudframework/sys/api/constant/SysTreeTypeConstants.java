package com.telecom.ecloudframework.sys.api.constant;

/**
 * 系统树分类
 * @author jeff
 *
 */
public enum SysTreeTypeConstants {
	FLOW("flow","流程分类"),
	DICT("dict","数据字典");
	
	private String key;
	private String label;
	
	SysTreeTypeConstants(String key, String label){
		this.key = key;
		this.label = label;
	}
	public String key(){
		return key;		
	}
	public String label(){
		return label;		
	}	
	
	public static SysTreeTypeConstants getByKey(String key){
		for (SysTreeTypeConstants rights : SysTreeTypeConstants.values()) {
			if(rights.key.equals(key)){
				return rights;
			}
		}
		throw new RuntimeException(String.format(" key [%s] 对应RightsObjectConstants 不存在的权限常亮定义，请核查！",key));
	}
}
