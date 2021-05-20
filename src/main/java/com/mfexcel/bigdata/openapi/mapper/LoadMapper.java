package com.mfexcel.bigdata.openapi.mapper;

import com.mfexcel.bigdata.openapi.utils.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface LoadMapper {
    //加载文件方法
    AjaxResult upload(MultipartFile[] files);

    //下载文件方法
//    AjaxResult download(String fileName, HttpServletRequest request) throws Exception;
    AjaxResult download(String fileName, HttpServletResponse response);

}
