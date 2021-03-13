package com.mfexcel.bigdata.openapi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mfexcel.bigdata.openapi.cache.SessionFactoryCache;
import com.mfexcel.bigdata.openapi.load.LoadMapperData;
import com.mfexcel.bigdata.openapi.mapper.BaseMapper;
import com.mfexcel.bigdata.openapi.utils.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/*")
@RestController
public class BaseController {

    @Value("${defalt.page.no:1}")
    private int pageNo;
    @Value("${defalt.page.size:10}")
    private int pageSize;

    @Autowired
    private BaseMapper baseMapper;

    @Autowired
    private LoadMapperData loadMapperData;



//    public class CrosInterceptor implements HandlerInterceptor {
//        @Override
//        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
//                                 Object handler) throws Exception {
//            if(request.getMethod().equals(RequestMethod.OPTIONS.name())) {
//                response.setStatus(HttpStatus.OK.value());
//                return false;
//            }
//            return true;
//        }
//        @Override
//        public void postHandle(HttpServletRequest request, HttpServletResponse response,
//                               Object handler, ModelAndView modelAndView) throws Exception {
//        }
//        @Override
//        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
//                                    Object handler, Exception ex) throws Exception {
//        }
//    }

//    @RequestMapping("/handle")
//    public ResponseEntity<Void> handle(){
//
//        ResponseEntity.BodyBuilder builder = (ResponseEntity.BodyBuilder) ResponseEntity.ok();
//        return builder.body("204");
//
//        return ResponseEntity.noContent().build();
//    }
//    @ResponseBody
//    @RequestMapping(value = {"/*"}, method = RequestMethod.OPTIONS)
//    public int options(HttpServletResponse resp,HttpServletRequest request){
//        setCORS(resp,request);
//        return 204;
//    }
//
//    private void setCORS(HttpServletResponse resp,HttpServletRequest request){
//        resp.setStatus(204);
//        resp.setHeader("Access-Control-Allow-Origin", request.getHeader("*"));
//        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
//        resp.setHeader("Access-Control-Allow-Headers", "content-type");
//        resp.setHeader("Access-Control-Allow-Credentials","true");
//    }

//    @RequestMapping(value = "/*",method = RequestMethod.OPTIONS)
//    public AjaxResult handleOptions(HttpServletRequest request, @RequestBody Map<String, Object> paramMap) throws  Throwable{
//        ResponseEntity.ok();
//        return new BaseController().handler(request,paramMap);
//    }

//    @RequestMapping(value = "/*",method = {RequestMethod.OPTIONS,RequestMethod.POST})


@PostMapping
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

            if(uri.contains("insert")){
                int result = mapper.insertExecute(paramMap);
                session.commit();//手动提交事务
                return AjaxResult.success(result);
            }

            IPage page = buildIPage(paramMap);
            if (page != null) {
                paramMap.put("_PAGE_", page);
            }

            List<Map<String, Object>> result =  mapper.execute(paramMap);
            if(result.toString().equals("[null]")){
                return AjaxResult.success(new ArrayList<>());
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

    @RequestMapping("/_reload")
    private boolean reload(String uri) {
        loadMapperData.load(-1);
        return true;
    }


}
