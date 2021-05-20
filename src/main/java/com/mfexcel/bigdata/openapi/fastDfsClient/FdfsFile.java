package com.mfexcel.bigdata.openapi.fastDfsClient;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.github.tobato.fastdfs.domain.fdfs.StorePath;

import java.util.HashMap;

public class FdfsFile extends StorePath {
    private String fileName;
    private Long filesSize;
    private String fileUrl;
    private String fileFullPath;
    private String fileExtName;

    private HashMap result = new HashMap();

    public HashMap toHashMap(){
        this.result.put("oldFileName",this.fileName);
        this.result.put("fileDownLoadUri",this.fileUrl);
        this.result.put("size",this.filesSize);
        this.result.put("objFilePath",this.fileFullPath);
        return this.result;
    }

    public FdfsFile() {
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFilesSize() {
        return this.filesSize;
    }

    public void setFilesSize(Long filesSize) {
        this.filesSize = filesSize;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileFullPath() {
        this.fileFullPath = this.getFullPath();
        return this.fileFullPath;
    }

    public void setFileFullPath(String fileFullPath) {
        this.fileFullPath = fileFullPath;
    }

    public String getFileExtName() {
        return this.fileExtName;
    }

    public void setFileExtName(String fileExtName) {
        this.fileExtName = fileExtName;
    }
}
