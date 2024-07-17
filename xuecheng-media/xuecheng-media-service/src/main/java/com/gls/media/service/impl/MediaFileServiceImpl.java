package com.gls.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gls.base.exception.XueChengException;
import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.base.model.RestResponse;
import com.gls.media.mapper.MediaFilesMapper;
import com.gls.media.model.dto.QueryMediaParamsDto;
import com.gls.media.model.dto.UploadFileParamsDto;
import com.gls.media.model.po.MediaFiles;
import com.gls.media.model.vo.UploadFileVo;
import com.gls.media.service.MediaFileService;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jdk.internal.util.xml.impl.Input;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资管理服务类
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaFileServiceImpl implements MediaFileService {

    private final MediaFilesMapper mediaFilesMapper;
    private final MinioClient minioClient;
    @Value("${minio.bucket.files}")
    private String bucket_files;
    @Value("${minio.bucket.videofiles}")
    private String bucket_videoFiles;
    @Autowired
    private MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    //获取文件默认存储目录路径，年/月/日
    private String getDefaultFolderPath(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-","/")+"/";
        return folder;
    }

    //获取文件的md5
    private String getFileMd5(File file){
        try(FileInputStream fileInputStream = new FileInputStream(file)){
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //根据扩展名获得通用字节流
    private String getMimeType(String extension){
        if(extension==null)
            extension="";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null)
            mimeType = extensionMatch.getMimeType();
        return mimeType;
    }

    /**
     * @description 文件写入minIO
     *
     * @param localFilePath
     * @param mimeType
     * @param bucket
     * @param objectName
     * @return boolean
     * @author 郭林赛
     * @createDate 2024/7/13 9:53
     **/
    public boolean addMediaFilesToMinIO(String localFilePath, String mimeType,String bucket,String objectName){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.info("上传文件到minio成功，bucket：{}，objectName：{}",bucket,objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错，bucket：{}，objectName：{}，错误原因：{}",bucket,objectName,e.getMessage(),e);
            XueChengException.cast("上传文件到文件系统失败");
        }
        return false;
    }

    /**
     * @description 文件信息添加到数据库
     *
     * @param companyId
     * @param fileMd5
     * @param uploadFileParamsDto
     * @param bucket
     * @param objectName
     * @return com.gls.media.model.po.MediaFiles
     * @author 郭林赛
     * @createDate 2024/7/16 9:54
     **/
    @Transactional
    public MediaFiles addMediaFileToDb(Long companyId, String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles==null){
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtil.copyProperties(uploadFileParamsDto,mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                XueChengException.cast("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());
        }
        return mediaFiles;
    }



    @Override
    public UploadFileVo uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        File file = new File(localFilePath);
        if(!file.exists()){
            XueChengException.cast("文件不存在");
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //文件md5值
        String fileMd5 = getFileMd5(file);
        //文件默认目录
        String defaultFolderPath = getDefaultFolderPath();
        //存储到minio中的对象名（带目录）
        String objectName = defaultFolderPath + fileMd5 + extension;
        //将文件上传到minio
        boolean b = addMediaFilesToMinIO(localFilePath,mimeType,bucket_files,objectName);
        //文件大小
        uploadFileParamsDto.setFileSize(file.length());
        //文件信息存储到数据库
        MediaFiles mediaFiles = addMediaFileToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
        //准备返回数据
        UploadFileVo uploadFileVo = new UploadFileVo();
        BeanUtils.copyProperties(mediaFiles,uploadFileVo);
        return uploadFileVo;
    }

    /**
     * @description 检查文件是否存在
     *
     * @param fileMd5
     * @return com.gls.base.model.RestResponse<java.lang.Boolean>
     * @author 郭林赛
     * @createDate 2024/7/16 9:02
     **/
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles!=null){
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();
            try {
                InputStream stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());
                if(stream!=null)
                    return RestResponse.success(true);
            }catch (Exception e){
                log.debug("网络错误，媒体文件检查失败");
                e.printStackTrace();
            }
        }
        log.debug("文件不存在");
        return RestResponse.success(false);
    }

    //分块文件目录
    private String getChunkFileFolderPath(String fileMd5){
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+"chunk"+"/";
    }

    /**
     * @description 检查分块是否存在
     *
     * @param fileMd5
     * @param chunkIndex
     * @return com.gls.base.model.RestResponse<java.lang.Boolean>
     * @author 郭林赛
     * @createDate 2024/7/16 9:02
     **/
    @Override
    public RestResponse<Boolean> checkchunk(String fileMd5, int chunkIndex) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        try{
            InputStream fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(chunkFilePath)
                            .build());
            if(fileInputStream!=null)
                return RestResponse.success(true);
        }catch (Exception e){

        }
        return RestResponse.success(false);
    }

    /**
     * @description 上传一个分块到数据库
     *
     * @param fileMd5
     * @param chunk
     * @param localChunkFilePath
     * @return com.gls.base.model.RestResponse
     * @author 郭林赛
     * @createDate 2024/7/16 9:02
     **/
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath = chunkFileFolderPath + chunk;
        String mimeType = getMimeType(null);
        boolean flag = addMediaFilesToMinIO(localChunkFilePath,mimeType,bucket_videoFiles,chunkFilePath);
        if(!flag){
            log.debug("上传分块文件失败：{}",chunkFilePath);
            return RestResponse.validfail(false,"上传分块文件失败");
        }
        log.debug("上传分块文件成功：{}",chunkFilePath);
        return RestResponse.success(true);
    }

    //得到合并后文件的地址
    private String getFilePathByMd5(String fileMd5,String extendName){
        return fileMd5.charAt(0)+"/"+fileMd5.charAt(1)+"/"+fileMd5+"/"+fileMd5+extendName;
    }

    /**
     * @description 合并分块
     *
     * @param companyId
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return com.gls.base.model.RestResponse
     * @author 郭林赛
     * @createDate 2024/7/16 9:02
     **/
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_videoFiles)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        String fileName = uploadFileParamsDto.getFilename();
        String extendName = fileName.substring(fileName.lastIndexOf("."));
        String mergeFilePath = getFilePathByMd5(fileMd5,extendName);
        try {
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build()
            );
        }catch (Exception e){
            log.debug("合并文件失败，fileMd5：{}，异常：{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false,"合并文件失败！！！！！");
        }

        //验证md5
        File minioFile = downloadFileFromMinIO(bucket_videoFiles,mergeFilePath);
        if(minioFile==null){
            log.debug("下载合并后文件失败，mergeFilePath：{}",mergeFilePath);
            return RestResponse.validfail(false,"下载合并文件失败");
        }

        try(InputStream newFileInputStream = new FileInputStream(minioFile)){
            String md5Hex = DigestUtils.md5Hex(newFileInputStream);
            if(!fileMd5.equals(md5Hex)){
                return RestResponse.validfail(false,"文件合并校验失败，上传失败！！！");
            }
            uploadFileParamsDto.setFileSize(minioFile.length());
        }catch (Exception e){
            log.debug("校验文件失败！fileMd5：{}，异常：{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false,"文件合并校验失败，最终上传失败！！！");
        }finally {
            if(minioFile!=null){
                minioFile.delete();
            }
        }

        //文件入库
        currentProxy.addMediaFileToDb(companyId,fileMd5,uploadFileParamsDto,bucket_videoFiles,mergeFilePath);
        //清除分块文件
        clearChunkFiles(chunkFileFolderPath,chunkTotal);
        return RestResponse.success(true);
    }

    /**
     * @description 从minio下载文件
     *
     * @param bucket
     * @param objectName
     * @return java.io.File
     * @author 郭林赛
     * @createDate 2024/7/16 9:38
     **/
    public File downloadFileFromMinIO(String bucket,String objectName){
        FileOutputStream outputStream = null;
        try{
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            File minioFile = File.createTempFile("minio",".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream,outputStream);
            return minioFile;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //清除分块文件
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal){
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket(bucket_videoFiles)
                    .objects(deleteObjects)
                    .build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r->{
                DeleteError deleteError = null;
                try{
                    deleteError = r.get();
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("清除分块文件失败，objectName：{}",deleteError.objectName(),e);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            log.error("清除分块文件失败，chunkFileFolderPath：{}",chunkFileFolderPath,e);
        }
    }
}
