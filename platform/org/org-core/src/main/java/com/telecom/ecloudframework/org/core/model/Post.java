package com.telecom.ecloudframework.org.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import com.telecom.ecloudframework.org.api.model.IGroup;

/**
 * <pre>
 * 描述：岗位实体
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-1 11:21
 */
public class Post extends BaseModel implements IGroup {
    private static final long serialVersionUID = -700694295167942753L;
    /**
     * 名字
     */
    protected String name;
    /**
     * 编码
     */
    protected String code;
    /**
     * 类型
     */
    protected String type;
    /**
     * 描述
     */
    protected String desc;
    /**
     * 是否公务员
     */
    protected Integer isCivilServant;
    /**
     * 公务员级别
     */
    protected String level;
    /**
     * 机构id
     */
    protected String orgId;
    /*------------------------------前端字段------------------------------*/
    /**
     * 组人数
     */
    protected Integer userNum;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getIsCivilServant() {
        return isCivilServant;
    }

    public void setIsCivilServant(Integer isCivilServant) {
        this.isCivilServant = isCivilServant;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String getGroupId() {
        return this.id;
    }

    @Override
    public String getGroupName() {
        return this.name;
    }

    @Override
    public String getGroupCode() {
        return this.code;
    }

    @Override
    public String getGroupType() {
        return GroupTypeConstant.POST.key();
    }

    @Override
    public Integer getGroupLevel() {
        return null;
    }

    @Override
    public String getParentId() {
        return null;
    }

    @Override
    public Integer getSn() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public Integer getUserNum() {
        return userNum;
    }

    @Override
    public String getSimple() {
        return null;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }
}