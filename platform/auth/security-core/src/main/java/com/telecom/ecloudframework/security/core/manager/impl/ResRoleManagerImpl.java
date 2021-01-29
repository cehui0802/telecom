package com.telecom.ecloudframework.security.core.manager.impl;

import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.manager.impl.BaseManager;
import com.telecom.ecloudframework.security.constans.PlatformConsts;
import com.telecom.ecloudframework.security.core.dao.ResRoleDao;
import com.telecom.ecloudframework.security.core.manager.ResRoleManager;
import com.telecom.ecloudframework.security.core.manager.SysResourceManager;
import com.telecom.ecloudframework.security.core.model.ResRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <pre>
 * 描述：角色资源分配 处理实现类
 * </pre>
 *
 * @author
 */
@Service("resRoleManager")
public class ResRoleManagerImpl extends BaseManager<String, ResRole> implements ResRoleManager {
    @Resource
    ResRoleDao resRoleDao;
    @Resource
    ICache iCache;
    @Resource
    SysResourceManager sysResourceManager;


    @Override
    public void assignResByRoleSys(String resIds, String systemId, String roleId) {
        resRoleDao.removeByRoleAndSystem(roleId, systemId);

        String[] aryRes = resIds.split(",");
        for (String resId : aryRes) {
            if ("0".equals(resId)) {
                continue;
            }
            ResRole resRole = new ResRole(systemId, resId, roleId);
            resRoleDao.create(resRole);
        }
        cleanResoucesCache();
    }

    private Map<String, Set<String>> getUrlRoleMapping() {
        if (iCache.containKey(PlatformConsts.CACHE_KEY_URL_ROLE_MAPPING)) {
            Object o = iCache.getByKey(PlatformConsts.CACHE_KEY_URL_ROLE_MAPPING);
            if (o != null) {
                return (Map<String, Set<String>>) o;
            }
        }

        List<ResRole> list = resRoleDao.getAllResRole();
        Map<String, Set<String>> urlRoleMapping = new HashMap<>();

        for (ResRole res : list) {
            String url = res.getUrl();
            if (StringUtil.isEmpty(url)) {
                continue;
            }

            if (urlRoleMapping.containsKey(url)) {
                Set<String> set = urlRoleMapping.get(url);
                set.add(res.getRoleAlias());
            } else {
                Set<String> set = new HashSet<>();
                set.add(res.getRoleAlias());
                urlRoleMapping.put(url, set);
            }
        }
        //添加到缓存
        iCache.add(PlatformConsts.CACHE_KEY_URL_ROLE_MAPPING, urlRoleMapping);
        return urlRoleMapping;
    }

    private void cleanResoucesCache() {
        iCache.delByKey(PlatformConsts.CACHE_KEY_URL_ROLE_MAPPING);
    }


    /**
     * TODO 将 url accessRoleUrl 放进 set 结构的redis缓存中
     */
    @Override
    public Set<String> getAccessRoleByUrl(String url) {
        url = url.trim();
        if (StringUtil.isEmpty(url)) {
            return Collections.emptySet();
        }

        Map<String, Set<String>> urlMapping = getUrlRoleMapping();
        Set<String> urlAccessRoles = urlMapping.get(url);
        return urlAccessRoles;
    }

    /**
     * 删除
     *
     * @param systemId
     * @param roleId
     * @param resId
     * @author 谢石
     * @date 2020-7-15 16:32
     */
    @Override
    public void remove(String systemId, String roleId, String resId) {
        resRoleDao.removeByInfo(systemId, roleId, resId);
    }

    /**
     * 保存角色资源关联信息
     *
     * @param param
     * @author 谢石
     * @date 2020-9-17
     */
    @Override
    public void assignResByRoleSys(List<ResRole> param) {
        if (param.size() > 0) {
            ResRole resRole = param.get(0);
            String roleId = resRole.getRoleId();
            String systemId = resRole.getSystemId();
            List<String> lstResId = sysResourceManager.getResResList(roleId, systemId);
            Set<String> mapRoleRes = new HashSet<>(lstResId);
            param.forEach(temp -> {
                if (!"0".equals(temp.getResId())) {
                    if (!mapRoleRes.contains(temp.getResId())) {
                        resRoleDao.create(temp);
                    } else {
                        mapRoleRes.remove(temp.getResId());
                    }
                }
            });
            mapRoleRes.forEach(temp -> remove(systemId, roleId, temp));
            cleanResoucesCache();
        }
    }
}
