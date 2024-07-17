package com.gls.media.api;

import com.gls.base.model.PageParams;
import com.gls.base.model.PageResult;
import com.gls.base.model.RestResponse;
import com.gls.media.model.dto.QueryMediaParamsDto;
import com.gls.media.model.dto.UploadFileParamsDto;
import com.gls.media.model.po.MediaFiles;
import com.gls.media.model.vo.UploadFileVo;
import com.gls.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
@RequiredArgsConstructor
public class MediaFilesController {

    private final MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);

    }

    @ApiOperation("上传文件")
    @PostMapping(value="/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileVo upload(@RequestPart("filedata") MultipartFile filedata,
                               @RequestParam(value = "folder",required = false) String folder,
                               @RequestParam(value = "objectName",required = false) String objectName) throws IOException {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        //图片
        uploadFileParamsDto.setFileType("001001");
        //文件名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());//文件名称
        //创建临时文件
        File tempFile = File.createTempFile("minio", "temp");
        //上传的文件拷贝到临时文件
        filedata.transferTo(tempFile);
        //文件路径
        String absolutePath = tempFile.getAbsolutePath();
        //上传文件
        UploadFileVo uploadFileVo = mediaFileService.uploadFile(companyId, uploadFileParamsDto, absolutePath);

        return uploadFileVo;
    }

    /**
     * @description 文件上传前检查
     *
     * @param fileMd5
     * @return com.gls.base.model.RestResponse<java.lang.Boolean>
     * @author 郭林赛
     * @createDate 2024/7/16 8:47
     **/
    @ApiOperation(value = "文件上传前检查")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(
            @RequestParam("fileMd5") String fileMd5
    ) throws Exception{
        return mediaFileService.checkFile(fileMd5);
    }

    /**
     * @description 分块文件上传前检测
     *
     * @param fileMd5
     * @param chunk
     * @return com.gls.base.model.RestResponse<java.lang.Boolean>
     * @author 郭林赛
     * @createDate 2024/7/16 8:48
     **/
    @ApiOperation(value = "分块文件上传前检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk)
            throws Exception{
        return mediaFileService.checkchunk(fileMd5,chunk);
    }

    /**
     * @description 上传分块文件
     *
     * @param file
     * @param fileMd5
     * @param chunk
     * @return com.gls.base.model.RestResponse
     * @author 郭林赛
     * @createDate 2024/7/16 8:55
     **/
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5")String fileMd5,
                                    @RequestParam("chunk") int chunk)
            throws Exception{
        File tempFile = File.createTempFile("minio","temp");
        file.transferTo(tempFile);  //拷贝临时文件
        String absolutePath = tempFile.getAbsolutePath();
        return mediaFileService.uploadChunk(fileMd5,chunk,absolutePath);
    }

    /**
     * @description 合并文件
     *
     * @param fileMd5
     * @param fileName
     * @param chunkTotal
     * @return com.gls.base.model.RestResponse
     * @author 郭林赛
     * @createDate 2024/7/16 8:52
     **/
    @PostMapping("/upload/mergechunks")
    @ApiOperation(value = "合并文件")
    public RestResponse mergechunks(@RequestParam("fileMd5")String fileMd5,
                                    @RequestParam("fileName")String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal)
            throws Exception{
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(fileName);
        return mediaFileService.mergechunks(companyId,fileMd5,chunkTotal,uploadFileParamsDto);
    }



}
