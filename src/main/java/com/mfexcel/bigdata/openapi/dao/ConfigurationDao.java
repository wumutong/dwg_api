package com.mfexcel.bigdata.openapi.dao;

import com.mfexcel.bigdata.openapi.domain.ConfigurationDomain;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class ConfigurationDao {

    private final SqlSession sqlSession;

    public ConfigurationDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public ConfigurationDomain selectCityById(long id) {
        return this.sqlSession.selectOne("selectConfigurationDomain", id);
    }

}
