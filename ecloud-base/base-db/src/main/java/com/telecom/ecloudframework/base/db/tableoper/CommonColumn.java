package com.telecom.ecloudframework.base.db.tableoper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommonColumn {

    /**
     * 创建人字段
     */
    @Value("${ecloud.common-column.createUser:create_by}")
    private String createUser;

    /**
     * 创建时间
     */
    @Value("${ecloud.common-column.createTime:create_time}")
    private String createTime;

    @Value("${ecloud.common-column.updateUser:update_by}")
    private String updateUser;

    @Value("${ecloud.common-column.updateTime:update_time}")
    private String updateTime;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
