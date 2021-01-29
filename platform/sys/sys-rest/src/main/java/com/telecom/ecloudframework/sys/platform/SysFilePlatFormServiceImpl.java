package com.telecom.ecloudframework.sys.platform;

import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.base.db.model.query.DefaultQueryFilter;
import com.telecom.ecloudframework.base.rest.util.HttpClientUtil;
import com.telecom.ecloudframework.sys.api.model.ISysFile;
import com.telecom.ecloudframework.sys.api.platform.ISysFilePlatFormService;
import com.telecom.ecloudframework.sys.service.SysSomeService;
import com.telecom.ecloudframework.sys.core.manager.SysFileManager;
import com.telecom.ecloudframework.sys.core.model.SysFile;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SysFilePlatFormServiceImpl implements ISysFilePlatFormService {
    @Resource
    private SysSomeService sysSomeService;
    @Resource
    private SysFileManager sysFileManager;

    @Override
    public Map<String, String> uploaderOfd(MultipartFile file, String instId, String uploader, String remark, String uploadUrl, String watermark, String autoConvert) throws Exception {
        return sysSomeService.uploadOfd(file, instId, uploader, remark, uploadUrl, watermark, autoConvert);
    }

    @Override
    public Map<String, String> uploaderOfd(MultipartFile file, String instId, String uploader, String remark, String uploadUrl, String watermark, String autoConvert, String userId) throws Exception {
        return sysSomeService.uploadOfd(file, instId, uploader, remark, uploadUrl, watermark, autoConvert);
    }

    @Override
    public SysFile upload(MultipartFile file, String uploader, String instId, String remark) {
        SysFile sysFile = new SysFile();
        sysFile.setUploader(uploader);
        sysFile.setRemark(remark);
        sysFile.setInstId(instId);
        sysFile.setName(file.getOriginalFilename());
        try {
            sysFileManager.upload(file.getInputStream(), sysFile);
        } catch (IOException e) {
            throw new BusinessException("上传失败", e);
        }
        return sysFile;
    }

    @Override
    public ISysFile upload(String url, String fileName, String uploader, String instId, String remark, String userId) {
        HttpEntity httpEntity = null;
        try {
            httpEntity = HttpClientUtil.httpGetFileEntity(url);
        }catch (Exception e){
            throw new BusinessException("上传失败", e);
        }
        SysFile sysFile = new SysFile();
        sysFile.setUploader(uploader);
        sysFile.setRemark(remark);
        sysFile.setInstId(instId);
        sysFile.setName(fileName);
        try {
            sysFileManager.upload(httpEntity.getContent(), sysFile);
        } catch (IOException e) {
            throw new BusinessException("上传失败", e);
        }
        return sysFile;
    }

    @Override
    public ISysFile upload(MultipartFile file) {
        return upload(file, null, null, null);
    }

    @Override
    public ISysFile upload(MultipartFile file, String uploader, String instId, String remark, String userId) {
        return upload(file, uploader, instId, remark);
    }

    @Override
    public ResponseEntity<byte[]> download(String fileId) {
        SysFile sysFile = sysFileManager.get(fileId);
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity responseEntity = null;
        try {
            String downloadFileName = new String(sysFile.getName().getBytes("UTF-8"), "iso-8859-1");
            downloadFileName = downloadFileName.replaceAll("\r|\n|\t", "");
            headers.setContentDispositionFormData("attachment", downloadFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseEntity = new ResponseEntity<>(IOUtils.toByteArray(sysFileManager.download(fileId)), headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new BusinessException("下载失败", e);
        }
        return responseEntity;
    }

    @Override
    public List<SysFile> listJson(String instId, String remark, String ids) {
        QueryFilter queryFilter = new DefaultQueryFilter(true);
        if (StringUtil.isEmpty(instId)
                && StringUtil.isEmpty(ids)) {
            throw new BusinessException("instId ids 参数不能为空");
        }
        if (StringUtil.isNotEmpty(instId)) {
            queryFilter.addFilter("inst_id_", instId, QueryOP.EQUAL);
        } else if (StringUtil.isNotEmpty(remark)) {
            queryFilter.addFilter("remark_", remark, QueryOP.EQUAL);
        } else if (StringUtil.isNotEmpty(ids)) {
            queryFilter.addFilter("id_", ids, QueryOP.IN);
        }
        return sysFileManager.query(queryFilter);
    }

    @Override
    public String delete(String ids) {
        if (StringUtil.isNotEmpty(ids)) {
            Arrays.asList(ids.split(",")).forEach(id -> sysFileManager.delete(id));
        }
        return "删除成功";
    }
}
