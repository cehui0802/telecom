package com.telecom.ecloudframework.sys.api.platform;

import com.telecom.ecloudframework.sys.api.model.IDataDict;

import java.util.List;
import java.util.Map;

public interface ISysDataDictPlatFormService {
    List<? extends IDataDict>  getByDictKey(String dictKey, Boolean hasRoot);

    Map<String,List<? extends IDataDict>>  getByDictKeyList(Map<String,Boolean> dictKeyList);
}
