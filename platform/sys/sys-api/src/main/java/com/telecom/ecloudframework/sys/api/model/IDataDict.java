package com.telecom.ecloudframework.sys.api.model;

public interface IDataDict {
    /**
     * 上级id
     */
    String getParentId();
    /**
     * key
     */
    String getKey();
    /**
     * name
     */
    String getName();
    /**
     * 字典key
     */
    String getDictKey();
    /**
     * 分组id
     */
    String getTypeId();
    /**
     * 排序
     */
    Integer getSn();
    /**
     * dict/node字典项
     */
    String getDictType();
    /**
     * 是否删除
     */
    String getDeleteFlag();
}
