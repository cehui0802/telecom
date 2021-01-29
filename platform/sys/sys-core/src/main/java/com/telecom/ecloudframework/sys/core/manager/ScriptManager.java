package com.telecom.ecloudframework.sys.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.core.model.Script;

import java.util.List;


public interface ScriptManager extends Manager<String, Script> {

    /**
     * 返回所有脚本的分类
     *
     * @return
     */
    public List<String> getDistinctCategory();

    /**
     * 判断脚本名称是否存在
     *
     * @param name
     * @return
     */
    public Integer isNameExists(String name);
}
