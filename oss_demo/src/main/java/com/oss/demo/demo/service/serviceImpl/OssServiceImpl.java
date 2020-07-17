package com.oss.demo.demo.service.serviceImpl;

import com.aliyun.oss.OSSClient;
import com.oss.demo.demo.service.OssService;
import com.oss.demo.demo.util.OssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * @Author asus
 * @create 16/7/2020 下午3:25
 */
@Slf4j
@Service
public class OssServiceImpl implements OssService {

    private OssUtil ossunit = null;
    private OSSClient client = null;
    private String bucketName = "vihenne-demo";

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        ossunit = new OssUtil();
        client = OssUtil.getOssClient();
        String diskName = "datas/image/";//你要存放的Bucket的目录 
        String md5key = OssUtil.uploadObject2Oss(client, file, bucketName, diskName);
        log.info("上传后的文件MD5数字唯一签名:" + md5key);
        return "";
    }

    @Override
    public String downloadFile(String fileName) throws IOException {
        ossunit = new OssUtil();
        client = OssUtil.getOssClient();
        //你要存放的Bucket的目录
        String diskName = "datas/image/";
        String fileSavePath="G:/Test/"+fileName;
        OssUtil.downloadFile(client, bucketName, diskName,fileName,fileSavePath);
        return "";
    }


    @Override
    public String uploadBigFile(MultipartFile file) throws IOException {
        //初始化
        ossunit = new OssUtil();
        client = OssUtil.getOssClient();
        //你要存放的Bucket的目录 
        String diskName = "datas/image/";
        //上传文件
        OssUtil.uploadBigFile(client, file, bucketName, diskName);
        return "";
    }

}
