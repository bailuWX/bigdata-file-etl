package com.bigdata.omp.util;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;


/***
 * 数据库密码,系统密码等重要的密码,需使用本工具进行脱敏加密.再到配置文件中填写
 */
@Slf4j
public class CoreEncryptUtil {


    public static void main(String[] args) {
        BasicTextEncryptor standardPBEStringEncryptor = new BasicTextEncryptor();
        //盐值
        String salt = "bigdata";
        standardPBEStringEncryptor.setPassword(salt);
        System.out.println("您设置的盐值: " + salt);


        //加密明文,可以自行设置
        String password = "123456";
        System.out.println("您设置的明文密码: " + password);
        String encrypt = standardPBEStringEncryptor.encrypt(password);


        System.out.println("加密后的密文:"+encrypt);



        //第二次test,解密第一次test的密文
        String decrypt = standardPBEStringEncryptor.decrypt(encrypt);
        System.out.println("解密后得到的明文密码:"+decrypt);
        System.out.println("============================================");
        if (decrypt.equals(password)) {
            System.out.println("您设置的密码明文:"+password+" 经过加密再解密后得到的明文:"+decrypt+"一致!");
            System.out.println("恭喜您设置密文成功,请填写:");
            System.out.println("ENC(" + encrypt + ")");
            System.out.println("到相关配置文件中");
        }
    }
}

