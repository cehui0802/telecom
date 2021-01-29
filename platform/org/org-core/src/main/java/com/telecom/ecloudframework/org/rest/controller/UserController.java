package com.telecom.ecloudframework.org.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.aop.annotion.ParamValidate;
import com.telecom.ecloudframework.base.api.exception.BusinessError;
import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.encrypt.EncryptUtil;
import com.telecom.ecloudframework.base.core.util.FileUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.api.model.IUser;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.UserManager;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.org.core.model.User;
import com.telecom.ecloudframework.sys.api.platform.ISysPropertiesPlatFormService;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：用户表 控制器类
 * </pre>
 *
 * @author
 */
@RestController
@RequestMapping("/org/user/default")
@Api(description = "用户服务接口")
public class UserController extends BaseController<User> {
    @Resource
    UserManager userManager;
    @Resource
    GroupManager groupManager;
    @Resource
    private ICache<IUser> iCache;
    @Resource
    private ICache<Object> iCacheFile;
    @Resource
    private ICurrentContext iCurrentContext;
    @Resource
    ISysPropertiesPlatFormService iSysPropertiesPlatFormService;

    /**
     * 保存用户表信息，后端添加实体校验
     */
    @RequestMapping("save")
    @Override
    @CatchErr("操作用户失败！")
    @ParamValidate
    @OperateLog
    public ResultMsg<String> save(@RequestBody User user) {
        if (userManager.isUserExist(user)) {
            throw new BusinessMessage("用户在系统中已存在!");
        }

        userManager.saveUserInfo(user);
        return getSuccessResult(user.getId(), "保存成功");
    }

    @RequestMapping(value = "updateUserPassWorld", method = {RequestMethod.POST, RequestMethod.GET})
    @CatchErr("更新密码失败")
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "oldPassword", value = "旧密码"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "newPassword", value = "新密码"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "id", value = "当前用户ID")})
    public ResultMsg<String> updateUserPsw(HttpServletRequest request, HttpServletResponse response) {
        String oldPassWorld = RequestUtil.getRQString(request, "oldPassword", "旧密码必填");
        String newPassword = RequestUtil.getRQString(request, "newPassword", "新密码必填");
        String userId = RequestUtil.getRQString(request, "id", "当前用户ID");

        if (!userId.equals(ContextUtil.getCurrentUserId())) {
            throw new BusinessException("禁止修改他人密码！");
        }

//        if (AppUtil.getCtxEnvironment().contains(EnvironmentConstant.SIT.key())) {
//       	 throw new BusinessError("测试环境为了防止不法之徒恶意破坏演示数据，禁止修改密码！<br/>您的访问信息已经被我们统计！");
//        }

        User user = userManager.get(ContextUtil.getCurrentUserId());
        if (!user.getPassword().equals(EncryptUtil.encryptSha256(oldPassWorld))) {
            throw new BusinessMessage("旧密码输入错误");
        }
        //安全设置弱密码禁止保存
        String decentralizationEnable = iSysPropertiesPlatFormService.getByAlias("security.pwd.weakDisable");
        if (StringUtils.isNotEmpty(decentralizationEnable) && "true".equals(decentralizationEnable)) {
            if (StringUtils.isNotEmpty(newPassword) && newPassword.length() < 6) {
                throw new BusinessMessage("密码位数低于6位为弱密码，禁止保存");
            }
        }
        user.setPassword(EncryptUtil.encryptSha256(newPassword));
        userManager.update(user);
        return getSuccessResult("更新密码成功");

    }

    /**
     * 批量删除
     */
    @Override
    @RequestMapping("remove")
    @CatchErr
    @OperateLog
    public ResultMsg<String> remove(@RequestParam String id) throws Exception {
        User user = userManager.get(id);
        if (null == user) {
            throw new BusinessError("用户不存在");
        } else if (user.getActiveStatus() == 1 && user.getStatus() == 1) {
            //激活并且启用状态的用户不能删除
            throw new BusinessError("激活并且启用状态的用户不能删除");
        }
//        if (AppUtil.getCtxEnvironment().contains(EnvironmentConstant.SIT.key())) {
//          	 throw new BusinessError("测试环境为了防止不法之徒恶意破坏演示数据，禁止删除用户！<br/>您的访问信息已经被我们统计！");
//        }
        return super.remove(id);
    }

    @Override
    protected String getModelDesc() {
        return "用户";
    }

    /**
     * 修改用户状态
     *
     * @param userId
     * @param status
     * @return
     * @author 谢石
     * @date 2020-7-7 17:59
     */
    @RequestMapping(value = "status")
    @CatchErr
    public ResultMsg<String> status(@RequestParam(name = "userId") String userId, @RequestParam(name = "status") int status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        userManager.updateByPrimaryKeySelective(user);
        return getSuccessResult();
    }

    /**
     * 重置用户密码
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-7-7 18:14
     */
    @RequestMapping(value = "reset")
    @CatchErr
    public ResultMsg<String> reset(@RequestParam(name = "userId") String userId) {
        User user = new User();
        user.setId(userId);
        user.setPassword(EncryptUtil.encryptSha256("111111"));
        userManager.updateByPrimaryKeySelective(user);
        return getSuccessResult();
    }

    /**
     * 获取当前用户信息
     *
     * @return
     * @author 谢石
     * @date 2020-7-9 15:26
     */
    @RequestMapping(value = "getCurrentUserInfo")
    @CatchErr
    public ResultMsg<User> getCurrentUserInfo() {
        String userId = ContextUtil.getCurrentUserId();
        User user = new User();
        if (StringUtils.isNotEmpty(userId)) {
            user = userManager.get(userId);
            //添加登陆用户角色
            if (null != user && null != user.getOrgRelationList()) {
                OrgRelation orgRelation = new OrgRelation();
                orgRelation.setRoleAlias("ROLE_USER");
                orgRelation.setRoleName("登陆用户");
                orgRelation.setType(RelationTypeConstant.USER_ROLE.getKey());
                user.getOrgRelationList().add(orgRelation);
            }
        }
        return getSuccessResult(user);
    }

    /**
     * 获取启用用户数量
     *
     * @return
     * @author 谢石
     * @date 2020-7-9 16:01
     */
    @RequestMapping(value = "getAllEnableUserNum")
    @CatchErr
    public ResultMsg<Integer> getAllEnableUserNum() {
        return getSuccessResult(userManager.getAllEnableUserNum());
    }

    /**
     * 获取在线用户数量
     *
     * @return
     * @author 谢石
     * @date 2020-7-9 16:01
     */
    @RequestMapping(value = "onLineUser")
    @CatchErr
    public ResultMsg<Integer> onLineUser() {
        return getSuccessResult(iCache.keys("jwtToken:jwt:pc").size());
    }

    /**
     * 查询 用户
     *
     * @param request
     * @param response
     * @return
     * @author 谢石
     * @date 2020-9-2
     */
    @Override
    @RequestMapping("listJson")
    @OperateLog
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String orgIds = request.getParameter("orgIds");
        String roleIds = request.getParameter("roleIds");
        String postIds = request.getParameter("postIds");
        QueryFilter filter = getQueryFilter(request);
        if (StringUtils.isNotEmpty(name)) {
            filter.addFilter("tuser.fullname_", name, QueryOP.LIKE);
        }
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(orgIds)) {
            params.put("orgIds", orgIds.split(","));
        }
        if (StringUtils.isNotEmpty(roleIds)) {
            params.put("roleIds", roleIds.split(","));
        }
        if (StringUtils.isNotEmpty(postIds)) {
            params.put("postIds", postIds.split(","));
        }
        filter.addParams(params);
        List<User> pageList = userManager.query(filter);
        return new PageResult(pageList);
    }

    /**
     * 激活用户
     *
     * @param userId
     * @return
     * @author 谢石
     * @date 2020-9-17
     */
    @RequestMapping(value = "active")
    @CatchErr
    @OperateLog
    public ResultMsg<String> active(@RequestParam(name = "userId") String userId) {
        User user = new User();
        user.setId(userId);
        user.setActiveStatus(1);
        userManager.updateByPrimaryKeySelective(user);
        return getSuccessResult();
    }

    /**
     * 设置用户密级
     *
     * @param userId
     * @param secretLevel
     * @return
     * @author 谢石
     * @date 2020-9-17
     */
    @RequestMapping(value = "secretLevel")
    @CatchErr
    public ResultMsg<String> secretLevel(@RequestParam(name = "userId") String userId, @RequestParam(name = "secretLevel") Integer secretLevel) {
        User user = new User();
        user.setId(userId);
        user.setSecretLevel(secretLevel);
        userManager.updateByPrimaryKeySelective(user);
        return getSuccessResult();
    }

    /**
     * 更新头像
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updatePhoto", method = {RequestMethod.POST, RequestMethod.GET})
    @CatchErr("更新头像失败")
    @ApiOperation(value = "更新头像")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "photoId", value = "头像id")})
    public ResultMsg<String> updatePhoto(HttpServletRequest request, HttpServletResponse response) {
        String photoId = RequestUtil.getRQString(request, "photoId", "头像id必填");
        String userId = ContextUtil.getCurrentUserId();
        User user = new User();
        user.setId(userId);
        user.setPhoto(photoId);
        userManager.updateByPrimaryKeySelective(user);
        return getSuccessResult("更新头像成功");
    }

    /**
     * 更新用户基本信息
     *
     * @param request
     * @param response
     * @return
     * @author 谢石
     * @date 2020-9-28
     */
    @RequestMapping(value = "updateInfo", method = {RequestMethod.POST, RequestMethod.GET})
    @CatchErr("更新用户信息失败")
    @ApiOperation(value = "修改登录用户基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "email", value = "邮箱"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "mobile", value = "电话"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "telephone", value = "座机")})
    public ResultMsg<String> updateInfo(HttpServletRequest request, HttpServletResponse response) {
        String email = RequestUtil.getString(request, "email", null);
        String mobile = RequestUtil.getString(request, "mobile", null);
        String telephone = RequestUtil.getString(request, "telephone", null);
        String userId = ContextUtil.getCurrentUserId();
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setTelephone(telephone);
        userManager.updateByPrimaryKeySelective(user);
        return getSuccessResult("更新用户信息成功");
    }

    /**
     * 导入用户
     *
     * @param file
     * @return
     * @throws Exception
     * @author 谢石
     * @date 2020-10-23
     */
    @OperateLog(writeResponse = true)
    @RequestMapping(value = "/import", method = {RequestMethod.POST})
    @ResponseBody
    public ResultMsg<String> userImport(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        long totalNum = 0;
        long successNum = 0;
        if (null != file.getOriginalFilename() && (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx"))) {
            try {
                File realFile = FileUtil.multipartFileToFile(file);
                Workbook workBook = null;
                String type = "";
                if (file.getOriginalFilename().endsWith(".xls")) {
                    workBook = new HSSFWorkbook(new FileInputStream(realFile));
                    type = "xls";
                } else if (file.getOriginalFilename().endsWith(".xlsx")) {
                    workBook = new XSSFWorkbook(new FileInputStream(realFile));
                    type = "xlsx";
                }
                if (null != workBook) {
                    Sheet sheet = workBook.getSheetAt(0);
                    if (null != sheet) {
                        boolean hasError = false;
                        String[] cellName = {"编号", "主机构", "姓名", "账号", "密码", "邮箱", "手机号码", "座机号码", "地址", "排序字段", "性别", "机构"};
                        final Map<String, String> mapOrg = new HashMap<>();
                        int lastRowNum = sheet.getLastRowNum();
                        for (int i = 2; i <= lastRowNum; i++) {
                            Row data = sheet.getRow(i);
                            if (null == data) {
                                break;
                            }
                            StringBuilder errorMsg = new StringBuilder();
                            try {
                                String mainOrgPath = getCellStringData(data.getCell(1));
                                String name = getCellStringData(data.getCell(2));
                                String account = getCellStringData(data.getCell(3));
                                if (StringUtils.isEmpty(mainOrgPath) && StringUtils.isEmpty(name) && StringUtils.isEmpty(account)) {
                                    break;
                                }
                                totalNum++;
                                if (StringUtils.isEmpty(mainOrgPath)) {
                                    errorMsg.append("主机构不能为空").append("\n ");
                                }
                                if (StringUtils.isEmpty(name)) {
                                    errorMsg.append("姓名不能为空").append("\n ");
                                }
                                if (StringUtils.isEmpty(account)) {
                                    errorMsg.append("账号不能为空").append("\n ");
                                }
                                String password = getCellStringData(data.getCell(4));
                                if (StringUtils.isEmpty(password)) {
                                    errorMsg.append("密码不能为空").append("\n ");
                                }
                                String email = getCellStringData(data.getCell(5));
                                String mobile = getCellStringData(data.getCell(6));
                                if (StringUtils.isEmpty(mobile)) {
                                    errorMsg.append("手机号码不能为空").append("\n ");
                                }
                                String telephone = getCellStringData(data.getCell(7));
                                String address = getCellStringData(data.getCell(8));
                                String sn = getCellStringData(data.getCell(9));
                                int iSn = 0;
                                if (StringUtils.isNotEmpty(sn)) {
                                    try {
                                        iSn = Integer.parseInt(sn);
                                    } catch (Exception e) {
                                        errorMsg.append("“").append(sn).append("”非数字").append("\n ");
                                    }
                                }
                                String sex = getCellStringData(data.getCell(10));
                                if ("男".equals(sex)) {
                                    sex = "0";
                                } else if ("女".equals(sex)) {
                                    sex = "1";
                                } else {
                                    sex = "";
                                }
                                String orgPaths = getCellStringData(data.getCell(11));
                                if (errorMsg.length() == 0) {
                                    String mainOrgId = groupManager.findOrgId(mapOrg, mainOrgPath);
                                    User user = new User();
                                    User oldUser = userManager.getByAccount(account);
                                    if (null != oldUser) {
                                        user.setId(oldUser.getId());
                                    }
                                    user.setFullname(name);
                                    user.setAccount(account);
                                    user.setPassword(password);
                                    user.setEmail(email);
                                    user.setMobile(mobile);
                                    user.setTelephone(telephone);
                                    user.setAddress(address);
                                    user.setSn(iSn);
                                    user.setSex(sex);
                                    List<OrgRelation> orgRelationList = new ArrayList<>();
                                    OrgRelation orgRelation = new OrgRelation(mainOrgId, "", RelationTypeConstant.GROUP_USER.getKey());
                                    orgRelation.setIsMaster(1);
                                    orgRelationList.add(orgRelation);
                                    if (StringUtils.isNotEmpty(orgPaths)) {
                                        orgPaths = orgPaths.replaceAll("；", ";");
                                        String[] arrPath = orgPaths.split(";");
                                        for (String path : arrPath) {
                                            path = path.trim();
                                            String orgId = groupManager.findOrgId(mapOrg, path);
                                            if (!orgId.equals(mainOrgId)) {
                                                orgRelation = new OrgRelation(orgId, "", RelationTypeConstant.GROUP_USER.getKey());
                                                orgRelation.setIsMaster(0);
                                                orgRelationList.add(orgRelation);
                                            }
                                        }
                                    }
                                    user.setOrgRelationList(orgRelationList);
                                    userManager.saveUserInfo(user);
                                    successNum++;
                                    if (i == lastRowNum) {
                                        sheet.removeRow(data);
                                    } else {
                                        sheet.shiftRows(i + 1, lastRowNum, -1);
                                    }
                                    i--;
                                    lastRowNum--;
                                }
                            } catch (Exception e) {
                                errorMsg.append(e.getMessage()).append("\n ");
                            }
                            if (errorMsg.length() > 0) {
                                data.createCell(cellName.length).setCellValue(errorMsg.toString());
                                hasError = true;
                            }
                        }
                        if (hasError) {
                            sheet.setColumnWidth(cellName.length, 24 * 256);
                            workBook.write(new FileOutputStream(realFile));
                            String cacheKey = "file_upload_" + iCurrentContext.getCurrentUserId() + System.currentTimeMillis() + "." + type;
                            iCacheFile.add(cacheKey, realFile.getAbsolutePath());
                            return ResultMsg.SUCCESS(cacheKey);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                throw new BusinessMessage("未找到上传文件！");
            } catch (IOException e) {
                throw new BusinessMessage("上传文件失败！");
            } catch (Exception e) {
                throw new BusinessMessage("上传失败！");
            }
        } else {
            throw new BusinessMessage("文件格式错误！");
        }
        return ResultMsg.SUCCESS("导入成功,共" + totalNum + "条数据，" + successNum + "条导入成功.");
    }

    @RequestMapping(value = "/exportError", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> exportError(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "fileKey") String fileKey) {
        try {
            String path = String.valueOf(iCacheFile.getByKey(fileKey));
            iCacheFile.delByKey(fileKey);
            File realFile = new File(path);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "filename=user.xls");
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            return new ResponseEntity<>(IOUtils.toByteArray(new FileInputStream(realFile)), headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new BusinessMessage("下载失败！");
        }
    }


    /**
     * 获取单元格内容
     *
     * @param cell
     * @return
     * @author 谢石
     * @date 2020-10-23
     */
    private String getCellStringData(Cell cell) {
        String value = "";
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            value = cell.getStringCellValue();
            if (StringUtils.isNotBlank(value)) {
                value = value.trim();
            }
        }
        return value;
    }
}
