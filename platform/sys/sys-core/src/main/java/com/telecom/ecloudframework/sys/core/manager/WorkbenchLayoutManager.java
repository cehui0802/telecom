package com.telecom.ecloudframework.sys.core.manager;

import com.telecom.ecloudframework.base.manager.Manager;
import com.telecom.ecloudframework.sys.core.model.WorkbenchLayout;

import java.util.List;

public interface WorkbenchLayoutManager extends Manager<String, WorkbenchLayout> {

    List<WorkbenchLayout> getByUserId(String currentUserId);

    void savePanelLayout(List<WorkbenchLayout> layOutList, String layoutKey);

}
