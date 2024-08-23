package com.yupi.springbootinit;

import com.yupi.springbootinit.config.WxOpenConfig;
import javax.annotation.Resource;

import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {


    @Resource
    private ChartService chartService;
    @Test
    void contextLoads() {
        Chart byId = chartService.getById(1774039166049337346L);
        if (byId==null){
            System.out.println("weikong");
            return;
        }
        String chartData = byId.getGenChart();
        System.out.println(chartData);
    }

}
