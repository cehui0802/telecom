package com.telecom.ecloudframework.base.errorlog.mode;

import com.telecom.ecloudframework.base.api.model.IDModel;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;


/**
 *  错误日志 实体对象
 *  @author Jeff
 */
public class LogErr implements IDModel {
	private static final long serialVersionUID = 1L;
	protected String id; /* 主键 */
    protected String account; /* 登录帐号 */
    protected String ip; /* IP地址 */
    protected String ipAddress; /* IP地址 */
	protected String url; /* URL地址 */
    protected String content; /* 内容 */
    protected String stackTrace;/*堆栈*/
    protected String status = "unchecked";//状态   checked fixed
    protected String requestParam;
    protected Date createTime; /* 创建时间 */
    protected String heads;
    public LogErr() {
        super();
    }

    public String getHeads() {
        return heads;
    }

    public void setHeads(String heads) {
        this.heads = heads;
    }

    public LogErr(String id, String account, String ip, String url,
                  String content, Date createTime) {
        super();
        this.id = id;
        this.account = account;
        this.ip = ip;
        this.url = url;
        this.content = content;
        this.createTime = createTime;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


    /**
     * 返回 主键
     *
     * @return
     */
    @Override
    public String getId() {
        return this.id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 返回 登录帐号
     *
     * @return
     */
    public String getAccount() {
        return this.account;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 返回 IP地址
     *
     * @return
     */
    public String getIp() {
        return this.ip;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 返回 URL地址
     *
     * @return
     */
    public String getUrl() {
        return this.url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 返回 内容
     *
     * @return
     */
    public String getContent() {
        return this.content;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 返回 创建时间
     *
     * @return
     */
    public Date getCreateTime() {
        return this.createTime;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getRequestParam() {
		return requestParam;
	}

	public void setRequestParam(String requestParam) {
		this.requestParam = requestParam;
	}

	public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id)
                .append("account", this.account).append("ip", this.ip)
                .append("url", this.url).append("content", this.content)
                .append("createTime", this.createTime).toString();
    }
}