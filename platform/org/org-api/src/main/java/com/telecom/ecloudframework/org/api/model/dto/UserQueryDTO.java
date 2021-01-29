package com.telecom.ecloudframework.org.api.model.dto;

import com.telecom.ecloudframework.base.api.query.QueryOP;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * 描述：用户查询条件 实体对象
 * </pre>
 *
 * @author 谢石
 * @date 2020-9-9
 */
public class UserQueryDTO implements Serializable {
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
     * 机构id条件
     * 例子：id1,id2
     */
    String orgIds;
    /**
     * 机构path条件
     */
    String orgPath;
    /**
     * 是否包含子机构条件
     */
    Boolean orgHasChild;
    /**
     * 角色id条件
     * 例子：id1,id2
     */
    String roleIds;
    /**
     * 岗位id条件
     * 例子：id1,id2
     */
    String postIds;
    /**
     * 用户群组id条件
     * 例子：id1,id2
     */
    String teamIds;
    /**
     * 常用组id条件
     * 例子：id1,id2
     */
    String teamCustomIds;
    /**
     * 是否最近选择
     */
    Boolean userSelectHistory;
    /**
     * 返回结果类型
     */
    String resultType;
    /**
     * 用户状态 1可用0不可用
     */
    String status;

    /**
     * 用户表别名
     */
    final static String TABLE_NAME_USER = "tuser";

    /**
     * 返回结果类型-只有用户id
     */
    final static String RESULT_TYPE_ONLY_USER_ID = "onlyUserId";
    /**
     * 返回结果类型-只有用户账号
     */
    final static String RESULT_TYPE_ONLY_USER_ACCOUNT = "onlyUserAccount";
    /**
     * 用户状态-可用
     */
    final static String USER_STATUS_ALIVE = "1";
    /**
     * 用户状态-不可用
     */
    final static String USER_STATUS_NOT_ALIVE = "0";

    /**
     * 默认构造函数
     */
    public UserQueryDTO() {
        noPage = true;
        status = USER_STATUS_ALIVE;
        lstQueryConf = new ArrayList<>();
    }

    /**
     * 分页
     *
     * @param pageNo   第几页
     * @param pageSize 一页显示多少
     * @return
     */
    public UserQueryDTO page(int pageNo, int pageSize) {
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
     * 偏移量分页
     *
     * @param offset 偏移量
     * @param limit  条数
     * @return
     */
    public UserQueryDTO pageOffset(int offset, int limit) {
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
     * 用户ids查询
     *
     * @param lstUserId
     * @return
     */
    public UserQueryDTO queryByUserIds(List<String> lstUserId) {
        String userIds = "";
        if (null != lstUserId) {
            userIds = String.join(",", lstUserId);
        }
        return queryByUserIds(userIds);
    }

    /**
     * 用户ids查询
     *
     * @param userIds
     * @return
     */
    public UserQueryDTO queryByUserIds(String userIds) {
        lstQueryConf.add(new QueryConfDTO(TABLE_NAME_USER + ".id_", QueryOP.IN.value(), userIds));
        return this;
    }

    /**
     * 用户名查询
     *
     * @param userName
     * @return
     */
    public UserQueryDTO queryByUserName(String userName) {
        return queryByUserName(userName, QueryOP.LIKE);
    }

    /**
     * 用户名查询
     *
     * @param userName
     * @param queryType
     * @return
     */
    public UserQueryDTO queryByUserName(String userName, QueryOP queryType) {
        lstQueryConf.add(new QueryConfDTO(TABLE_NAME_USER + ".fullname_", queryType.value(), userName));
        return this;
    }

    /**
     * 用户账号查询
     *
     * @param lstUserAccount
     * @return
     */
    public UserQueryDTO queryByUserAccounts(List<String> lstUserAccount) {
        String userAccounts = "";
        if (null != lstUserAccount) {
            userAccounts = String.join(",", lstUserAccount);
        }
        return queryByUserAccounts(userAccounts);
    }

    /**
     * 用户账号查询
     *
     * @param userAccounts
     * @return
     */
    public UserQueryDTO queryByUserAccounts(String userAccounts) {
        lstQueryConf.add(new QueryConfDTO(TABLE_NAME_USER + ".account_", QueryOP.IN.value(), userAccounts));
        return this;
    }

    /**
     * 机构id查询
     *
     * @param orgIds
     * @return
     */
    public UserQueryDTO queryByOrgIds(List<String> orgIds) {
        String temp = "";
        if (!StringUtils.isEmpty(orgIds)) {
            temp = String.join(",", orgIds);
        }
        return queryByOrgIds(temp);
    }

    /**
     * 机构id查询
     *
     * @param orgIds
     * @return
     */
    public UserQueryDTO queryByOrgIds(String orgIds) {
        return queryByOrgIds(orgIds, false);
    }

    /**
     * 机构id查询
     *
     * @param orgIds
     * @param orgHasChild
     * @return
     */
    public UserQueryDTO queryByOrgIds(List<String> orgIds, boolean orgHasChild) {
        String temp = "";
        if (!StringUtils.isEmpty(orgIds)) {
            temp = String.join(",", orgIds);
        }
        return queryByOrgIds(temp, orgHasChild);
    }

    /**
     * 机构id查询
     *
     * @param orgIds
     * @param orgHasChild
     * @return
     */
    public UserQueryDTO queryByOrgIds(String orgIds, boolean orgHasChild) {
        this.orgIds = orgIds;
        this.orgHasChild = orgHasChild;
        return this;
    }

    /**
     * 机构path查询
     *
     * @param orgPath
     * @return
     */
    public UserQueryDTO queryByOrgPath(String orgPath) {
        this.orgPath = orgPath;
        return this;
    }

    /**
     * 角色id查询
     *
     * @param roleIds
     * @return
     */
    public UserQueryDTO queryByRoleIds(List<String> roleIds) {
        if (!StringUtils.isEmpty(roleIds)) {
            this.roleIds = String.join(",", roleIds);
        }
        return this;
    }

    /**
     * 机构id查询
     *
     * @param roleIds
     * @return
     */
    public UserQueryDTO queryByRoleIds(String roleIds) {
        this.roleIds = roleIds;
        return this;
    }

    /**
     * 岗位id查询
     *
     * @param postIds
     * @return
     */
    public UserQueryDTO queryByPostIds(List<String> postIds) {
        if (!StringUtils.isEmpty(postIds)) {
            this.postIds = String.join(",", postIds);
        }
        return this;
    }

    /**
     * 岗位id查询
     *
     * @param postIds
     * @return
     */
    public UserQueryDTO queryByPostIds(String postIds) {
        this.postIds = postIds;
        return this;
    }

    /**
     * 设置查询结果只有用户id
     *
     * @return
     */
    public UserQueryDTO setResultTypeOnlyUserId() {
        this.resultType = RESULT_TYPE_ONLY_USER_ID;
        return this;
    }

    /**
     * 设置查询结果只有用户账号
     *
     * @return
     */
    public UserQueryDTO setResultTypeOnlyUserAccount() {
        this.resultType = RESULT_TYPE_ONLY_USER_ACCOUNT;
        return this;
    }

    /**
     * 用户群组id查询
     *
     * @param teamIds
     * @return
     */
    public UserQueryDTO queryByTeamIds(List<String> teamIds) {
        if (!StringUtils.isEmpty(teamIds)) {
            this.teamIds = String.join(",", teamIds);
        }
        return this;
    }

    /**
     * 用户群组id查询
     *
     * @param teamIds
     * @return
     */
    public UserQueryDTO queryByTeamIds(String teamIds) {
        this.teamIds = teamIds;
        return this;
    }

    /**
     * 常用组id查询
     *
     * @param teamCustomIds
     * @return
     */
    public UserQueryDTO queryByTeamCustomIds(List<String> teamCustomIds) {
        if (!StringUtils.isEmpty(teamCustomIds)) {
            this.teamCustomIds = String.join(",", teamCustomIds);
        }
        return this;
    }

    /**
     * 常用组id查询
     *
     * @param teamCustomIds
     * @return
     */
    public UserQueryDTO queryByTeamCustomIds(String teamCustomIds) {
        this.teamCustomIds = teamCustomIds;
        return this;
    }

    /**
     * 最近用户选择查询
     *
     * @return
     */
    public UserQueryDTO queryByUserSelectHistory() {
        this.userSelectHistory = true;
        return this;
    }

    /**
     * 查询可用用户
     *
     * @return
     */
    public UserQueryDTO queryAliveStatusUser() {
        this.status = USER_STATUS_ALIVE;
        return this;
    }

    /**
     * 查询不可用用户
     *
     * @return
     */
    public UserQueryDTO queryNotAliveStatusUser() {
        this.status = USER_STATUS_NOT_ALIVE;
        return this;
    }

    /**
     * 查询所有状态用户
     *
     * @return
     */
    public UserQueryDTO queryAllStatusUser() {
        this.status = null;
        return this;
    }

    public Boolean getNoPage() {
        return noPage;
    }

    public UserQueryDTO setNoPage(Boolean noPage) {
        this.noPage = noPage;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public UserQueryDTO setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public UserQueryDTO setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public String getOrgIds() {
        return orgIds;
    }

    public UserQueryDTO setOrgIds(String orgIds) {
        this.orgIds = orgIds;
        return this;
    }

    public String getOrgPath() {
        return orgPath;
    }

    public UserQueryDTO setOrgPath(String orgPath) {
        this.orgPath = orgPath;
        return this;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public UserQueryDTO setRoleIds(String roleIds) {
        this.roleIds = roleIds;
        return this;
    }

    public String getPostIds() {
        return postIds;
    }

    public UserQueryDTO setPostIds(String postIds) {
        this.postIds = postIds;
        return this;
    }

    public String getResultType() {
        return resultType;
    }

    public UserQueryDTO setResultType(String resultType) {
        this.resultType = resultType;
        return this;
    }

    public Boolean getOrgHasChild() {
        return orgHasChild;
    }

    public UserQueryDTO setOrgHasChild(Boolean orgHasChild) {
        this.orgHasChild = orgHasChild;
        return this;
    }

    public String getTeamIds() {
        return teamIds;
    }

    public UserQueryDTO setTeamIds(String teamIds) {
        this.teamIds = teamIds;
        return this;
    }

    public String getTeamCustomIds() {
        return teamCustomIds;
    }

    public UserQueryDTO setTeamCustomIds(String teamCustomIds) {
        this.teamCustomIds = teamCustomIds;
        return this;
    }

    public Boolean getUserSelectHistory() {
        return userSelectHistory;
    }

    public UserQueryDTO setUserSelectHistory(Boolean userSelectHistory) {
        this.userSelectHistory = userSelectHistory;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserQueryDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public List<QueryConfDTO> getLstQueryConf() {
        return lstQueryConf;
    }

    public UserQueryDTO setLstQueryConf(List<QueryConfDTO> lstQueryConf) {
        this.lstQueryConf = lstQueryConf;
        return this;
    }
}