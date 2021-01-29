package com.telecom.ecloudframework.org.rest.controller;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.aop.annotion.OperateLog;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.cache.ICache;
import com.telecom.ecloudframework.base.core.util.BeanUtils;
import com.telecom.ecloudframework.base.core.util.FileUtil;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.BaseController;
import com.telecom.ecloudframework.base.rest.util.RequestUtil;
import com.telecom.ecloudframework.org.api.constant.GroupGradeConstant;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.org.core.manager.GroupManager;
import com.telecom.ecloudframework.org.core.manager.OrgRelationManager;
import com.telecom.ecloudframework.org.core.manager.UserManager;
import com.telecom.ecloudframework.org.core.model.Group;
import com.telecom.ecloudframework.org.core.model.OrgTree;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组控制层
 *
 * @author 谢石
 */
@RestController
@RequestMapping("/org/group/default")
@Api("组服务接口")
public class GroupController extends BaseController<Group> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    GroupManager groupManager;
    @Resource
    UserManager userManager;
    @Resource
    OrgRelationManager orgRelationMananger;
    @Resource
    private ICache<Object> iCacheFile;
    @Resource
    private ICurrentContext iCurrentContext;

    /**
     * 组织架构列表(分页条件查询)数据
     *
     * @param request
     * @param response
     * @return
     * @throws Exception PageJson
     * @throws
     */
    @Override
    @RequestMapping("listJson")
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) {
        QueryFilter queryFilter = getQueryFilter(request);
        String parentId = request.getParameter("parentId");
        if (StringUtil.isNotEmpty(parentId)) {
            queryFilter.addFilter("torg.parent_id_", parentId, QueryOP.EQUAL);
        }
        List<Group> orgList = groupManager.query(queryFilter);
        return new PageResult(orgList);
    }


    @RequestMapping("isExist")
    public boolean isExist(HttpServletRequest request, HttpServletResponse response) {
        String oldCode = RequestUtil.getString(request, "oldCode");
        String code = RequestUtil.getString(request, "key");
        if (oldCode.equals(code) && StringUtil.isNotEmpty(code)) {
            return false;
        }
        if (StringUtil.isNotEmpty(code)) {
            Group temp = groupManager.getByCode(code);
            return temp != null;
        }
        return false;
    }

    /**
     * 组织架构明细页面
     *
     * @return
     * @throws Exception ModelAndView
     */
    @RequestMapping("get")
    @Override
    @CatchErr
    @OperateLog
    public ResultMsg<Group> get(@RequestParam String id) {
        Group group = groupManager.get(id);
        if (group != null && !"0".equals(group.getParentId())) {
            String parentOrgName = groupManager.get(group.getParentId()).getName();
            group.setParentName(parentOrgName);
        }

        return getSuccessResult(group);
    }

    @RequestMapping("getTreeData")
    public List<OrgTree> getTreeData(HttpServletRequest request, HttpServletResponse response) {
        List<OrgTree> groupTreeList = getGroupTree();
        if (CollectionUtil.isEmpty(groupTreeList)) {
            groupTreeList = new ArrayList<>();
        }
        if (CollectionUtil.isEmpty(groupTreeList)) {
            OrgTree rootGroup = new OrgTree();
            rootGroup.setName("组织");
            rootGroup.setId("0");
            groupTreeList.add(rootGroup);
        }
        return groupTreeList;
    }

    private List<OrgTree> getGroupTree() {
        List<OrgTree> groupTreeList = new ArrayList<>();
        List<Group> groupList = groupManager.getAll();
        for (Group group : groupList) {
            OrgTree groupTree = new OrgTree(group);
            groupTreeList.add(groupTree);
        }
        return groupTreeList;
    }

    @RequestMapping("getOrgTree")
    @OperateLog
    public ResultMsg<List<OrgTree>> getOrgTree() {
        List<OrgTree> groupTreeList = getGroupTree();
        if (CollectionUtil.isEmpty(groupTreeList)) {
            groupTreeList = new ArrayList<>();
        }

        if (CollectionUtil.isEmpty(groupTreeList)) {
            OrgTree rootGroup = new OrgTree();
            rootGroup.setName("组织");
            rootGroup.setId("0");
            groupTreeList.add(rootGroup);
        }

        return getSuccessResult(BeanUtils.listToTree(groupTreeList));
    }

    @Override
    protected String getModelDesc() {
        return "组织";
    }

    /**
     * 排序
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "changeOrder", method = RequestMethod.POST)
    public ResultMsg<String> changeOrder(@RequestBody List<Group> param) {
        groupManager.chageOrder(param);
        return getSuccessResult();
    }

    /**
     * 选人组件：查询当前用户公司下所有机构
     *
     * @param
     * @return ResultMsg
     * @author guolihao
     * @date 2020/9/16 14:49
     */
    @GetMapping("queryAllGroup")
    @ApiOperation(value = "查询当前用户公司下所有机构", notes = "选人组件：查询当前用户公司下所有机构")
    public ResultMsg<List<OrgTree>> queryAllGroup() {
        return getSuccessResult(BeanUtils.listToTree(groupManager.queryAllGroup().stream().
                map(OrgTree::new).collect(Collectors.toList())));
    }


    /**
     * 保存
     */
    @Override
    @RequestMapping("save")
    @CatchErr
    @OperateLog
    public ResultMsg<String> save(@RequestBody Group t) {
        String desc;
        if (StringUtil.isEmpty(t.getId())) {
            desc = "添加%s成功";
            groupManager.create(t);
        } else {
            groupManager.update(t);
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
        groupManager.removeByIds(aryIds);
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
    public ResultMsg<String> groupImport(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
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
                        String[] cellName = {"编号", "上级机构", "机构名称", "组织类型", "机构编码", "备注"};
                        final Map<String, String> mapOrg = new HashMap<>();
                        int lastRowNum = sheet.getLastRowNum();
                        for (int i = 2; i <= lastRowNum; i++) {
                            Row data = sheet.getRow(i);
                            if (null == data) {
                                break;
                            }
                            StringBuilder errorMsg = new StringBuilder();
                            try {
                                String parentOrgPath = getCellStringData(data.getCell(1));
                                String name = getCellStringData(data.getCell(2));
                                String code = getCellStringData(data.getCell(4));
                                if (StringUtils.isEmpty(parentOrgPath) && StringUtils.isEmpty(name) && StringUtils.isEmpty(code)) {
                                    break;
                                }
                                totalNum++;
                                if (StringUtils.isEmpty(parentOrgPath)) {
                                    errorMsg.append("上级机构不能为空").append("\n ");
                                }
                                if (StringUtils.isEmpty(name)) {
                                    errorMsg.append("机构名称不能为空").append("\n ");
                                }
                                String orgType = getCellStringData(data.getCell(3));
                                if (StringUtils.isEmpty(code)) {
                                    errorMsg.append("机构编号不能为空").append("\n ");
                                }
                                String desc = getCellStringData(data.getCell(5));
                                String parentOrgId;
                                if ("0".equals(parentOrgPath)) {
                                    parentOrgId = "0";
                                } else {
                                    parentOrgId = groupManager.findOrgId(mapOrg, parentOrgPath);
                                }
                                Group group = new Group();
                                Group oldGroup = groupManager.getByCode(code);
                                group.setName(name);
                                group.setParentId(parentOrgId);
                                group.setCode(code);
                                GroupGradeConstant groupGradeConstant = GroupGradeConstant.getByLabel(orgType);
                                if (null == groupGradeConstant) {
                                    errorMsg.append("组织类型：").append(orgType).append("不存在").append("\n ");
                                }
                                group.setType(groupGradeConstant.key());
                                group.setDesc(desc);
                                if (errorMsg.length() == 0) {
                                    if (null != oldGroup) {
                                        group.setId(oldGroup.getId());
                                        groupManager.update(group);
                                    } else {
                                        groupManager.create(group);
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

    /**
     * 重置组织path
     *
     * @param
     * @return ResultMsg
     * @author 谢石
     * @date 2020-12-17
     */
    @GetMapping("resetPath")
    @ApiOperation(value = "重置组织")
    public ResultMsg<String> resetPath() {
        List<Group> lstGroup = groupManager.getAll();
        Map<String, String> temp = new HashMap<>();
        lstGroup.forEach(group -> temp.put(group.getId(), group.getParentId()));
        lstGroup.forEach(group -> {
            StringBuilder path = new StringBuilder(group.getId());
            String parentId = group.getParentId();
            Set<String> ids = new HashSet<>();
            ids.add(group.getId());
            int num = 1;
            while (num <= 100 && StringUtils.isNotEmpty(parentId) && !"0".equals(parentId)) {
                if (ids.contains(parentId)) {
                    logger.error("id为" + group.getId() + "的组织存在循环父节点的情况");
                } else {
                    ids.add(parentId);
                    path.insert(0, parentId + ".");
                    parentId = temp.get(parentId);
                }
            }
            if ("0".equals(parentId)) {
                Group groupTemp = new Group();
                groupTemp.setId(group.getId());
                groupTemp.setPath(path.toString());
                if (!path.toString().equals(group.getPath())) {
                    logger.info("id为" + group.getId() + "的组织path异常 被重置");
                    groupManager.updateByPrimaryKeySelective(groupTemp);
                }
            } else {
                logger.error("id为" + group.getId() + "的组织ptah断裂" + path.toString());
            }
        });
        return getSuccessResult("重置成功");
    }
}
