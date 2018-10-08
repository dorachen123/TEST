package com.example.demo1.demo;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.util.Vector;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by zhangcheng on 2018/5/8.
 */
@Component
@ConfigurationProperties
public class SftpUtil {
    private static final String CLZ_NAME = SftpUtil.class.getSimpleName() + ".";

    private ChannelSftp sftp;

    private Session session;
    /**
     * SFTP 登录用户名
     */
    private String username;
    /**
     * SFTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private String host;
    /**
     * SFTP 端口
     */
    private int port;


    /**
     * 构造基于密码认证的sftp对象
     */
    public SftpUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SftpUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SftpUtil() {
    }


    /**
     * 连接sftp服务器
     */
    public void login() throws Exception {
        JSch jsch = new JSch();
        if (privateKey != null) {
            jsch.addIdentity(privateKey);// 设置私钥
        }
//
        session = jsch.getSession(username, host, port);

        if (password != null) {
            session.setPassword(password);
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //TODO 移除kerberos验证方法
        config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");

        session.setConfig(config);
        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        sftp = (ChannelSftp) channel;

    }

    /**
     * 关闭连接 server
     */
    public void logout() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param path     上传到该目录
     * @param fileName sftp端文件名
     * @param input    输入流
     */
    public void upload(String path, String fileName, InputStream input) throws SftpException {
        try {
            sftp.cd(path);
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String[] dirs = path.split("/");
            for (String dir : dirs) {
                if (null == dir || "".equals(dir)) continue;
                try {
                    sftp.cd(dir);
                } catch (SftpException ex) {
                    sftp.mkdir(dir);
                    sftp.cd(dir);
                }
            }
        }
        sftp.put(input, fileName);  //上传文件
    }


    /**
     * 下载文件。
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException {
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }


    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }


    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @param
     */
    public Vector<?> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    //上传文件测试
    public static void main(String[] args) throws Exception {
        SftpUtil sftp = new SftpUtil("nooshtest", "nooshtest", "10.8.8.183", 22);
        sftp.login();
        File file = new File("I:\\ftp-inbound\\ftp1.txt");
        InputStream is = new FileInputStream(file);

//        sftp.upload("/home/nooshtest/noosh-integration", "test_sftp1.txt", is);
//        sftp.delete("/home/nooshtest/noosh-integration", "test_sftp1.txt");
        Vector vv = sftp.listFiles("/home/nooshtest/noosh-integration");

        if(vv!=null){
            for(int ii=0; ii<vv.size(); ii++){
                System.out.println(vv.elementAt(ii).toString());

//                Object obj=vv.elementAt(ii);
//                if(obj instanceof com.jcraft.jsch.ChannelSftp.LsEntry){
//                    System.out.println(((com.jcraft.jsch.ChannelSftp.LsEntry)obj).getLongname());
//                }

            }
        }
        is.close();
        sftp.logout();
    }
}
