package com.gls.media;


import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;

import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    //分块文件上传minio，必须排序，因为minio合并时除了最后一块，其他都不能小于5MB
    @Test
    public void uploadChunk(){
        String chunkFolderPath = "E:\\software\\媒资测试文件\\chunk\\";
        File chunkFolder = new File(chunkFolderPath);
        File[] files = chunkFolder.listFiles();
        Arrays.sort(files, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
        for (int i = 0; i < files.length; i++) {
            try{
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs
                        .builder().
                        bucket("testbucket").
                        object("chunk/"+i).
                        filename(files[i].getAbsolutePath())    //文件本地路径
                        .build();
                minioClient.uploadObject(uploadObjectArgs);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //通过minio的合并文件，注意minio分块大小至少为5MB
    @Test
    public void test_merge() throws Exception{
        //从minio中获取分块源文件
        List<ComposeSource> sources = Stream
                .iterate(0,i->++i)  //从0开始遍历，到
                .limit(19)  //i<?
                .map(i->ComposeSource   //相当于for循环里面，获取minio里面的分块源文件
                        .builder()
                        .bucket("testbucket")
                        .object("chunk/".concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket("testbucket").object("merge.mp4").sources(sources).build();
        minioClient.composeObject(composeObjectArgs);
    }

    //清除分块文件
    @Test
    public void test_removeObjects(){
        List<DeleteObject> deleteObjects = Stream
                .iterate(0, i -> ++i)
                .limit(19)
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());
        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("testbucket").objects(deleteObjects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

    }
}
