package com.telecom.ecloudframework.org.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessError;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.constant.UserTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 用户组织关系 控制器类<br/>
 *
 * @author Jeff
 * </pre>
 */
@RestController
@RequestMapping("/org/orgRelation/default")
@Api("用户组织关系服务接口")
public class OrgRelationController extends BaseController<OrgRelation> {
    @Resource
    OrgRelationManager orgRelationManager;
    @Autowired
    ICurrentContext currentContext;
    @Autowired
    @Qualifier("defaultUserService")
    UserService userService;



    @Override
    protected String getModelDesc() {
        return "用户组织关系";
    }

    /**
     * 查询 组用户
     */
    @RequestMapping("queryGroupUser")
    @CatchErr
    public PageResult queryGroupUser(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "path", required = false) String path,
                                     @RequestParam(value = "userName", required = false) String userName,
                                     @RequestParam(value = "userType", required = false) String userType,
                                     @RequestParam(value = "relationType", required = false) String relationType,
                                     @RequestParam(name = "groupId", required = false) String groupId) {
        //默认查询组织用户关系
        if (StringUtils.isEmpty(relationType)) {
            relationType = RelationTypeConstant.GROUP_USER.getKey();
        }
        //默认用户类型
        if (StringUtils.isEmpty(userType)) {
            userType = UserTypeConstant.NORMAL.key();
        }
        //查询 岗位 和 用户组的关系
        QueryFilter filter = getQueryFilter(request);
        filter.getParams().put("relationType", relationType);
        filter.getParams().put("orderBySql", "userCreateTime desc,relation.IS_MASTER_ desc");

        if (StringUtils.isNotEmpty(path)) {
            if (RelationTypeConstant.GROUP_USER.getKey().equals(relationType)) {
                filter.addFilter("tgroup.path_", path, QueryOP.RIGHT_LIKE);
            }
        }
        if (StringUtils.isNotEmpty(groupId)) {
            filter.addFilter("relation.group_id_", groupId, QueryOP.EQUAL);
        }
        if (StringUtils.isNotEmpty(userName)) {
            filter.addFilter("tuser.fullname_", userName, QueryOP.LIKE);
        }
        filter.addFilter("relation.type_", relationType, QueryOP.EQUAL);
        filter.addFilter("tuser.type_", userType, QueryOP.IN);

        List<OrgRelation> pageList = orgRelationManager.query(filter);
        return new PageResult(pageList);
    }

    /**
     * 设置主组织
     *
     * @param request
     * @param response
     * @return
     * @author 谢石
     * @date 2020-11-17
     */
    @PostMapping("setMaster")
    @ApiOperation(value = "设置主组织")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "groupId", value = "组织id"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "userId", value = "用户id"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "groupType", value = "组织类型")})
    @CatchErr
    public ResultMsg<String> setMaster(HttpServletRequest request, HttpServletResponse response) {
        String groupId = RequestUtil.getString(request, "groupId");
        String userId = RequestUtil.getString(request, "userId");
        String groupType = RequestUtil.getString(request, "groupType");
        orgRelationManager.updateUserGroupRelationIsMaster(groupId, userId, groupType);

        return getSuccessResult("设置用户主组织成功!");
    }


    @RequestMapping("disable")
    @CatchErr
    public ResultMsg<String> disable(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getRQString(request, "id");
        orgRelationManager.changeStatus(id, 0);

        return getSuccessResult("禁用成功!");
    }

    @RequestMapping("enable")
    @CatchErr
    public ResultMsg<String> enable(HttpServletRequest request, HttpServletResponse response) {
        String id = RequestUtil.getRQString(request, "id");
        orgRelationManager.changeStatus(id, 1);

        return getSuccessResult("启用成功!");
    }

    @RequestMapping("getGroupPost")
    @CatchErr
    public ResultMsg<List<OrgRelation>> getPostByGroupId(HttpServletRequest request, HttpServletResponse response) {
        String groupId = RequestUtil.getRQString(request, "groupId");
        List<OrgRelation> orgRelations = orgRelationManager.getGroupPost(groupId);

        return getSuccessResult(orgRelations);
    }

    @RequestMapping("saveGroupUserRel")
    @CatchErr
    public ResultMsg<String> saveGroupUserRel(HttpServletRequest request, HttpServletResponse response) {
        String groupId = RequestUtil.getRQString(request, "groupId");
        String[] roleIds = RequestUtil.getStringAryByStr(request, "roleIds");
        String[] userIds = RequestUtil.getStringAryByStr(request, "userIds");
        if (ArrayUtil.isEmpty(userIds)) {
            throw new BusinessError("请选择用户");
        }

        orgRelationManager.saveUserGroupRelation(groupId, roleIds, userIds);

        return getSuccessResult("添加成功");
    }

    @RequestMapping("saveRoleUsers")
    @CatchErr
    public ResultMsg<String> saveRoleUsers(HttpServletRequest request, HttpServletResponse response) {
        String roleId = RequestUtil.getString(request, "roleId");
        String[] userIds = RequestUtil.getStringAryByStr(request, "userIds");

        int i = orgRelationManager.saveRoleUsers(roleId, userIds);

        return getSuccessResult(i + "条用户角色添加成功");
    }

    /**
     * 查询 组用户
     */
    @RequestMapping("roleJson")
    public PageResult roleJson(HttpServletRequest request, HttpServletResponse response) {
        String roleId = RequestUtil.getRQString(request, "roleId", "查询 角色ID不能为空");
        //查询 岗位 和 用户组的关系
        QueryFilter filter = getQueryFilter(request);
        filter.addFilter("role.id_", roleId, QueryOP.EQUAL);
        filter.addFilter("relation.type_", RelationTypeConstant.USER_ROLE.getKey(), QueryOP.NOT_EQUAL);

        List<OrgRelation> pageList = orgRelationManager.query(filter);
        return new PageResult(pageList);
    }


    /**
     * 删除 角色、删除组织、删除岗位前进行校验
     * 删除角色 校验 岗位、岗位人员、角色人员是否存在
     * 删除组织、 校验岗位、组织人员
     * 删除岗位  校验岗位人员
     *
     * @param request
     * @return
     * @
     */
    @RequestMapping("removeCheck")
    @CatchErr
    public ResultMsg<String> removeCheck(HttpServletRequest request) {
        String groupId = RequestUtil.getString(request, "groupId");
        orgRelationManager.removeCheck(groupId);
        return getSuccessResult();
    }

    /**
     * 批量修改用户和组的关系
     *
     * @param relations
     * @return
     */
    @RequestMapping(value = "batchModityUserOrg")
    public ResultMsg<String> batchModifyUserOrg(@RequestBody List<OrgRelation> relations) {
        if (CollectionUtil.isNotEmpty(relations)) {
            orgRelationManager.modifyUserOrg(relations);
        }
        return getSuccessResult();
    }

    /**
     * 组下所有用户转组
     *
     * @param path
     * @param newGroupId
     * @param type
     * @return
     */
    @RequestMapping(value = "modifyAllUserGroup")
    public ResultMsg<String> modifyAllUserGroup(@RequestParam(value = "path", required = false) String path,
                                                @RequestParam(value = "oldGroupId", required = false) String oldGroupId,
                                                @RequestParam(value = "newGroupId") String newGroupId,
                                                @RequestParam(value = "type") String type) {
        orgRelationManager.modifyAllUserGroup(path, oldGroupId, newGroupId, type);
        return getSuccessResult();
    }

    /**
     * 批量添加关系
     *
     * @param relations
     * @return
     * @author 谢石
     * @date 2020-9-8
     */
    @PostMapping(value = "batchAdd")
    @ApiOperation(value = "批量添加关系")
    @CatchErr
    @OperateLog
    public ResultMsg<String> batchAdd(@RequestBody @ApiParam(value = "关系列表", required = true) List<OrgRelation> relations) {
        if (CollectionUtil.isNotEmpty(relations)) {
            orgRelationManager.batchAdd(relations);
        }
        return getSuccessResult();
    }

    /**
     * 批量删除关系
     *
     * @param relations
     * @return
     * @author 谢石
     * @date 2020-9-19
     */
    @PostMapping(value = "batchRemove")
    @ApiOperation(value = "批量删除关系")
    @CatchErr
    @OperateLog
    public ResultMsg<String> batchRemove(@RequestBody @ApiParam(value = "关系列表", required = true) List<OrgRelation> relations) {
        if (CollectionUtil.isNotEmpty(relations)) {
            orgRelationManager.batchRemove(relations);
        }
        return getSuccessResult();
    }
}
