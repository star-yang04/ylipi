package com.example.ylipi;

import com.example.ylipi.entity.Appeals;
import com.example.ylipi.mapper.AppealsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 功能
 * 作者：yangxin
 * 日期：2025/4/30 00:22
 */
@SpringBootTest
public class Appealtest {

    @Autowired
    private AppealsMapper appealsMapper;

    @Test
    void selectAppealsById() {
        Appeals appeal = appealsMapper.selectById(1);
        System.out.println("受影响的行数="+appeal.getContent());
    }




}
