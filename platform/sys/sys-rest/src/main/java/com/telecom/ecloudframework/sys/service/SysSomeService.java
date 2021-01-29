package com.telecom.ecloudframework.sys.service;

import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.StringUtil;
import com.telecom.ecloudframework.sys.rest.ofd.custom.agent.HTTPAgent;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.Const;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.MarkPosition;
import com.telecom.ecloudframework.sys.rest.ofd.custom.wrapper.model.TextInfo;
import com.telecom.ecloudframework.sys.core.manager.SysFileManager;
import com.telecom.ecloudframework.sys.core.model.SysFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class SysSomeService {
    @Autowired
    SysFileManager sysFileManager;

    private final String DEFAULT_UPLOAD_URL = "http://39.98.67.158:8080/convert-issuer/";

    public Map uploadOfd(MultipartFile file,
                         String uploader,
                         String instId,
                         String remark,
                         String uploadUrl,
                         String watermark,
                         String autoConvert) throws Exception {
        return uploadOfd(file, null, null, uploader, instId, remark, uploadUrl, watermark, autoConvert);
    }

    public Map uploadOfd(MultipartFile file,
                         String ofdId,
                         String fileId,
                         String uploader,
                         String instId,
                         String remark,
                         String uploadUrl,
                         String watermark,
                         String autoConvert) throws Exception {
        Map<String, String> files = new HashMap<String, String>();

        // 是否需要转换OFD
        if (!"no".equals(StringUtils.trimToEmpty(autoConvert))) {

            // 转换OFD
            if ("".equals(StringUtils.trimToEmpty(uploadUrl))) {
                uploadUrl = DEFAULT_UPLOAD_URL;
            }

            HTTPAgent ha = new HTTPAgent(uploadUrl);
            ByteArrayOutputStream out = null;
            ByteArrayInputStream swapStream = null;
            try {
                InputStream inputStream = file.getInputStream();
                out = new ByteArrayOutputStream();

                // 水印
                watermark = StringUtils.trimToEmpty(watermark);

                if ("".equals(watermark)) {

                    // 转换
                    ha.officeToOFD(inputStream, "OFD-" + file.getOriginalFilename(), out,
                            new HashMap<Const.Perm, Object>());
                } else {

                    // 所有页加水印
                    MarkPosition mp = new MarkPosition(10, 20, 200, 300, MarkPosition.INDEX_ALL);

                    // 旋转角度是45的倍数。
                    TextInfo textinfo = new TextInfo(watermark, "宋体", 18, "#cccccc", 45, Const.XAlign.Center,
                            Const.YAlign.Middle);

                    ha.addTextMark(inputStream, "OFD-" + file.getOriginalFilename(), out, textinfo, mp, true, true);

                }

                // 水印
                // ha.addPermission(new File("D:\\convert\\ori\\docx_1.docx"), new
                // FileOutputStream("D:\\convert\\tar\\addPermission.ofd"),map );
                // 写入
                swapStream = new ByteArrayInputStream(out.toByteArray());

                // 这里名称后缀转换成OFD 后上传
                String originalFilename = file.getOriginalFilename();
                originalFilename = "OFD-" + originalFilename.substring(0, originalFilename.lastIndexOf(".")) + ".ofd";

                SysFile sysFile = new SysFile();
                sysFile.setUploader(uploader);
                sysFile.setRemark("generate-ofd");
                sysFile.setInstId(instId);
                sysFile.setName(originalFilename);
                if (StringUtil.isNotEmpty(ofdId)) {
                    // 传入 ofdId 时 代表编辑操作
                    sysFileManager.modify(swapStream, sysFile, ofdId);
                    files.put("ofd", ofdId);
                } else {
                    sysFileManager.upload(swapStream, sysFile);
                    files.put("ofd", sysFile.getId());
                }
                files.put("ofdName", originalFilename);
            } catch (Exception e) {
                // throw e;
                throw new BusinessMessage("转换OFD错误" + e.getMessage());
            } finally {
                try {
                    // 注意：一定要记得关闭ha
                    ha.close();
                    ha = null;

                    if (out != null) {
                        out.close();
                        out = null;
                    }

                    if (swapStream != null) {
                        swapStream.close();
                        swapStream = null;
                    }
                } catch (IOException e) {
                    // throw e;
                    throw new BusinessMessage("转换OFD关闭资源错误");
                }
            }

        }

        // 原附件上传
        SysFile sysFile = new SysFile();
        sysFile.setUploader(uploader);
        sysFile.setRemark(remark);
        sysFile.setInstId(instId);
        sysFile.setName(file.getOriginalFilename());

        if (StringUtil.isNotEmpty(fileId)) {
            // 传入文件id时代表编辑操作
            sysFileManager.modify(file.getInputStream(), sysFile, fileId);
            files.put("file", fileId);
        } else {
            sysFileManager.upload(file.getInputStream(), sysFile);
            files.put("file", sysFile.getId());
        }

        return files;
    }
}
