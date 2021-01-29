package com.telecom.ecloudframework.base.operateLog.model;

import com.telecom.ecloudframework.base.api.model.IDModel;

import java.util.Date;

/**
 * 记录操作日志信息实体类
 *
 * @author guolihao
 * @date 2020/9/18 17:08
 */
public class LogOperate implements IDModel {
    /**
     * 主键
     */
    protected String id;
    /**
     * 操作人id
     */
    protected String userId;
    /**
     * 操作人
     */
    protected String account;
    /**
     * 操作人IP
     */
    protected String ip;
    /**
     * 请求参数
     */
    protected String requestParam;
    /**
     * 请求头
     */
    protected String requestHead;
    /**
     * 访问路径
     */
    protected String path;
    /**
     * 操作时间
     */
    protected Date operateTime;
    /**
     * 操作结果（0：失败；1：成功）
     */
    protected Integer result;
    /**
     * 请求返回内容
     */
    protected String responseResultData;
    /**
     * 备份文件名称
     */
    protected String backupFileName;
    /**
     * 备份文件类型
     */
    protected String backupFileType;
    /**
     * 1：普通日志；2：备份日志
     */
    protected Integer logType = 1;
    /**
     * 操作
     */
    protected String operateType;
    /**
     * 操作项
     */
    protected String operateItem;
    /**
     * 操作项名称key
     */
    protected String operateItemNameKey;
    /**
     * 所属系统
     */
    protected String system;
    /**
     * 1：普通操作；2：流程操作
     */
    protected Integer operateObjectType;
    /**
     * 日志类型
     */
    protected Integer type;
    /**
     * 创建时间
     */
    protected Date createTime;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateItem() {
        return operateItem;
    }

    public void setOperateItem(String operateItem) {
        this.operateItem = operateItem;
    }

    public String getOperateItemNameKey() {
        return operateItemNameKey;
    }

    public void setOperateItemNameKey(String operateItemNameKey) {
        this.operateItemNameKey = operateItemNameKey;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Integer getOperateObjectType() {
        return operateObjectType;
    }

    public void setOperateObjectType(Integer operateObjectType) {
        this.operateObjectType = operateObjectType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getRequestHead() {
        return requestHead;
    }

    public void setRequestHead(String requestHead) {
        this.requestHead = requestHead;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getResponseResultData() {
        return responseResultData;
    }

    public void setResponseResultData(String responseResultData) {
        this.responseResultData = responseResultData;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBackupFileName() {
        return backupFileName;
    }

    public void setBackupFileName(String backupFileName) {
        this.backupFileName = backupFileName;
    }

    public String getBackupFileType() {
        return backupFileType;
    }

    public void setBackupFileType(String backupFileType) {
        this.backupFileType = backupFileType;
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }
}
