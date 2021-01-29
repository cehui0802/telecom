package com.telecom.ecloudframework.org.api.model.dto;

import com.telecom.ecloudframework.base.api.query.QueryOP;

import java.io.Serializable;

/**
 * <pre>
 * 描述：查询条件 实体对象
 * </pre>
 *
 * @author 谢石
 * @date 2020-9-18
 */
public class QueryConfDTO implements Serializable {
    /**
     * 查询名称
     */
    String name;
    /**
     * 查询类型 {@linkplain QueryOP}
     */
    String type;
    /**
     * 查询值
     */
    String value;

    /**
     * 构造函数
     *
     * @param name
     * @param type
     * @param value
     */
    public QueryConfDTO(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
