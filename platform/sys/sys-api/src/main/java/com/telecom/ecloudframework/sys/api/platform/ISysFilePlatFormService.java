package com.telecom.ecloudframework.sys.api.platform;

import com.telecom.ecloudframework.sys.api.model.ISysFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ISysFilePlatFormService {
    /**
     * 上传文件并转换为ofd
     * @param file 文件
     * @param instId 流程实例id
     * @param uploader 上传方式
     * @param remark 件插件的上传文件标识
     * @param uploadUrl 转换地址
     * @param watermark 水印
     * @param autoConvert 是否转换 no-不需要转换
     * @return
     * @throws Exception
     */
    Map<String, String> uploaderOfd(MultipartFile file,
                                    String instId,
                                    String uploader,
                                    String remark,
                                    String uploadUrl,
                                    String watermark,
                                    String autoConvert) throws Exception;

    Map<String, String> uploaderOfd(MultipartFile file,
                                    String instId,
                                    String uploader,
                                    String remark,
                                    String uploadUrl,
                                    String watermark,
                                    String autoConvert,
                                    String userId) throws Exception;
    /**
     * 文件上传
     * @param file 文件
     * @param uploader 文件保存方式，默认配置，db-数据库，minio-minio对象存储，ordinary-保存到服务器，oss-OSS存储
     * @param instId 流程实例id，
     * @param remark 件插件的上传文件标识
     * @return
     */
    ISysFile upload(MultipartFile file, String uploader, String instId, String remark);

    ISysFile upload(MultipartFile file, String uploader, String instId, String remark, String userId);

    ISysFile upload(String url,String fileName, String uploader, String instId, String remark, String userId);
    /**
     * 文件上传
     * @param file 文件
     * @return
     */
    ISysFile upload(MultipartFile file);
    /**
     * 下载文件
     * @param fileId 文件id
     * @return
     */
    ResponseEntity<byte[]> download(String fileId);

    /**
     * 查询文件
     * @param instId 流程实例id
     * @param remark 文件插件的上传文件标识
     * @param ids 文件id，多个用逗号拼接
     * @return
     */
    List<? extends ISysFile> listJson(String instId, String remark, String ids);

    /**
     * 删除文件
     * @param ids 文件id
     * @return
     */
    String delete(String ids);
}
