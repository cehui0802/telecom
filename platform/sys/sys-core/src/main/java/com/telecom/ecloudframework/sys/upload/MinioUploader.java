package com.telecom.ecloudframework.sys.upload;

import com.telecom.ecloudframework.base.api.exception.BusinessException;
import com.telecom.ecloudframework.sys.core.model.SysFile;
import com.telecom.ecloudframework.sys.oss.MinioOss;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinioUploader extends AbstractUploader  {
    protected Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MinioOss minioOss = null ;

    @Override
    public String type() {
        return "minio";
    }

    @Override
    public String upload(InputStream is, String name) {
        try {
            return minioOss.putObject(name ,is);
        } catch (Exception e) {
            LOG.error("上传失败",e);
            throw new BusinessException(e);
        }
    }

    @Override
    public InputStream take(String name) {
        try {
            return minioOss.getObject(name) ;
        } catch (Exception e) {
            LOG.error("下载失败",e);
            throw new BusinessException(e);
        }
    }

    @Override
    public void remove(String name) {
        try {
            minioOss.removeObject(name);
        } catch (Exception e) {
            LOG.error("删除失败",e);
            throw new BusinessException(e);
        }
    }

    @Override
    public String uploadUrl(SysFile sysFile) {
        try {
            String fileName = sysFile.getName();
            String ext = fileName.substring(fileName.lastIndexOf('.'));
            String filePath = minioOss.getPath().concat(sysFile.getId().concat(ext)) ;
            sysFile.setPath(filePath);
            return minioOss.presignedPutObject(filePath) ;
        } catch (Exception e) {
            LOG.error("获取上传链接失败",e);
            throw new BusinessException(e);
        }
    }

    @Override
    public String downloadUrl(SysFile sysFile) {
        try {
            return minioOss.presignedGetObject(sysFile.getPath()) ;
        } catch (Exception e) {
            LOG.error("获取下载链接失败",e);
            throw new BusinessException(e);
        }
    }

    @Override
    public String modify(InputStream is, String name, String dbFilePath) {
        return upload(is,name);
    }
}
