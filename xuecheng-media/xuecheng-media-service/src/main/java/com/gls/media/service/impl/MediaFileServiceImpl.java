package com.gls.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gls.base.exception.XueChengException;
import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.media.mapper.MediaFilesMapper;
import com.gls.media.model.dto.QueryMediaParamsDto;
import com.gls.media.model.dto.UploadFileParamsDto;
import com.gls.media.model.po.MediaFiles;
import com.gls.media.model.vo.UploadFileVo;
import com.gls.media.service.MediaFileService;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    private String bucketFiles;

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
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错，bucket：{}，objectName：{}，错误原因：{}",bucket,objectName,e.getMessage(),e);
            XueChengException.cast("上传文件到文件系统失败");
        }
        return false;
    }

    //文件信息添加到数据库
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
        boolean b = addMediaFilesToMinIO(localFilePath,mimeType,bucketFiles,objectName);
        //文件大小
        uploadFileParamsDto.setFileSize(file.length());
        //文件信息存储到数据库
        MediaFiles mediaFiles = addMediaFileToDb(companyId, fileMd5, uploadFileParamsDto, bucketFiles, objectName);
        //准备返回数据
        UploadFileVo uploadFileVo = new UploadFileVo();
        BeanUtils.copyProperties(mediaFiles,uploadFileVo);
        return uploadFileVo;
    }
}
