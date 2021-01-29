package com.telecom.ecloudframework.sys.core.model.def;

import com.telecom.ecloudframework.sys.api.model.ISysDataSourceDefAttribute;

/**
 * <pre>
 * 描述：SysDataSourceDef的属性字段，SysDataSource也直接复用了
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:下午3:25:25
 * 版权:summer
 * </pre>
 */
public class SysDataSourceDefAttribute implements ISysDataSourceDefAttribute {
    /**
     * 名字
     */

    private String name;
    /**
     * 描述
     */

    private String comment;
    /**
     * 参数类型
     */

    private String type;
    /**
     * 是否必填
     */
    private boolean required;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 值，这个字段
     */
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
