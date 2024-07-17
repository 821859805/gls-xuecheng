package com.gls.media.service;

import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.base.model.RestResponse;
import com.gls.media.model.dto.QueryMediaParamsDto;
import com.gls.media.model.dto.UploadFileParamsDto;
import com.gls.media.model.po.MediaFiles;
import com.gls.media.model.vo.UploadFileVo;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    public UploadFileVo uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto,String localFilePath);
    
    /**
     * @description 检查文件是否存在
     *
     * @param fileMd5
     * @return com.gls.base.model.RestResponse<java.lang.Boolean>
     * @author 郭林赛
     * @createDate 2024/7/16 8:57
     **/
    public RestResponse<Boolean> checkFile(String fileMd5);
    
    /**
     * @description 检查分块是否存在
     *
     * @param fileMd5
     * @param chunkIndex
     * @return com.gls.base.model.RestResponse<java.lang.Boolean>
     * @author 郭林赛
     * @createDate 2024/7/16 8:57
     **/
    public RestResponse<Boolean> checkchunk(String fileMd5,int chunkIndex);
    
    /**
     * @description 上传分块
     *
     * @param fileMd5
     * @param chunk
     * @param localChunkFilePath
     * @return com.gls.base.model.RestResponse
     * @author 郭林赛
     * @createDate 2024/7/16 9:00
     **/
    public RestResponse uploadChunk(String fileMd5,int chunk, String localChunkFilePath);

    /**
     * @description 合并分块
     *
     * @param companyId
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return com.gls.base.model.RestResponse
     * @author 郭林赛
     * @createDate 2024/7/16 9:01
     **/
    public  RestResponse mergechunks(Long companyId, String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);

    public MediaFiles addMediaFileToDb(Long companyId, String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);

}
