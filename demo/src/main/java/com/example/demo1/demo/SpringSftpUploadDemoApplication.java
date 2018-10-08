package com.example.demo1.demo;

import org.aspectj.weaver.tools.UnsupportedPointcutPrimitiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

import java.io.File;

/**
 * Created by dora on 9/12/2018.
 */
@SpringBootApplication
@IntegrationComponentScan
@EnableIntegration
public class SpringSftpUploadDemoApplication {

    @Autowired
    SftpConfig.UploadGateway uploadGateway;

    public static void main(String[] args) {
        SpringApplication.run(SpringSftpUploadDemoApplication.class, args);
//        Configurationcon
//        dTestUpload();
    }

    public void dTestUpload(){
        File file = new File("I:/ftp-inbound/bar.txt");
        uploadGateway.upload(file);
    }
}