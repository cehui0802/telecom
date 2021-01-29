package com.telecom.ecloudframework.org.api.model.dto;

import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.org.api.constant.GroupTypeConstant;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 描述：组查询条件 实体对象
 * </pre>
 *
 * @author guolihao
 * @date 2020/9/16 09:58
 */
public class GroupQueryDTO implements Serializable {
    /**
     * 拼接查询条件集合
     */
    List<QueryConfDTO> lstQueryConf;

    /**
     * 是否不要分页
     */
    Boolean noPage;
    /**
     * 分页查询偏移量
     */
    Integer offset;
    /**
     * 分页查询记录条数
     */
    Integer limit;
    /**
     * 用户id
     */
    String userId;
    /**
     * 组织ids列表
     */
    List<String> orgIds;
    /**
     * 组织types列表
     */
    List<String> types;
    /**
     * 是否包含子组织
     */
    Boolean orgHasChild;
    /**
     * 组织类型 {@linkplain GroupTypeConstant}
     */
    String groupType;
    /**
     * 组path条件
     */
    String groupPath;
    /**
     * 返回结果类型
     */
    String resultType;
    /**
     * 排序字段
     */
    String sort;
    /**
     * 排序，默认升序
     */
    String order;
    /**
     * 不带子组织ids列表
     */
    List<String> noHasChildOrgIds;
    /**
     * 只返回组id
     */
    final static String RESULT_TYPE_ONLY_GROUP_ID = "onlyGroupId";
    /**
     * 只返回组名
     */
    final static String RESULT_TYPE_ONLY_GROUP_NAME = "onlyGroupName";
    /**
     * 返回组下人员数
     */
    final static String RESULT_TYPE_WITH_USER_NUM = "withUserNum";

    /**
     * 默认构造函数
     */
    public GroupQueryDTO() {
        noPage = true;
        lstQueryConf = new ArrayList<>();
        orgIds = new ArrayList<>();
        types = new ArrayList<>();
        noHasChildOrgIds = new ArrayList<>();
    }

    /**
     * 分页
     *
     * @param pageNo   第几页
     * @param pageSize 一页显示多少
     * @return
     */
    public GroupQueryDTO page(int pageNo, int pageSize) {
        if (pageNo <= 0) {
            System.out.println("别闹，页数有负数或者零的吗");
            pageNo = 1;
        }
        if (pageSize <= 0) {
            System.out.println("别闹，能显示负数或者零条记录吗");
            pageNo = 10;
        }
        this.offset = (pageNo - 1) * pageSize;
        this.limit = pageSize;
        this.noPage = false;
        return this;
    }

    /**
     * 分偏移量分页
     *
     * @param offset 偏移量
     * @param limit  条数
     * @return
     */
    public GroupQueryDTO pageOffset(int offset, int limit) {
        if (offset < 0) {
            System.out.println("别闹，偏移量有负数的吗");
            offset = 0;
        }
        if (limit <= 0) {
            System.out.println("别闹，能显示负数或者零条记录吗");
            limit = 10;
        }
        this.offset = offset;
        this.limit = limit;
        this.noPage = false;
        return this;
    }

    /**
     * 用户id查询
     *
     * @param userId
     * @return
     */
    public GroupQueryDTO queryByUserId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * 组ids查询
     *
     * @param lstGroupId
     * @return
     */
    public GroupQueryDTO queryByGroupIds(List<String> lstGroupId) {
        String groupIds = "";
        if (null != lstGroupId) {
            groupIds = String.join(",", lstGroupId);
        }
        return queryByGroupIds(groupIds);
    }

    /**
     * 组groupTypes查询
     *
     * @param listGroupType
     * @return
     */
    public GroupQueryDTO queryByGroupTypes(List<String> listGroupType) {
        String groupTypes = "";
        if (null != listGroupType) {
            groupTypes = String.join(",", listGroupType);
        }
        return queryByGroupTypes(groupTypes);
    }

    /**
     * 组ids查询
     *
     * @param groupIds
     * @return
     */
    public GroupQueryDTO queryByGroupIds(String groupIds) {
        lstQueryConf.add(new QueryConfDTO("id_", QueryOP.IN.value(), groupIds));
        return this;
    }

    /**
     * 组父ids查询
     *
     * @param groupIds
     * @return
     */
    public GroupQueryDTO queryByParentGroupIds(String groupIds) {
        lstQueryConf.add(new QueryConfDTO("parent_id_", QueryOP.IN.value(), groupIds));
        return this;
    }

    /**
     * 组types查询
     *
     * @param groupTypes
     * @return
     */
    public GroupQueryDTO queryByGroupTypes(String groupTypes) {
        lstQueryConf.add(new QueryConfDTO("type_", QueryOP.IN.value(), groupTypes));
        return this;
    }

    /**
     * 组名查询
     *
     * @param groupName
     * @return
     */
    public GroupQueryDTO queryByGroupName(String groupName) {
        return queryByGroupName(groupName, QueryOP.LIKE);
    }

    /**
     * 用户名查询
     *
     * @param groupName
     * @param queryType
     * @return
     */
    public GroupQueryDTO queryByGroupName(String groupName, QueryOP queryType) {
        lstQueryConf.add(new QueryConfDTO("name_", queryType.value(), groupName));
        return this;
    }

    /**
     * 组路径查询
     *
     * @param groupPath
     * @return
     */
    public GroupQueryDTO queryByGroupPath(String groupPath) {
        this.groupPath = groupPath;
        return this;
    }

    /**
     * 设置排序字段
     *
     * @param sort
     * @param order
     * @return
     */
    public GroupQueryDTO queryOrderBy(String sort, String order) {
        this.sort = sort;
        if (!StringUtils.isEmpty(sort)) {
            if (StringUtils.isEmpty(sort)) {
                sort = "asc";
            }
            this.order = order;
        }
        return this;
    }

    /**
     * 设置查询结果只有组id
     *
     * @return
     */
    public GroupQueryDTO setResultTypeOnlyGroupId() {
        this.resultType = RESULT_TYPE_ONLY_GROUP_ID;
        return this;
    }

    /**
     * 设置查询结果只有组名称
     *
     * @return
     */
    public GroupQueryDTO setResultTypeOnlyGroupName() {
        this.resultType = RESULT_TYPE_ONLY_GROUP_NAME;
        return this;
    }

    /**
     * 设置查询结果含有组下人员数量
     *
     * @return
     */
    public GroupQueryDTO setResultTypeWithUserNum() {
        this.resultType = RESULT_TYPE_WITH_USER_NUM;
        return this;
    }

    public Boolean getNoPage() {
        return noPage;
    }

    public GroupQueryDTO setNoPage(Boolean noPage) {
        this.noPage = noPage;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public GroupQueryDTO setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public GroupQueryDTO setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public GroupQueryDTO setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getGroupType() {
        return groupType;
    }

    public GroupQueryDTO setGroupType(String groupType) {
        this.groupType = groupType;
        return this;
    }

    public String getGroupPath() {
        return groupPath;
    }

    public GroupQueryDTO setGroupPath(String groupPath) {
        this.groupPath = groupPath;
        return this;
    }

    public String getResultType() {
        return resultType;
    }

    public GroupQueryDTO setResultType(String resultType) {
        this.resultType = resultType;
        return this;
    }

    public List<QueryConfDTO> getLstQueryConf() {
        return lstQueryConf;
    }

    public GroupQueryDTO setLstQueryConf(List<QueryConfDTO> lstQueryConf) {
        this.lstQueryConf = lstQueryConf;
        return this;
    }

    public String getSort() {
        return sort;
    }

    public GroupQueryDTO setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public String getOrder() {
        return order;
    }

    public GroupQueryDTO setOrder(String order) {
        this.order = order;
        return this;
    }

    public List<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    public Boolean getOrgHasChild() {
        return orgHasChild;
    }

    public void setOrgHasChild(Boolean orgHasChild) {
        this.orgHasChild = orgHasChild;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getNoHasChildOrgIds() {
        return noHasChildOrgIds;
    }

    public void setNoHasChildOrgIds(List<String> noHasChildOrgIds) {
        this.noHasChildOrgIds = noHasChildOrgIds;
    }
}
