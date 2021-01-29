package com.telecom.ecloudframework.org.sync.service;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/org/Sync")
public class OrgSyncController extends ControllerTools{
    @Resource
    OrgSyncService orgSyncService;
    
    /**
                * 批量同步
     */
    @RequestMapping("fullOrgSync")
    @CatchErr("同步用户失败")
    public ResultMsg<String> fullOrgSync() throws Exception {
    	
    	orgSyncService.fullSyncOrg();
    	
        return getSuccessResult("同步成功");
    }

}
