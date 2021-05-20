package com.mfexcel.bigdata.openapi.fastDfsClient;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class FdfsClient {
    @Resource
    private FastFileStorageClient fastFileStorageClient;
    @Resource
    private  FdfsWebServer fdfsWebServer;

    public FdfsClient() {
    }

    public FdfsFile uploadFile(MultipartFile file, double... compressParams) throws Exception {
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        InputStream inputStream = file.getInputStream();
        if (compressParams != null && compressParams.length == 2) {
            double compressFileSize = compressParams[0];
            double compressAccuracy = compressParams[1];
            byte[] fileByteArray = this.compressImageCycle(file.getBytes(), compressFileSize, compressAccuracy);
            fileSize = (long)fileByteArray.length;
            inputStream = new ByteArrayInputStream(fileByteArray);
        }

        return this.uploadFile((InputStream)inputStream, fileName, fileSize);
    }

    public FdfsFile uploadFile(File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        String fileName = file.getName();
        return this.uploadFile((InputStream)inputStream, fileName, file.length());
    }

    public FdfsFile uploadString(String str, String fileName) throws Exception {
        if (null != str && str.length() > 0) {
            byte[] fileByteArray = str.getBytes(StandardCharsets.UTF_8);
            return this.uploadFile(fileByteArray, fileName, (long)fileByteArray.length);
        } else {
            return null;
        }
    }

    public FdfsFile uploadCompressFile(InputStream inputStream, String fileName, double compressFileSize, double compressAccuracy) throws Exception {
        byte[] fileByteArray = IOUtils.toByteArray(inputStream);
        fileByteArray = this.compressImageCycle(fileByteArray, compressFileSize, compressAccuracy);
        ByteArrayInputStream InputStream = new ByteArrayInputStream(fileByteArray);
        return this.uploadFile((InputStream)inputStream, fileName, (long)fileByteArray.length);
    }

    public FdfsFile uploadFile(byte[] fileByteArray, String fileName, long fileSize) throws Exception {
        return this.uploadFile((InputStream)(new ByteArrayInputStream(fileByteArray)), fileName, fileSize);
    }

    public FdfsFile uploadFile(InputStream inputStream, String fileName, long fileSize) throws Exception {
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        StorePath storePath = this.fastFileStorageClient.uploadFile(inputStream, fileSize, fileExtName, (Set)null);
        FdfsFile fdfsFile = new FdfsFile();
        fdfsFile.setGroup(storePath.getGroup());
        fdfsFile.setPath(storePath.getPath());
        fdfsFile.setFileFullPath(storePath.getFullPath());
        fdfsFile.setFilesSize(fileSize);
        fdfsFile.setFileName(fileName);
        fdfsFile.setFileExtName(fileExtName);
        fdfsFile.setFileUrl(this.fdfsWebServer.getWebServerUrl() +"/"+ storePath.getFullPath());
        return fdfsFile;
    }

    public byte[] download(String fileUrl, HttpServletResponse response) throws Exception {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        byte[] bytes = (byte[])this.fastFileStorageClient.downloadFile(group, path, new DownloadByteArray());
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(bytes, outputStream);
        return bytes;
    }

    public byte[] download(String fileUrl) throws Exception {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        return (byte[])this.fastFileStorageClient.downloadFile(group, path, new DownloadByteArray());
    }

    public void deleteFile(String fileUrl) {
        StorePath storePath = StorePath.parseFromUrl(fileUrl);
        this.fastFileStorageClient.deleteFile(storePath.getGroup(), storePath.getPath());
    }

    private byte[] compressImageCycle(byte[] bytes, double compressFileSize, double compressAccuracy) throws Exception {
        long newFileLength = (long)bytes.length;
        if ((double)newFileLength <= compressFileSize) {
            return bytes;
        } else {
            BufferedImage srcImage = ImageIO.read(new ByteArrayInputStream(bytes));
            int WIDTH = (int)((double)srcImage.getWidth() * compressAccuracy);
            int HEIGHT = (int)((double)srcImage.getHeight() * compressAccuracy);
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, 1);
            Graphics g = image.getGraphics();
            g.drawImage(srcImage, 0, 0, WIDTH, HEIGHT, (ImageObserver)null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return this.compressImageCycle(baos.toByteArray(), compressFileSize, compressAccuracy);
        }
    }
}

