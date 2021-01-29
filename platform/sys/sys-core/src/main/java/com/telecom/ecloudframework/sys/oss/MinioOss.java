package com.telecom.ecloudframework.sys.oss;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

public class MinioOss extends AbstractOss {

    @Autowired
    private MinioClient minioClient = null ;

    @Override
    public String putObject(String fileName, InputStream stream) {
        try {
            String objectName = path.concat(fileName) ;
            minioClient.putObject(bucketName,objectName,stream,"application/octet-stream");
            return objectName ;
        } catch (Exception e) {
            throw new RuntimeException("minio上传失败", e) ;
        }
    }

    @Override
    public InputStream getObject(String objectName) {
        try{
            return minioClient.getObject(bucketName,objectName) ;
        } catch (Exception e) {
            throw new RuntimeException("下载失败", e) ;
        }
    }

    @Override
    public void removeObject(String objectName) {
        try{
            minioClient.removeObject(bucketName,objectName);
        } catch (Exception e) {
            throw new RuntimeException("删除失败", e) ;
        }

    }

    @Override
    public String presignedPutObject(String objectName) {
        try{
            return minioClient.presignedPutObject(bucketName,objectName,60 * 60 * 24) ;
        } catch (Exception e) {
            throw new RuntimeException("获取上传地址失败", e) ;
        }
    }

    @Override
    public String presignedGetObject(String objectName) {
        try{
            return minioClient.presignedGetObject(bucketName,objectName,60 * 60 * 24);
        } catch (Exception e) {
            throw new RuntimeException("获取下载地址失败", e) ;
        }
    }


}
