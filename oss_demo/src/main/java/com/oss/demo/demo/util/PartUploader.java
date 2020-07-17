package com.oss.demo.demo.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Author asus
 * @create 16/7/2020 下午8:45
 */
@Slf4j
public class PartUploader implements Runnable {
    private File sampleFile;
    private long startPos;
    private String bucketName;
    private int partNumber;
    private String uploadId;
    private List<PartETag> partETags;
    private String objectName;
    private long curPartSize;
    private OSSClient client;

    public PartUploader( OSSClient client,File sampleFile,long startPos,String bucketName,int partNumber,String uploadId,List<PartETag> partETags,String objectName,long curPartSize){
        this.bucketName=bucketName;
        this.startPos=startPos;
        this.partNumber=partNumber;
        this.uploadId=uploadId;
        this.partETags=partETags;
        this.sampleFile=sampleFile;
        this.objectName=objectName;
        this.curPartSize=curPartSize;
        this.client=client;
    }
    @SneakyThrows
    @Override
    public void run() {
        InputStream inputstream = null;
        try{
             inputstream = new FileInputStream(sampleFile);
            // 跳过已经上传的分片。
            inputstream.skip(startPos);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(objectName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(inputstream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
            uploadPartRequest.setPartNumber(partNumber);
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
            // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
            synchronized (this.partETags){
                partETags.add(uploadPartResult.getPartETag());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if(inputstream!=null){
                try {
                    inputstream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }

    }
}
