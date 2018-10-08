package com.example.demo1.demo;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;

import java.io.File;

/**
 * Created by dora on 9/16/2018.
 */
@SpringBootApplication
public class OutboundApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(OutboundApp.class)
                        .web(false)
                        .run(args);

//        SftpConfig.UploadGateway myGateway = context.getBean(SftpConfig.UploadGateway.class);
//        myGateway.upload(new File("I:/ftp-inbound/bar.txt"));
    }
}
