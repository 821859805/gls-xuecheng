package com.gls.media.service;

import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
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
}
