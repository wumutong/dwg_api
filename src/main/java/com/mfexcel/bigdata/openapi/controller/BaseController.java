package com.mfexcel.bigdata.openapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfexcel.bigdata.openapi.cache.SessionFactoryCache;
import com.mfexcel.bigdata.openapi.load.LoadMapperData;
import com.mfexcel.bigdata.openapi.mapper.BaseMapper;
import com.mfexcel.bigdata.openapi.mapper.LoadMapper;
import com.mfexcel.bigdata.openapi.utils.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/**")
@RestController
public class BaseController {


    @Value("${defalt.page.no:1}")
    private int pageNo;
    @Value("${defalt.page5【；'】.size:10}")
    private int pageSize;

    @Autowired
    private BaseMapper baseMapper;

    @Autowired
    @Qualifier("loadMappers")
    private LoadMapper loadMapper;

    @Autowired
    private LoadMapperData loadMapperData;



    //@PostMapping
    @RequestMapping(method = {RequestMethod.POST ,RequestMethod.OPTIONS })
    public AjaxResult handler(HttpServletRequest request, @RequestBody Map<String, Object> paramMap) throws Throwable {
        //获取接口地址
        String uri = request.getRequestURI();


        //读取XML配置文件
        SqlSessionFactory sqlSessionFactory = SessionFactoryCache.factoryMap.get(uri);
        if (sqlSessionFactory == null) {
            throw new NoHandlerFoundException(request.getMethod(), uri, (new ServletServerHttpRequest(request)).getHeaders());
        }
        //通过SqlsessionFactory打开一个数据库会话
        try (SqlSession session = sqlSessionFactory.openSession()) {
            BaseMapper mapper = session.getMapper(BaseMapper.class);

            //添加 INSERT UPDATE DELETE 相关语句
            if(uri.contains("insert") || uri.contains("update") || uri.contains("delete") ){
                int result = 0;

                if(uri.contains("insert")){
                    if(uri.contains("insertCategoryConfig")){
                        if(!mapper.execute(paramMap).toString().equals("[]")){
                            return  AjaxResult.NOT_MODIFIED(new ArrayList<>());
                        }
                    }
                    result = mapper.insertExecute(paramMap);
                }

                if(uri.contains("update")){
                    if(uri.contains("updatePassWord")){
                        if(mapper.execute(paramMap).toString().equals("[{tag=true}]")){
                            return  AjaxResult.NOT_MODIFIED("旧密码输入错误！",new ArrayList<>());
                        }
                    }
                    result = mapper.updateExecute(paramMap);
                }

                if(uri.contains("delete")){
                    result = mapper.deleteExecute(paramMap);
                }
                session.commit();//手动提交事务

                return AjaxResult.success(result);
            }


            IPage page = buildIPage(paramMap);
            if (page != null) {
                paramMap.put("_PAGE_", page);
            }

            List<Map<String, Object>> result =  mapper.execute(paramMap);

//            if(result.toString().equals("[null]")){
//                return AjaxResult.no_content(new ArrayList<>());
//            }

            if(uri.contains("indexDengLu") && result.toString().equals("[]")){
                return  AjaxResult.NOT_MODIFIED("用户密码错误",new ArrayList<>());
            }

            if(result.toString().equals("[]")){
                return AjaxResult.no_content(new ArrayList<>());
            }



            if (page != null) {
                page.setRecords(result);
                return AjaxResult.success(page);
            }

            return AjaxResult.success(result);
        }
    }

    private IPage buildIPage(Map<String, Object> paramMap) {
        Object pageNoStr = paramMap.get("pageNo");
        Object pageSizeStr = paramMap.get("pageSize");
        if (pageNoStr != null && pageSizeStr != null) {
            int pageNo = this.pageNo;
            try{
                pageNo = Integer.parseInt(pageNoStr.toString());
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
            int pageSize = this.pageSize;
            try{
                pageSize = Integer.parseInt(pageSizeStr.toString());
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
            return new Page(pageNo,pageSize);
        }
        return null;
    }



    //上传文件相关接口
    @RequestMapping(value = "/dgwApi/upload",method = RequestMethod.POST)
    public AjaxResult uploadfile(@RequestParam("files") MultipartFile[] files)
    {
        return loadMapper.upload(files);
    }


    //下载文件相关接口
    @RequestMapping(value = "/dgwApi/downLoad",method = RequestMethod.GET)
    public AjaxResult downloadfile(@RequestParam("path") String path, HttpServletResponse response){
        return loadMapper.download(path,response);
    }


    @RequestMapping("/dgwApi/_reload")
    private boolean reload(String uri) {
        loadMapperData.load(-1);
        return true;
    }




}
