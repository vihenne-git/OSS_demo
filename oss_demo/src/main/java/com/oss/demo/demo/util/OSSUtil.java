package com.oss.demo.demo.util;

/**
 * @Author asus
 * @create 16/7/2020 上午10:01
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import static java.util.concurrent.Executors.*;

@Slf4j
public class OSSUtil {



    //阿里云API的内或外网域名
    private static String ENDPOINT;

    //阿里云API的密钥Access Key ID
    private static String ACCESS_KEY_ID;

    //阿里云API的密钥Access Key Secret
    private static String ACCESS_KEY_SECRET;

    //创建一个可重用固定线程数的线程池
    private static ExecutorService executorService = newFixedThreadPool(10);

    //init static datas
    static{
        // endpoint
        ENDPOINT= "oss-cn-hangzhou.aliyuncs.com";
        ACCESS_KEY_ID = "LTAI4G7wkzKXcuHhPL27sb4j";
        ACCESS_KEY_SECRET = "O44mm1IqWCbQyUfpaZSHOpCEcLoX3r";
    }

     /**
     * 获取阿里云OSS客户端对象
     * */
     public static final OSSClient getOSSClient(){
         return new OSSClient(ENDPOINT,ACCESS_KEY_ID, ACCESS_KEY_SECRET);
     }

     /**
     * 新建Bucket  --Bucket权限:私有
     * @param bucketName bucket名称
     * @return true 新建Bucket成功
     * */
     public static final boolean createBucket(OSSClient client, String bucketName){
         Bucket bucket = client.createBucket(bucketName);
         return bucketName.equals(bucket.getName());
     }

     /**
     * 删除Bucket
     * @param bucketName bucket名称
     * */
     public static final void deleteBucket(OSSClient client, String bucketName){
         client.deleteBucket(bucketName);
         log.info("删除" + bucketName + "Bucket成功");
     }

     /**
     * 向阿里云的OSS存储中存储文件  --file也可以用InputStream替代
     * @param client OSS客户端
     * @param file 上传文件
     * @param bucketName bucket名称
     * @param diskName 上传文件的目录  --bucket下文件的路径
     * @return String 唯一MD5数字签名
     * */
     public static final String uploadObject2OSS(OSSClient client, MultipartFile mfile, String bucketName, String diskName) throws IOException {
         File file = null;
         if (mfile.equals("") || mfile.getSize() <= 0) {
             mfile = null;
         } else {
             InputStream ins = null;
             ins = mfile.getInputStream();
             file = new File(mfile.getOriginalFilename());
             inputStreamToFile(ins, file);
             ins.close();
         }

         String resultStr = null;
         try {
             InputStream is = new FileInputStream(file);
             String fileName = file.getName();
             Long fileSize = file.length();
             //创建上传Object的Metadata
             ObjectMetadata metadata = new ObjectMetadata();
             metadata.setContentLength(is.available());
             metadata.setCacheControl("no-cache");
             metadata.setHeader("Pragma", "no-cache");
             metadata.setContentEncoding("utf-8");
             metadata.setContentType(getContentType(fileName));
             metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
             //上传文件
             PutObjectResult putResult = client.putObject(bucketName, diskName + fileName, is, metadata);
             //解析结果
             resultStr = putResult.getETag();
         } catch (Exception e) {
            log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
         }
         return resultStr;
     }

     /**
     * 根据key获取OSS服务器上的文件输入流
     * @param client OSS客户端
     * @param bucketName bucket名称
     * @param diskName 文件路径
     * @param key Bucket下的文件的路径名+文件名
     */
     public static final void DownloadFile(OSSClient client, String bucketName, String diskName, String key,String fileSavePath) throws IOException {
         OSSObject ossObj = client.getObject(bucketName, diskName + key);
         InputStream content = ossObj.getObjectContent();
         try {
             BufferedInputStream bis = new BufferedInputStream(content);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(fileSavePath)));
             int itemp = 0;
             while((itemp = bis.read()) != -1){
                 bos.write(itemp);
             }
             log.info("文件获取成功");
             bis.close();
             bos.close();
         } catch (Exception e) {
             log.error("从OSS获取文件失败:" + e.getMessage(), e);
         }

     }

     /**

     * 根据key删除OSS服务器上的文件
     * @param client OSS客户端
     * @param bucketName bucket名称
     * @param diskName 文件路径
     * @param key Bucket下的文件的路径名+文件名
     */
     public static void deleteFile(OSSClient client, String bucketName, String diskName, String key){
         client.deleteObject(bucketName, diskName + key);
         log.info("删除" + bucketName + "下的文件" + diskName + key + "成功");
     }

     /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     * @param fileName 文件名
     * @return 文件的contentType
     */
     public static final String getContentType(String fileName){
         String fileExtension = fileName.substring(fileName.lastIndexOf("."));
         if("bmp".equalsIgnoreCase(fileExtension)) {
             return "image/bmp";
         }
         if("gif".equalsIgnoreCase(fileExtension)) {
             return "image/gif";
         }
         if("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension)  || "png".equalsIgnoreCase(fileExtension) ) {
             return "image/jpeg";
         }
         if("html".equalsIgnoreCase(fileExtension)) {
             return "text/html";
         }
         if("txt".equalsIgnoreCase(fileExtension)) {
             return "text/plain";
         }
         if("vsd".equalsIgnoreCase(fileExtension)) {
             return "application/vnd.visio";
         }
         if("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
             return "application/vnd.ms-powerpoint";
         }
         if("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
             return "application/msword";
         }
         if("xml".equalsIgnoreCase(fileExtension)) {
             return "text/xml";
         }
         return "text/html";
     }

    /**
     *
     *
     * * InputStream 转 File
     * * @param ins
     * * @param file
     *
     */
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
     *
     *
     * @description: 上传大文件
     * @param
     * @return:
     * @author: YuHangChen
     * @time: 16/7/2020 下午5:36
     */
    public static void uploadBigFile(OSSClient client,MultipartFile mfile,String bucketName,String diskName) throws IOException {
        File file = null;
        if (mfile.equals("") || mfile.getSize() <= 0) {
            mfile = null;
        } else {
            InputStream ins = null;
            ins = mfile.getInputStream();
            file = new File(mfile.getOriginalFilename());
            inputStreamToFile(ins, file);
            ins.close();
        }


        String objectName = diskName + file.getName();
        // 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName,objectName);
        // 初始化分片。
        InitiateMultipartUploadResult upresult = client.initiateMultipartUpload(request);
        // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
        String uploadId = upresult.getUploadId();
        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags =  new ArrayList<PartETag>();
        try {
            // 计算文件有多少个分片。
            final long partSize = 1 * 1024 * 1024L;   // 1MB
            final File sampleFile = file;
            long fileLength = sampleFile.length();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            log.info("partCount = "+partCount);


            // 遍历分片创建线程加入线程池。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                executorService.execute(new PartUploader(client,sampleFile,startPos,bucketName,i+1,uploadId, partETags,objectName,curPartSize));
            }
            //等待所有的分片完成
            executorService.shutdown();
            while (!executorService.isTerminated()){
                try {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
            //验证是否所有的分片都完成
            if(partETags.size()!=partCount){
                throw new IllegalStateException("文件的某些部分上传失败！");
            }else {
                log.info("成功上传文件"+file.getName());
            }
            //排序。partETags必须按分片号升序排列
            Collections.sort(partETags, new Comparator<PartETag>() {
                @Override
                public int compare(PartETag o1, PartETag o2) {
                    return o1.getPartNumber()-o2.getPartNumber();
                }
            });

            // 创建CompleteMultipartUploadRequest对象。
            // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);
            // 完成上传。
            CompleteMultipartUploadResult completeMultipartUploadResult = client.completeMultipartUpload(completeMultipartUploadRequest);
        }catch (
                OSSException oe){
            log.error(oe.getMessage());
        }catch (
                ClientException ce){
            log.error(ce.getErrorMessage());
        } finally {
            if (client!=null)
            //关闭OSSClient
            {
                client.shutdown();
            }
        }
        return ;
    }

}

