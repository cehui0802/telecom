package com.telecom.ecloudframework.org.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.util.FileUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.org.api.constant.RelationTypeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.manager.PostManager;
import com.telecom.ecloudframework.org.core.model.OrgRelation;
import com.telecom.ecloudframework.org.core.model.Post;
import com.telecom.ecloudframework.sys.api.model.IDataDict;
import com.telecom.ecloudframework.sys.api.platform.ISysDataDictPlatFormService;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 描述：岗位控制层
 * </pre>
 *
 * @author 谢石
 * @date 2020-7-1 11:35
 */
@RestController
@RequestMapping("/org/post/default")
@Api(description = "岗位服务接口")
public class PostController extends BaseController<Post> {
    @Resource
    PostManager postManager;
    @Resource
    OrgRelationManager orgRelationManager;
    @Autowired
    ICurrentContext currentContext;
    @Resource
    private ICache<Object> iCacheFile;
    @Resource
    private ICurrentContext iCurrentContext;
    @Resource
    GroupManager groupManager;

    @Override
    protected String getModelDesc() {
        return "岗位";
    }

    /**
     * 查询 岗位
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    @RequestMapping(value = "listJson", method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation(value = "岗位列表信息")
    @OperateLog
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "Integer", name = "offset", value = "偏移量"),
            @ApiImplicitParam(paramType = "form", dataType = "Integer", name = "limit", value = "分页大小，默认10"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "order", value = "排序，默认升序 ASC"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sort", value = "排序字段(可不传)"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "name", value = "岗位名称（模糊）"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "userId", value = "查询用户岗位列表"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "excludeUserId", value = "查询用户没有的岗位列表"),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "resultType", value = "结果类型 onlyGroupId|onlyGroupName|withUserNum")})
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) {

        String name = request.getParameter("name");
        String userId = request.getParameter("userId");
        String excludeUserId = request.getParameter("excludeUserId");
        String resultType = request.getParameter("resultType");
        QueryFilter filter = getQueryFilter(request);
        if (StringUtils.isNotEmpty(name)) {
            filter.addFilter("tpost.name_", name, QueryOP.LIKE);
        }
        if (StringUtils.isNotEmpty(userId)) {
            filter.getParams().put("userId", userId);
        }
        if (StringUtils.isNotEmpty(excludeUserId)) {
            filter.getParams().put("excludeUserId", excludeUserId);
        }
        if (StringUtils.isNotEmpty(resultType)) {
            filter.getParams().put("resultType", resultType);
        }
        List<Post> pageList = postManager.query(filter);
        return new PageResult(pageList);
    }

    /**
     * 转岗
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("moveUserPost")
    @CatchErr
    public ResultMsg<String> moveUserPost(HttpServletRequest request, HttpServletResponse response, @RequestBody List<OrgRelation> relations) {
        if (CollectionUtil.isNotEmpty(relations)) {
            relations.forEach(relation -> relation.setType(RelationTypeConstant.POST_USER.getKey()));
            orgRelationManager.modifyUserOrg(relations);
        }
        return getSuccessResult("转岗成功!");
    }

    /**
     * 保存
     */
    @Override
    @PostMapping("save")
    @CatchErr
    @OperateLog
    public ResultMsg<String> save(@RequestBody Post t) {
        String desc;
        if (StringUtil.isEmpty(t.getId())) {
            desc = "添加%s成功";
            postManager.create(t);
        } else {
            postManager.update(t);
            desc = "更新%s成功";
        }
        return getSuccessResult(t.getId(), String.format(desc, getModelDesc()));
    }

    /**
     * 批量删除
     */
    @Override
    @RequestMapping("remove")
    @CatchErr
    @OperateLog
    public ResultMsg<String> remove(@RequestParam String id) {
        String[] aryIds = StringUtil.getStringAryByStr(id);
        postManager.removeByIds(aryIds);
        return getSuccessResult(String.format("删除%s成功", getModelDesc()));
    }

    /**
     * 导入机构
     *
     * @param file
     * @return
     * @throws Exception
     * @author 谢石
     * @date 2020-11-3
     */
    @OperateLog(writeResponse = true)
    @RequestMapping(value = "/import", method = {RequestMethod.POST})
    @ResponseBody
    public ResultMsg<String> postImport(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
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
                        String[] cellName = {"编号", "部门", "名称", "编码", "类型", "是否为公务员", "描述"};
                        final Map<String, String> mapOrg = new HashMap<>();
                        int lastRowNum = sheet.getLastRowNum();
//                        List<? extends IDataDict> lstLevel = iSysDataDictPlatFormService.getByDictKey("gwyzj", true);
//                        List<? extends IDataDict> lstPostType = iSysDataDictPlatFormService.getByDictKey("postType", true);
                        Map<String, String> mapLevel = new HashMap<>();
                        Map<String, String> mapPostType = new HashMap<>();
//                        lstLevel.forEach(level -> mapLevel.put(level.getName(), level.getKey()));
//                        lstPostType.forEach(postType -> mapPostType.put(postType.getName(), postType.getKey()));
                        for (int i = 2; i <= lastRowNum; i++) {
                            Row data = sheet.getRow(i);
                            if (null == data) {
                                break;
                            }
                            StringBuilder errorMsg = new StringBuilder();
                            try {
                                String parentOrgPath = getCellStringData(data.getCell(1));
                                String name = getCellStringData(data.getCell(2));
                                String code = getCellStringData(data.getCell(3));
                                if (StringUtils.isEmpty(parentOrgPath) && StringUtils.isEmpty(name) && StringUtils.isEmpty(code)) {
                                    break;
                                }
                                totalNum++;
                                if (StringUtils.isEmpty(parentOrgPath)) {
                                    errorMsg.append("部门不能为空").append("\n ");
                                }
                                if (StringUtils.isEmpty(name)) {
                                    errorMsg.append("名称不能为空").append("\n ");
                                }
                                if (StringUtils.isEmpty(code)) {
                                    errorMsg.append("编码不能为空").append("\n ");
                                }
                                String postType = getCellStringData(data.getCell(4));
                                String isCivilServant = getCellStringData(data.getCell(5));
                                String level = getCellStringData(data.getCell(6));
                                String desc = getCellStringData(data.getCell(7));
                                String orgId = groupManager.findOrgId(mapOrg, parentOrgPath);

                                Post post = new Post();
                                Post oldPost = postManager.getByAlias(code);
                                post.setName(name);
                                post.setOrgId(orgId);
                                post.setCode(code);
                                if (StringUtils.isNotEmpty(postType)) {
                                    if (null == mapPostType.get(postType)) {
                                        errorMsg.append("类型：").append(postType).append("不存在").append("\n ");
                                    } else {
                                        post.setType(mapPostType.get(postType));
                                    }
                                }
                                int iIsCivilServant = 2;
                                if ("是".equals(isCivilServant)) {
                                    iIsCivilServant = 1;
                                }
                                post.setIsCivilServant(iIsCivilServant);
                                if (StringUtils.isNotEmpty(level)) {
                                    if (null == mapLevel.get(level)) {
                                        errorMsg.append("公务员职级：").append(level).append("不存在").append("\n ");
                                    } else {
                                        post.setLevel(mapLevel.get(level));
                                    }
                                }
                                post.setDesc(desc);
                                if (errorMsg.length() == 0) {
                                    if (null != oldPost) {
                                        post.setId(oldPost.getId());
                                        postManager.update(post);
                                    } else {
                                        postManager.create(post);
                                    }
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
