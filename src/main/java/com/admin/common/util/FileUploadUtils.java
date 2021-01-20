package com.admin.common.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtils {
    /**
     * 默认大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int FILE_NAME_MAX = 100;

    /**
     * 默认上传的地址
     */
    private static String DEFAULT_BASE_FILE = "D:\\image\\upload";

    /**
     * 按照默认的配置上传文件
     */
    public static final String upload(MultipartFile file) throws IOException {
        try {
            return upload(FileUploadUtils.DEFAULT_BASE_FILE, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 根据文件路径上传
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 文件上传
     */
    public static final String upload(String baseDir, MultipartFile file, String[] allowedExtension)
            throws Exception {
        //合法性校验
        assertAllowed(file, allowedExtension);

        String fileName = encodingFileName(file);

        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc);
        return desc.getAbsolutePath();
    }

    private static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

    /**
     * 对文件名特殊处理一下
     */
    private static String encodingFileName(MultipartFile file) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String datePath = simpleDateFormat.format(new Date());
        return datePath + "-" + UUID.randomUUID().toString() + "." + getExtension(file);
    }

    /**
     * 文件合法性校验
     */
    public static final void assertAllowed(MultipartFile file, String[] allowedExtension) throws Exception {
        if (file.getOriginalFilename() != null) {
            int fileNamelength = file.getOriginalFilename().length();
            if (fileNamelength > FILE_NAME_MAX) {
                throw new Exception("文件名过长");
            }
        }

        long size = file.getSize();
        if (size > DEFAULT_MAX_SIZE) {
            throw new Exception("文件过大");
        }

        String extension = getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {
            throw new Exception("请上传指定类型的文件！");
        }

    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件名的后缀
     */
    public static final String getExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = null;
        if (fileName == null) {
            return null;
        } else {
            int index = indexOfExtension(fileName);
            extension = index == -1 ? "" : fileName.substring(index + 1);
        }

        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }

    public void setDEFAULT_BASE_FILE(String DEFAULT_BASE_FILE) {
        FileUploadUtils.DEFAULT_BASE_FILE = DEFAULT_BASE_FILE;
    }

    public String getDEFAULT_BASE_FILE() {
        return DEFAULT_BASE_FILE;
    }
}
