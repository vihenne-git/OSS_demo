package com.oss.demo.demo.controller;

import com.oss.demo.demo.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


/**
 * @Author asus
 * @create 16/7/2020 下午2:27
 */
@Slf4j
@RestController
public class OssController {

    @Autowired
    private OssService ossService;

    /**
     *
     *
     * @description: 上传普通文件到OSS
     * @param {MultipartFile} file
     * @return: {String}
     * @author: YuHangChen
     * @time: 16/7/2020 下午4:05
     */    
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        long start = System.currentTimeMillis();
        ossService.uploadFile(file);
        long end = System.currentTimeMillis();
        log.info("普通上传："+String.valueOf(end-start));
        return "上传文件成功";
    }

    /**
     *
     *
     * @description: 上传普通文件到OSS
     * @param {MultipartFile} file
     * @return: {String}
     * @author: YuHangChen
     * @time: 16/7/2020 下午4:05
     */
    @PostMapping("/uploadBigFile")
    public String uploadBigFile(@RequestParam("file") MultipartFile file) throws IOException {
        long start = System.currentTimeMillis();
        ossService.uploadBigFile(file);
        long end = System.currentTimeMillis();
        log.info("多线程上传："+String.valueOf(end-start));
        return "上传大文件成功";
    }

    /**
     *
     *
     * @description: 从OSS下载指定文件名的文件到服务器
     * @param {String} fileName
     * @return: {String}
     * @author: YuHangChen
     * @time: 16/7/2020 下午4:51
     */
    @GetMapping("/downloadFile")
    public String downloadFile(@RequestParam String fileName) throws IOException {
        ossService.downloadFile(fileName);
        return "下载文件成功";
    }




}
