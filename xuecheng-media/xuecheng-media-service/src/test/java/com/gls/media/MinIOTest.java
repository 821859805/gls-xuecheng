package com.gls.media;


import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;

import io.minio.errors.*;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author 郭林赛
 * @version 1.0
 * @description minio测试
 * @createDate 2024/7/11 18:07
 **/

public class MinIOTest {
    private static final String endpoint = "http://192.168.32.128:9001";
    private static final String accessKey = "minioadmin";
    private static final String secretKey = "minioadmin";
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey,secretKey)
                    .build();

    @Test
    public void upload(){
        //根据扩展名得到mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".jpeg");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;     //通用mimeType，字节流
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .object("001/人像面.jpeg")
                    .filename("C:\\Users\\Admin\\OneDrive\\桌面\\人像面.jpeg")
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    @Test
    public void delete(){
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket("testbucket").object("001/人像面.jpeg").build());
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败！！！");
        }
    }

    @Test
    public void getFile(){
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("001/人像面.jpeg").build();
        try(
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream outputStream = new FileOutputStream(new File("C:\\Users\\Admin\\OneDrive\\桌面\\upload\\test.jpeg"));
        ) {

            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
