package com.telecom.ecloudframework.sys.api.model;

import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.org.api.model.IUser;

/**
 * <pre>
 * 描述：默认的SysIdentity
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2019年5月26日
 * 版权:summer
 * </pre>
 */
public class DefaultIdentity implements SysIdentity<DefaultIdentity> {

    private static final long serialVersionUID = -5039484524490929855L;
    private String id;
    private String name;
    private String type;
    private Integer sn = 0;

    public DefaultIdentity() {

    }

    /**
     * 创建一个新的实例 DefaultDefaultIdentity.
     *
     * @param id
     * @param name
     * @param type
     */
    public DefaultIdentity(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public DefaultIdentity(String id, String name, String type, Integer sn) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.sn = sn;
    }

    public DefaultIdentity(IUser user) {
        this.id = user.getUserId();
        this.name = user.getFullname();
        this.type = TYPE_USER;
        this.sn = user.getSn();
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode() + this.type.hashCode();
    }

    @Override
    public String toString() {
        return "DefaultIdentity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", sn=" + sn +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SysIdentity)) {
            return false;
        }

        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(name)) {
            return false;
        }

        SysIdentity identity = (SysIdentity) obj;

        if (this.type.equals(identity.getType()) && this.id.equals(identity.getId())) {
            return true;
        }

        return false;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    @Override
    public int compareTo(DefaultIdentity identity) {

        if (this.sn == null || identity.sn == null){
            return 0;
        }
        if (this.getSn() == identity.getSn()) {
            return 0;
        }

        if (this.getSn() > identity.getSn()) {
            return 1;
        }
        return -1;
    }

}
