package com.telecom.ecloudframework.org.core.model;

import com.telecom.ecloudframework.base.core.model.BaseModel;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import com.telecom.ecloudframework.org.api.model.IGroup;

import java.util.List;


/**
 * 组 实体对象
 *
 * @author Jeff
 * @email for_office@qq.com
 * @time 2018-12-16 01:11:44
 */
public class Group extends BaseModel implements IGroup {
    private static final long serialVersionUID = -700694295167942753L;
    /**
     * 名字
     */
    protected String name;
    /**
     * 父ID
     */
    protected String parentId;
    /**
     * 编码
     */
    protected String code;
    /**
     * 类型
     */
    protected Integer type;
    /**
     * 描述
     */
    protected String desc;
    /**
     * path_
     */
    protected String path;
    /**
     * 排序
     */
    protected Integer sn;
    /**
     * 简称
     */
    protected String simple;

    /**
     * 岗位
     */
    protected List<OrgRelation> orgRelationList;
    /*------------------------------前端字段------------------------------*/
    /**
     * 组人数
     */
    protected Integer userNum;

    /**
     * 父机构名称
     */
    protected String parentName;


    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回 名字
     *
     * @return
     */
    public String getName() {
        return this.name;
    }


    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * 返回 父ID
     *
     * @return
     */
    @Override
    public String getParentId() {
        return this.parentId;
    }


    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 返回 编码
     *
     * @return
     */
    public String getCode() {
        return this.code;
    }


    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 返回 类型
     *
     * @return
     */
    public Integer getType() {
        return this.type;
    }


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 返回 描述
     *
     * @return
     */
    public String getDesc() {
        return this.desc;
    }


    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 返回 path_
     *
     * @return
     */
    @Override
    public String getPath() {
        return this.path;
    }

    public List<OrgRelation> getOrgRelationList() {
        return orgRelationList;
    }

    public void setOrgRelationList(List<OrgRelation> orgRelationList) {
        this.orgRelationList = orgRelationList;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    /**
     * 返回 排序
     *
     * @return
     */
    @Override
    public Integer getSn() {
        return this.sn;
    }

    @Override
    public String getGroupId() {
        return this.id;
    }

    @Override
    public String getGroupCode() {
        return this.code;
    }


    @Override
    public String getGroupType() {
        return GroupTypeConstant.ORG.key();
    }

    @Override
    public Integer getGroupLevel() {
        return this.type;
    }

    @Override
    public String getGroupName() {
        return this.name;
    }

    @Override
    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }

    @Override
    public String getSimple() {
        return simple;
    }

    public void setSimple(String simple) {
        this.simple = simple;
    }
}