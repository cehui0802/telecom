package com.telecom.ecloudframework.sys.oss;

import cn.hutool.core.date.DateUtil;

import java.io.InputStream;
import java.util.Date;

public abstract class AbstractOss {

    protected String bucketName = null ;
    protected String path = null ;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setPath(String path) {
        //OSS只认识“/”符号 无法识别File.separator
        this.path = (path + DateUtil.format(new Date(), "yyyyMMdd") + "/");
    }

    public String getPath() {
        return path;
    }

    /**
     * 上传文件
     * @param filename 文件名
     * @param stream 输入流
     */
    public abstract String putObject(String filename, InputStream stream) ;

    /**
     * 获取文件流
     * @param objectName 对象名称，全路径
     * @return 文件流
     */
    public abstract InputStream getObject(String objectName);

    /**
     * 删除文件
     * @param objectName
     */
    public abstract void removeObject(String objectName) ;

    /**
     * 返回一个指定路径的url以便前端在指定有效期的时间内上传
     * @param objectName
     * @return
     */
    public abstract String presignedPutObject(String objectName);

    /**
     * 返回下载地址用于前端下载
     * @param objectName 对象名称，全路径
     * @return
     */
    public abstract String presignedGetObject(String objectName);

}
