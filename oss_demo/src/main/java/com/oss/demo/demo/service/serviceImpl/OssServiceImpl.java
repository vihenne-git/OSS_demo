package com.oss.demo.demo.service.serviceImpl;

import com.aliyun.oss.OSSClient;
import com.oss.demo.demo.service.OssService;
import com.oss.demo.demo.util.OSSUtil;
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

    private OSSUtil ossunit = null;
    private OSSClient client = null;
    private String bucketName = "vihenne-demo";

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        ossunit = new OSSUtil();
        client = OSSUtil.getOSSClient();
        String diskName = "datas/image/";//你要存放的Bucket的目录 
        String md5key = OSSUtil.uploadObject2OSS(client, file, bucketName, diskName);
        log.info("上传后的文件MD5数字唯一签名:" + md5key);
        return "";
    }

    @Override
    public String downloadFile(String fileName) throws IOException {
        ossunit = new OSSUtil();
        client = OSSUtil.getOSSClient();
        String diskName = "datas/image/";//你要存放的Bucket的目录
        String fileSavePath="G:/Test/"+fileName;
        OSSUtil.DownloadFile(client, bucketName, diskName,fileName,fileSavePath);
        return "";
    }


    @Override
    public String uploadBigFile(MultipartFile file) throws IOException {
        //初始化
        ossunit = new OSSUtil();
        client = OSSUtil.getOSSClient();
        String diskName = "datas/image/";//你要存放的Bucket的目录 

        //上传文件
        OSSUtil.uploadBigFile(client, file, bucketName, diskName);
        return "";
    }

}
