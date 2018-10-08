package com.example.demo1.demo;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.*;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.gateway.SftpOutboundGateway;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;

/**
 * Created by dora on 8/28/2018.
 */
//@SpringBootApplication
//@IntegrationComponentScan
//@EnableIntegration
public class SftpJavaApplication {


    public static void main(String[] args) {
        /*ConfigurableApplicationContext context =
                new SpringApplicationBuilder(SftpJavaApplication.class)
                        .web(false)
                        .run(args);*/
        SpringApplication.run(SftpJavaApplication.class, args);
//        gateway.sendToSftp(new File("I:/ftp-inbound/bar.txt"));
    }

    @Bean
    public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost("10.8.8.183");
        factory.setPort(22);
        factory.setUser("nooshtest");
        factory.setPassword("nooshtest");
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<ChannelSftp.LsEntry>(factory);
    }

    @Bean
    @ServiceActivator(inputChannel = "toSftpChannel")
    public MessageHandler handler() {
        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
        handler.setRemoteDirectoryExpressionString("/home/nooshtest/noosh-integration");
//        handler.setRemoteDirectoryExpressionString("headers['remote-target-dir']");
        handler.setFileNameGenerator(new FileNameGenerator() {

            @Override
            public String generateFileName(Message<?> message) {
                if (message.getPayload() instanceof File) {
                    return ((File) message.getPayload()).getName();
                } else {
                    throw new IllegalArgumentException("File expected as payload.");
                }
            }

        });
//        return new SftpOutboundGateway(sftpSessionFactory(), "put", "'my_remote_dir/'");
        return handler;
    }

    @MessagingGateway
    public interface MyGateway {

        @Gateway(requestChannel = "toSftpChannel")
        void sendToSftp(File file);

    }
}