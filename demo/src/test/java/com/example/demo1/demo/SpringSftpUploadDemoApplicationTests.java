package com.example.demo1.demo;

import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dora on 9/12/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "sftp.port = 10022" })
public class SpringSftpUploadDemoApplicationTests {

    @Autowired
    private SftpConfig.UploadGateway gateway;

    private static EmbeddedSftpServer server;

    private static Path sftpFolder;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new EmbeddedSftpServer();
        server.setPort(10022);
        sftpFolder = Files.createTempDirectory("SFTP_UPLOAD_TEST");
        server.afterPropertiesSet();
        server.setHomeFolder(sftpFolder);
        // Starting SFTP
        if (!server.isRunning()) {
            server.start();
        }
    }

    @org.junit.Before
    @org.junit.After
    public void cleanSftpFolder() throws IOException {
        Files.walk(sftpFolder).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
    }

    @Test
    public void testUpload() throws IOException {
        // Prepare phase
        Path tempFile = Files.createTempFile("UPLOAD_TEST", ".csv");

        // Prerequisites
        Assert.assertEquals(0, Files.list(sftpFolder).count());

        // test phase
        gateway.upload(tempFile.toFile());

        // Validation phase
        List<Path> paths = Files.list(sftpFolder).collect(Collectors.toList());
        Assert.assertEquals(1, paths.size());
        Assert.assertEquals(tempFile.getFileName(), paths.get(0).getFileName());
    }

    @AfterClass
    public static void stopServer() {
        if (server.isRunning()) {
            server.stop();
        }
    }
}