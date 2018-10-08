package com.example.demo1.demo;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;


@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);//.web(false)//.run(args);
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
	public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
		SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
		fileSynchronizer.setDeleteRemoteFiles(false);
		fileSynchronizer.setRemoteDirectory("/home/nooshtest/noosh-integration");
		fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.txt"));
		return fileSynchronizer;
	}

	@Bean
	@InboundChannelAdapter(channel = "sftpChannel", poller = @Poller(fixedDelay = "5000"))
	public MessageSource<File> sftpMessageSource() {
		SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(
				sftpInboundFileSynchronizer());
//		source.setLocalDirectory(new File("ftp-inbound"));
		source.setLocalDirectory(new File("I:\\ftp-inbound2"));
		source.setAutoCreateLocalDirectory(true);
		source.setLocalFilter(new AcceptOnceFileListFilter<File>());
		return source;
	}

	@Bean
	@ServiceActivator(inputChannel = "sftpChannel")
	public MessageHandler handler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println(message.getPayload());
				System.out.println("testing");
			}

		};
	}

}