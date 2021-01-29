package com.telecom.ecloudframework.sys.rest.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.telecom.ecloudframework.base.api.query.QueryOP;
import com.telecom.ecloudframework.base.rest.util.HttpClientUtil;
import com.telecom.ecloudframework.sys.service.SysSomeService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.telecom.ecloudframework.base.api.aop.annotion.CatchErr;
import com.telecom.ecloudframework.base.api.exception.BusinessMessage;
import com.telecom.ecloudframework.base.api.query.QueryFilter;
import com.telecom.ecloudframework.base.api.response.impl.ResultMsg;
import com.telecom.ecloudframework.base.core.util.ZipUtil;
import com.telecom.ecloudframework.base.db.model.page.PageResult;
import com.telecom.ecloudframework.base.rest.ControllerTools;
import com.telecom.ecloudframework.sys.core.manager.SysFileManager;
import com.telecom.ecloudframework.sys.core.model.SysFile;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import net.lingala.zip4j.core.ZipFile;

/**
 * <pre>
 * 描述：上传附件的controller
 * 作者:aschs
 * 邮箱:aschs@qq.com
 * 日期:2018年6月4日
 * 版权:summer
 * </pre>
 */
@RestController
@RequestMapping("/sys/sysFile/")
public class SysFileController extends ControllerTools {
    @Autowired
    SysFileManager sysFileManager;
    @Autowired
    private SysSomeService sysSomeService;

    /**
     * 相关文件
     */
    @RequestMapping("listFiles")
    public PageResult listFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = getQueryFilter(request);
        //不显示自动生成的OFD文件
        queryFilter.addFilter("remark_", "list-files", QueryOP.EQUAL);
        List<SysFile> query = sysFileManager.query(queryFilter);
        return new PageResult(query);
    }


    /**
     * 相关文件
     */
    @RequestMapping("aboutFiles")
    public PageResult aboutFiles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = getQueryFilter(request);
        //不显示自动生成的OFD文件
        String sql = "(remark_ != 'generate-ofd' or remark_ is null)";
        queryFilter.addParamsFilter("defaultWhere", sql);
        List<SysFile> query = sysFileManager.query(queryFilter);
        return new PageResult(query);
    }

    /**
     * OFD上传+转换接口
     */
    @RequestMapping(value = "ofd", method = RequestMethod.POST)
    @CatchErr(value = "上传失败")
    public ResultMsg<Map<String, String>> ofd(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "uploader", required = false) String uploader,
                                              @RequestParam(value = "instId", required = false) String instId,
                                              @RequestParam(value = "remark", required = false) String remark,
                                              @RequestParam(value = "uploadUrl", required = false) String uploadUrl,
                                              @RequestParam(value = "watermark", required = false) String watermark,
                                              @RequestParam(value = "autoConvert", required = false) String autoConvert) throws Exception {

        ResultMsg<Map<String, String>> resultMsg = new ResultMsg<Map<String, String>>();
        resultMsg.setOk(true);
        resultMsg.setMsg("上传成功");
        resultMsg.setData(sysSomeService.uploadOfd(file, uploader, instId, remark, uploadUrl, watermark, autoConvert));
        return resultMsg;

    }

    /**
     * OFD上传+转换接口
     */
    @RequestMapping(value = "modifyOfd", method = RequestMethod.POST)
    @CatchErr(value = "更新失败")
    public ResultMsg<Map<String, String>> modifyOfd(@RequestParam("file") MultipartFile file,
                                                    @RequestParam(value = "ofdId", required = false) String ofdId,
                                                    @RequestParam(value = "fileId", required = false) String fileId,
                                                    @RequestParam(value = "uploader", required = false) String uploader,
                                                    @RequestParam(value = "instId", required = false) String instId,
                                                    @RequestParam(value = "remark", required = false) String remark,
                                                    @RequestParam(value = "uploadUrl", required = false) String uploadUrl,
                                                    @RequestParam(value = "watermark", required = false) String watermark,
                                                    @RequestParam(value = "autoConvert", required = false) String autoConvert) throws Exception {

        ResultMsg<Map<String, String>> resultMsg = new ResultMsg<Map<String, String>>();
        resultMsg.setOk(true);
        resultMsg.setMsg("更新成功");
        resultMsg.setData(sysSomeService.uploadOfd(file,ofdId,fileId, uploader, instId, remark, uploadUrl, watermark, autoConvert));
        return resultMsg;

    }

    /**
     * <pre>
     * </pre>
     *
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @CatchErr(value = "上传失败")
    public ResultMsg<String> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "uploader", required = false) String uploader,
                                    @RequestParam(value = "instId", required = false) String instId,
                                    @RequestParam(value = "remark", required = false) String remark) throws Exception {
        SysFile sysFile = new SysFile();
        sysFile.setUploader(uploader);
        sysFile.setRemark(remark);
        sysFile.setInstId(instId);
        sysFile.setName(file.getOriginalFilename());
        sysFileManager.upload(file.getInputStream(), sysFile);
        return getSuccessResult(sysFile.getId(), "上传成功");
    }

    @RequestMapping(value = "uploadByUrl", method = RequestMethod.POST)
    @CatchErr(value = "上传失败")
    public ResultMsg<String> uploadByUrl(@RequestParam String url,
                                         @RequestParam String fileName,
                                         @RequestParam(value = "uploader", required = false) String uploader,
                                         @RequestParam(value = "instId", required = false) String instId,
                                         @RequestParam(value = "remark", required = false) String remark) throws Exception {
        HttpEntity httpEntity = HttpClientUtil.httpGetFileEntity(url);
        SysFile sysFile = new SysFile();
        sysFile.setUploader(uploader);
        sysFile.setRemark(remark);
        sysFile.setInstId(instId);
        sysFile.setName(fileName);
        sysFileManager.upload(httpEntity.getContent(), sysFile);
        return getSuccessResult(sysFile.getId(), "上传成功");
    }

    @RequestMapping(value = "modify", method = RequestMethod.POST)
    @CatchErr(value = "更新失败")
    public ResultMsg<String> modify(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "fileId") String fileId,
                                    @RequestParam(value = "uploader", required = false) String uploader,
                                    @RequestParam(value = "instId", required = false) String instId,
                                    @RequestParam(value = "remark", required = false) String remark) throws Exception {
        SysFile sysFile = new SysFile();
        sysFile.setUploader(uploader);
        sysFile.setRemark(remark);
        sysFile.setInstId(instId);
        sysFile.setName(file.getOriginalFilename());
        sysFileManager.modify(file.getInputStream(), sysFile, fileId);
        return getSuccessResult(fileId, "更新成功");
    }

    /**
     * <pre>
     * </pre>
     *
     * @param fileId 文件名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "download", method = RequestMethod.GET)
    @CatchErr(value = "下载失败",write2response = true)
    public ResponseEntity<byte[]> download(@RequestParam("fileId") String fileId) throws Exception {
        SysFile sysFile = sysFileManager.get(fileId);

        HttpHeaders headers = new HttpHeaders();
        String downloadFileName = new String(sysFile.getName().getBytes("UTF-8"), "iso-8859-1");
        downloadFileName = downloadFileName
//				.replaceAll("%20", "\\+")
//				.replaceAll("%28", "\\(").replaceAll("%29", "\\)")
//				.replaceAll("%3B", ";").replaceAll("%40", "@")
//				.replaceAll("%23", "\\#").replaceAll("%26", "\\&")
//				.replaceAll("%2C", "\\,").replaceAll("%24", "\\$")
                .replaceAll("\r|\n|\t", "");
        headers.setContentDispositionFormData("attachment", downloadFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(IOUtils.toByteArray(sysFileManager.download(fileId)), headers, HttpStatus.OK);
    }

    /**
     * <pre>
     * </pre>
     *
     * @param fileName
     * @param uploader 文件名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "url/upload", method = RequestMethod.GET)
    @CatchErr(value = "上传URL创建失败")
    public ResultMsg<Map> uploadUrl(@RequestParam("fileName") String fileName,
                                    @RequestParam(value = "uploader", required = false) String uploader,
                                    @RequestParam(value = "remark", required = false) String remark) throws Exception {
        Map result = sysFileManager.uploadUrl(fileName, remark, uploader);
        return getSuccessResult(result, "上传URL创建成功");
    }

    /**
     * <pre>
     * </pre>
     *
     * @param fileId 文件名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "url/download", method = RequestMethod.GET)
    @CatchErr(value = "下载URL创建失败")
    public ResultMsg<String> downloadUrl(@RequestParam("fileId") String fileId) throws Exception {
        return getSuccessResult(sysFileManager.downloadUrl(fileId), "下载URL创建成功");
    }

    @RequestMapping(value = "zip", method = RequestMethod.GET)
    @CatchErr(value = "打包失败",write2response = true)
    public ResponseEntity<byte[]> zip(@RequestParam("fileIds") String fileIds) throws Exception {
        ArrayList<File> sourceFileList = new ArrayList<>();
        for (String id : fileIds.split(",")) {
            SysFile sysFile = sysFileManager.get(id);
            sourceFileList.add(inputstream2file(sysFileManager.download(id), new File(sysFile.getName())));
        }

        String zipName = DateUtil.format(new DateTime(), "yyyyMMddHHmmss") + ".zip";
        File file = ZipUtil.zip(sourceFileList, new ZipFile(new File(zipName)));
        HttpHeaders headers = new HttpHeaders();
        String downloadFileName = new String(zipName.getBytes("UTF-8"), "iso-8859-1");
        headers.setContentDispositionFormData("attachment", downloadFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] bs = FileUtils.readFileToByteArray(file);
        file.delete();// 删除临时zip文件
        return new ResponseEntity<>(bs, headers, HttpStatus.CREATED);
    }

    private static File inputstream2file(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @RequestMapping(value = "del")
    @CatchErr(value = "删除失败")
    public ResultMsg<String> del(@RequestParam("fileId") String fileId) throws Exception {
        sysFileManager.delete(fileId);
        return getSuccessResult("删除成功");
    }

    @RequestMapping("listJson")
    public PageResult listJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        QueryFilter queryFilter = getQueryFilter(request);
        List<SysFile> pageList = sysFileManager.query(queryFilter);
        return new PageResult(pageList);
    }
}
