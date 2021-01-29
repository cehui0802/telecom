package com.telecom.ecloudframework.sys.platform;

import com.telecom.ecloudframework.sys.api.model.IDataDict;
import com.telecom.ecloudframework.sys.api.platform.ISysDataDictPlatFormService;
import com.telecom.ecloudframework.sys.core.manager.DataDictManager;
import com.telecom.ecloudframework.sys.core.model.DataDict;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysDataDictPlatFormServiceImpl implements ISysDataDictPlatFormService {
    @Resource
    private DataDictManager dataDictManager;

    @Override
    public List<DataDict> getByDictKey(String dictKey, Boolean hasRoot) {
        return dataDictManager.getDictNodeList(dictKey, hasRoot);
    }

    @Override
    public Map<String, List<? extends IDataDict>> getByDictKeyList(Map<String, Boolean> dictKeyList) {
        Map<String, List<? extends IDataDict>> res = new HashMap<>(16);
        String tempKey;
        for (Map.Entry<String, Boolean> entry : dictKeyList.entrySet()) {
            tempKey = entry.getKey();
            res.put(tempKey, getByDictKey(tempKey, entry.getValue()));
        }
        return res;
    }
}
