package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

@Data
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String bucketName;

    public AliOssUtil(String endpoint, String bucketName) {
        this.endpoint = endpoint;
        this.bucketName = bucketName;
    }

    /**
     * 文件上传
     *
     * @param bytes 文件字节数组
     * @param objectName 文件对象名
     * @return 文件访问路径
     */
    public String upload(byte[] bytes, String objectName) {

        // 从系统环境变量中获取密钥
        String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");

        if (accessKeyId == null || accessKeySecret == null) {
            throw new RuntimeException("阿里云 OSS 环境变量未配置：ALIBABA_CLOUD_ACCESS_KEY_ID / ALIBABA_CLOUD_ACCESS_KEY_SECRET");
        }

        // 创建 OSSClient 实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 上传文件
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            log.error("OSS服务异常: {}", oe.getErrorMessage(), oe);
            throw new RuntimeException("文件上传失败：OSS服务异常");
        } catch (ClientException ce) {
            log.error("OSS客户端异常: {}", ce.getMessage(), ce);
            throw new RuntimeException("文件上传失败：客户端异常");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 文件访问路径规则：https://BucketName.Endpoint/ObjectName
        String url = "https://" + bucketName + "." + endpoint + "/" + objectName;
        log.info("文件上传到: {}", url);

        return url;
    }
}