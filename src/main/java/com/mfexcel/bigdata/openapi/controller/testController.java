package com.mfexcel.bigdata.openapi.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/*")
@RestController
public class testController {

//    @Value("${defalt.page.no:1}")
//    private int pageNo;
//    @Value("${defalt.page.size:10}")
//    private int pageSize;
//
//    @Autowired
//    private BaseMapper baseMapper;
//
//    @Autowired
//    private LoadMapperData loadMapperData;
//
//
//    @RequestMapping("/_reload")
//    private boolean reload(String uri) {
//        loadMapperData.load(-1);
//        return true;
//    }
//
//
//    @Autowired
//    private DgwCategoryMsgConfigMapper dgwCategoryMsgConfigMapper;
//
//    @PostMapping
//    public int insert(DgwCategoryMsgConfigDomain dgwCategoryMsgConfig) {
//        return dgwCategoryMsgConfigMapper.insertDgwCategoryMsgConfig(dgwCategoryMsgConfig);
//    }

}
