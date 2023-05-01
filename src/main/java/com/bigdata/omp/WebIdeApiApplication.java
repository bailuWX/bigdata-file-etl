package com.bigdata.omp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;
/**
 * Created by bl
 */

@EnableScheduling // 开启定时任务功能
@SpringBootApplication
@MapperScan({"com.bigdata.omp.modules.**.mapper"})
public class WebIdeApiApplication {
    public static final Logger logger = LoggerFactory.getLogger(WebIdeApiApplication.class);

    public static void main(String[] args) {
        try{
            SpringApplication.run(WebIdeApiApplication.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
