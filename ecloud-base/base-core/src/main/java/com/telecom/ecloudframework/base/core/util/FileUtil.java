package com.telecom.ecloudframework.base.core.util;

import cn.hutool.core.io.IoUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {
    public static void deleteFiles(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            String[] files = file.list();
            if (files.length > 0) {
                for (String childName : files) {
                    deleteFiles(filePath + "/" + childName);
                }
            }
        }
        file.delete();
    }

    private static final int BUFFER_SIZE = 2 * 1024;

    public static void toZipDir(String srcDir, String outPath, boolean KeepDirStructure) throws Exception {
        OutputStream out = new FileOutputStream(outPath);
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            out.close();
        }
    }

    public static void toZipFiles(File[] listFiles, OutputStream out, boolean KeepDirStructure) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(out);
        if (listFiles == null || listFiles.length == 0) {
            if (KeepDirStructure) {
                zos.putNextEntry(new ZipEntry("/"));
                zos.closeEntry();
            }
        } else {
            for (File file : listFiles) {
                if (KeepDirStructure) {
                    compress(file, zos, "/" + file.getName(), KeepDirStructure);
                } else {
                    compress(file, zos, file.getName(), KeepDirStructure);
                }
            }
        }
    }

    public static void unZipFiles(File zipFile, String descDir) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile, Charset.forName("UTF-8"));
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + "/" + zipEntryName).replaceAll("\\*", "/");
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (new File(outPath).isDirectory()) {
                continue;
            }
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            zos.putNextEntry(new ZipEntry(name));
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (KeepDirStructure) {
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (KeepDirStructure) {
                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), KeepDirStructure);
                    }
                }
            }
        }
    }

    public static File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 复制文件夹
     *
     * @param resource 源路径
     * @param target   目标路径
     */
    public static void copyFolder(String resource, String target) throws Exception {
        File resourceFile = new File(resource);
        if (!resourceFile.exists()) {
            throw new Exception("源目标路径：[" + resource + "] 不存在...");
        }
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File[] resourceFiles = resourceFile.listFiles();
        for (File file : resourceFiles) {
            File file1 = new File(targetFile.getAbsolutePath() + File.separator + file.getName());
            if (file.isFile()) {
                InputStream io = new FileInputStream(file);
                OutputStream out = new FileOutputStream(file1);
                IoUtil.copy(io, out);
                out.flush();
                io.close();
                out.close();
            }
            if (file.isDirectory()) {
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                copyFolder(file.getAbsolutePath(), file1.getAbsolutePath());
            }
        }
    }

    /**
     * 覆盖文件夹中的同名文件
     *
     * @param lastResource 源路径新文件
     * @param oldTarget    目标路径旧文件
     */
    public static void moveTargetFolderFromNewResource(String oldTarget, String lastResource,String mergeDir) throws Exception {
        File lastResourceFile = new File(lastResource);
        File targetFile = new File(oldTarget);
        if (!targetFile.exists()) {
            throw new Exception("目标路径：[" + oldTarget + "] 不存在...");
        }
        if (!lastResourceFile.exists()) {
            return;
        }
        File mergeFile = new File(mergeDir);
        if (!mergeFile.exists()){
            mergeFile.mkdirs();
        }
        File[] targetFiles = targetFile.listFiles();
        for (File file : targetFiles) {
            File newFile = new File(lastResourceFile.getAbsolutePath() + File.separator + file.getName());
            File merge = new File(mergeFile.getAbsoluteFile() + File.separator + file.getName());
            if (file.isFile()) {
                InputStream io = new FileInputStream(newFile);
                OutputStream out = new FileOutputStream(merge);
                IoUtil.copy(io, out);
                out.flush();
                io.close();
                out.close();
                newFile.delete();
            }
            if (file.isDirectory()) {
                moveTargetFolderFromNewResource(file.getAbsolutePath(), newFile.getAbsolutePath() , merge.getAbsolutePath());
                if (newFile.list().length == 0) {
                    newFile.delete();
                }
            }
        }
    }
}
