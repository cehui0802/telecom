package com.telecom.ecloudframework.sys.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import com.telecom.ecloudframework.sys.core.dao.ScriptDao;
import org.springframework.stereotype.Service;

import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.sys.core.manager.ScriptManager;
import com.telecom.ecloudframework.sys.core.model.Script;


@Service("scriptManager")
public class ScriptManagerImpl extends BaseManager<String, Script> implements ScriptManager {
    @Resource
    private ScriptDao scriptDao;

    @Override
    public List<String> getDistinctCategory() {
        return scriptDao.getDistinctCategory();
    }

    @Override
    public Integer isNameExists(String name) {
        return scriptDao.isNameExists(name);
    }

}
