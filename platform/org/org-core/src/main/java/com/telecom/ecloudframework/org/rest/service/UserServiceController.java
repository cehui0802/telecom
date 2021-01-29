package com.telecom.ecloudframework.org.rest.service;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.response.impl.PageResultDto;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.api.model.IUserRole;
import com.telecom.ecloudframework.org.api.model.dto.UserQueryDTO;
import com.telecom.ecloudframework.org.api.service.UserService;
import com.telecom.ecloudframework.org.core.manager.UserManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <pre>
 * 描述：用户对外开放接口
 * </pre>
 *
 * @author 谢石
 */
@RestController
@RequestMapping("/org/userService/default")
@Api(description = "用户信息服务接口")
public class UserServiceController {
    @Resource
    UserManager userManager;


    @Autowired
    @Qualifier("defaultUserService")
    UserService userService;


    /**
     * 根据用户ID获取用户的对象。
     *
     * @param userId 用户编号
     * @return 结果信息
     */
    @GetMapping("/getUserById/{userId}")
    @ApiOperation(value = "获取用户信息", notes = "根据用户ID获取用户的对象")
    public ResultMsg<IUser> getUserById(@PathVariable("userId") @ApiParam(value = "用户ID", required = true) String userId) {
        IUser user = userService.getUserById(userId);
        return new ResultMsg<>(user);
    }


    /**
     * 根据用户帐号获取用户对象。
     *
     * @param account 账户
     * @return 结果信息
     */
    @GetMapping("/getUserByAccount/{account}")
    @ApiOperation(value = "获取用户信息", notes = "根据用户帐号获取用户对象")
    @CatchErr
    public ResultMsg<IUser> getUserByAccount(@PathVariable("account") @ApiParam(value = "用户账户", required = true) String account) {
        final IUser user = userService.getUserByAccount(account);
        if(null==user){
            throw new BusinessException("账号不存在");
        }
        return new ResultMsg<>(user);
    }


    /**
     * 根据组织id和组织类型获取用户列表。
     *
     * @param groupId   组织列表
     * @param groupType 组织类型
     * @return 结果信息
     */
    @PostMapping("/getUserListByGroup")
    @ApiOperation(value = "获取组用户", notes = "根据某组织下的用户")
    public ResultMsg<List<? extends IUser>> getUserListByGroup(@RequestParam("groupType") @ApiParam(value = "组类型：org,post,role", required = true) String groupType,
                                                               @RequestParam("groupId") @ApiParam(value = "组ID", required = true) String groupId) {
        List<? extends IUser> userList = userService.getUserListByGroup(groupType, groupId);
        return new ResultMsg<>(userList);
    }

    /**
     * 获取用户的角色
     *
     * @param userId 用户编号
     * @return 结果信息
     */
    @GetMapping("/getUserRole/{userId}")
    @ApiOperation(value = "获取组用户的角色", notes = "根据用户ID 获取该用户的角色")
    public ResultMsg<List<? extends IUserRole>> getUserRole(@PathVariable("userId") String userId) {
        List<? extends IUserRole> userRoleList = userService.getUserRole(userId);
        return new ResultMsg<>(userRoleList);
    }

    /**
     * 查询用户汇总接口
     *
     * @param userQuery
     * @return
     * @author 谢石
     * @date 2020-9-15
     */
    @PostMapping("/getUsersByUserQuery")
    @ApiOperation(value = "查询用户汇总接口(支持分页信息)", notes = "根据查询对象获取的用户集合")
    public PageResultDto getUsersByUserQuery(@RequestBody
                                             @ApiParam(value = "noPage:是否不要分页 默认true<br/>" +
                                                     "offset:分页查询偏移量<br/>" +
                                                     "limit:分页查询记录条数<br/>" +
                                                     "orgIds:机构id条件 例子：id1,id2<br/>" +
                                                     "orgPath:机构path条件<br/>" +
                                                     "orgHasChild:是否包含子机构条件<br/>" +
                                                     "roleIds:角色id条件<br/>" +
                                                     "postIds:岗位id条件<br/>" +
                                                     "teamIds:用户群组id条件 例子：id1,id2<br/>" +
                                                     "teamCustomIds:常用组id条件 例子：id1,id2<br/>" +
                                                     "userSelectHistory:是否最近选择true|false<br/>" +
                                                     "resultType:返回结果类型 onlyUserId|onlyUserAccount<br/>" +
                                                     "status:状态 1启动|0不启用 默认1<br/>" +
                                                     "lstQueryConf:灵活开放查询<br/>" +
                                                     "&nbsp;&nbsp;例子[{name:'',type:'',value:''},{name:'',type:'',value:''}]<br/>" +
                                                     "&nbsp;&nbsp;用户ids {name:'tuser.id_',type:'IN',value:'1,2'}<br/>" +
                                                     "&nbsp;&nbsp;用户名 {name:'tuser.fullname_',type:'LK',value:'张三'}<br/>" +
                                                     "&nbsp;&nbsp;用户账号 {name:'tuser.account_',type:'IN',value:'1,2'}<br/>", required = true) UserQueryDTO userQuery) {
        return userService.getUsersByUserQuery(userQuery);
    }
}
