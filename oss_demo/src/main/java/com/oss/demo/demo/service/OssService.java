package com.oss.demo.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 业务层
 * @Author asus
 * @create 16/7/2020 下午3:29
 */
public interface OssService {

    /**
     *
     *
     * @description: 将Controller曾传来的文件调用OSSUnit方法上传到OSS
     * @param {MultipartFile} file
     * @return: {String}
     * @author: YuHangChen
     * @time: 16/7/2020 下午4:06
     */
    public String uploadFile(MultipartFile file) throws IOException;

    /**
     *
     *
     * @description: 调用OSSUnit方法下载OSS上的文件
     * @param {String} fileName
     * @return: {String}
     * @author: YuHangChen
     * @time: 16/7/2020 下午4:06
     */
    public String downloadFile(String fileName) throws IOException;

    /**
     *
     *
     * @description: 将Controller曾传来的大文件调用OSSUnit方法上传到OSS
     * @param {MultipartFile} file
     * @return: {String}
     * @author: YuHangChen
     * @time: 16/7/2020 下午4:06
     */
    public String uploadBigFile(MultipartFile file) throws IOException;
}
