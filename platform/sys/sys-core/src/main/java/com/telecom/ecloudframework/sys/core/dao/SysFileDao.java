package com.telecom.ecloudframework.sys.core.dao;
import com.telecom.ecloudframework.base.dao.BaseDao;
import com.telecom.ecloudframework.sys.core.model.SysFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统附件 DAO接口
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-06-07 23:54:49
 */
public interface SysFileDao extends BaseDao<String, SysFile> {
    /**
     * <pre>
     * 更新流程实例ID
     * </pre>
     * @param instId
     * @param id
     */
    void updateInstid(@Param("instId")String instId, @Param("id")String id);

    /**
     * 获取流程实例的附件列表
     * @param instId
     * @return
     */
    List<SysFile> getFileByInstId(@Param("instId")String instId);
}
