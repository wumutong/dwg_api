package com.mfexcel.bigdata.openapi.mapper.impl;

import com.mfexcel.bigdata.openapi.fastDfsClient.FdfsClient;
import com.mfexcel.bigdata.openapi.fastDfsClient.FdfsFile;
import com.mfexcel.bigdata.openapi.mapper.LoadMapper;
import com.mfexcel.bigdata.openapi.utils.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;


@Service("loadMappers")
public class LoadImpl implements LoadMapper {


    private static final Logger logger = LoggerFactory.getLogger(LoadImpl.class);

    @Autowired
    private FdfsClient fdfsClient = new FdfsClient();

    @Override
    public AjaxResult upload(MultipartFile[] files) {


        if(files.length  == 0){
            return AjaxResult.no_content("files is not exists!!",null);
        }
//        废弃
//        //返回文件属性
//        return AjaxResult.success("成功上传文件！！",Arrays.stream(files)
//                .map(this::uploadFile).map(x-> x.toHashMap()).collect(Collectors.toList()));

        //返回文件属性
        return AjaxResult.success("成功上传文件！！",Arrays.stream(files)
                .map(this::uploadFile).map(x-> x.toHashMap()).collect(Collectors.toList()));

    }

    public FdfsFile uploadFile(MultipartFile file){
        FdfsFile fdfsFile = null;

        try {
//            byte[] bytes = file.getBytes();
//            InputStream inputStream = new ByteArrayInputStream(bytes);
//            String extName = file.getOriginalFilename();
//            long size = file.getSize();
//            fdfsFile  = fdfsClient.uploadFile(inputStream,extName,size);

            fdfsFile  = fdfsClient.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        废弃  改 使用 fdfs
//        String ori_name=file.getOriginalFilename();
//        //配置服务文件地址
//        String path="/Users/wumutong/我的文档/";
//        String cur_name=path+System.currentTimeMillis()+"-"+ori_name;
//        //设置存储文件地址
//        File curr_file =new File(cur_name);
//
//        if(!curr_file.getParentFile().exists())
//        {
//            curr_file.getParentFile().mkdirs();
//        }
//
//        try{
//            file.transferTo(curr_file);
//        }catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//
//
//        URI fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path(cur_name)
//                .encode(Charset.forName("UTF-8"))
//                .build().toUri();


//        return new UploadFileResponse(cur_name, fileDownloadUri,
//                file.getContentType(), file.getSize());


        return fdfsFile;
    }

    @Override
    public AjaxResult download(String path, HttpServletResponse response){

        try {
             fdfsClient.download(path, response);

//            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
//            toClient.write(download);
//            toClient.flush();
//            toClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


//        try {
//            // path是指欲下载的文件的路径。
//            File file = new File(path);
//            // 取得文件名。
//            String filename = file.getName();
//            // 取得文件的后缀名。
//            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
//            // 以流的形式下载文件。
//            InputStream fis = new BufferedInputStream(new FileInputStream(path));
//
//            //若网络传输丢包时 导致数据为0时 -  不调用read
//            int count = 0;
//            while (count == 0) {
//                count = fis.available();
//            }
//            byte[] buffer = new byte[count];
//            fis.read(buffer);
//            fis.close();
//            // 清空response
//            response.reset();
//            // 设置response的Header
//            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
//            response.addHeader("Content-Length", "" + file.length());
//            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/octet-stream");
//            toClient.write(buffer);
//            toClient.flush();
//            toClient.close();
//            return  null;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return  null;

    }

}
