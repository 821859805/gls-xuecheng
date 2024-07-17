package com.gls.media;

import com.baomidou.mybatisplus.extension.api.R;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author 郭林赛
 * @version 1.0
 * @description 大文件处理测试
 * @createDate 2024/7/15 10:44
 **/

public class BigFileTest {
    //测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("E:\\software\\媒资测试文件\\单调栈（思路稍复杂）.mp4");   //要分块的文件
        String chunkPath = "E:\\software\\媒资测试文件\\chunk\\";
        File chunkFolder = new File(chunkPath);     //分块后分块存储位置
        if(!chunkFolder.exists()){
            chunkFolder.mkdirs();
        }
        long chunkSize = 1024 * 1024 * 5;   //分块大小1MB
        long chunkNum = (long)Math.ceil(sourceFile.length()*1.0/chunkSize); //分块数量
        byte[] buffer = new byte[1024]; //缓冲区大小
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile,"r"); //以只读的方式打开源文件
        for (long i = 0; i < chunkNum; i++) {
            File file = new File(chunkPath+i);
            if(file.exists())
                file.delete();  //如果文件已存在则删掉
            boolean newFile = file.createNewFile(); //创建文件
            if(newFile){
                RandomAccessFile raf_write = new RandomAccessFile(file,"rw");   //读写方式打开
                int len = -1;
                while((len=raf_read.read(buffer))!=-1){
                    raf_write.write(buffer,0,len);
                    if(file.length()>=chunkSize){
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }

    @Test
    public void testMerge() throws IOException{
        File chunkFolder = new File("E:\\software\\媒资测试文件\\chunk\\");
        File originalFile = new File("E:\\software\\媒资测试文件\\单调栈（思路稍复杂）.mp4"); //源文件，用于md5校验
        File mergeFile = new File("E:\\software\\媒资测试文件\\merge.mp4");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        mergeFile.createNewFile();
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
//        raf_write.seek(0);  //指针指向文件顶端
        byte[] buffer = new byte[1024];
        File[] fileArray = chunkFolder.listFiles(); //目录下所有文件
        List<File> fileList = Arrays.asList(fileArray);
        fileList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getName())));  //比较方式：文件名的数字大小
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"rw");
            int len = -1;
            while((len=raf_read.read(buffer))!=-1){
                raf_write.write(buffer,0,len);
            }
            raf_read.close();
        }
        raf_write.close();

        //md5校验文件
        try (
                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        ){
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5
            String mergeMd5 = DigestUtils.md5Hex(mergeFileStream);
            if(originalMd5.equals(mergeMd5)){
                System.out.println("合并文件成功！！！");
            }else{
                System.out.println("合并文件失败！！！");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("合并文件失败！！！");
        }
    }
}
