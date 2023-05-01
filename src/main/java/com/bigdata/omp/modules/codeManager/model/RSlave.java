package com.bigdata.omp.modules.codeManager.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 子服务总表,主要存储子服务的服务器各种信息
 */
@Table(name = "r_slave")
@Data
public class RSlave {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idSlave;

    @Column(name = "NAME")
    private String name;

    @Column(name = "HOST_NAME")
    private String hostName;

    @Column(name = "PORT")
    private String port;

    @Column(name = "USERNAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PROXY_HOST_NAME")
    private String proxyHostName;

    @Column(name = "PROXY_PORT")
    private String proxyPort;


    @Column(name = "NON_PROXY_HOSTS")
    private String nonProxyHosts;


    @Column(name = "MASTER")
    private String master;


    @Column(name = "ID_SYSTEM_DEF")
    private String idSystemDef;


    @Column(name = "HOST_USER")
    private String hostUser;


    @Column(name = "HOST_PASSWORD")
    private String hostPassword;

    @Column(name = "HOST_FTP_PORT")
    private String hostFtpPort;


    @Column(name = "HOST_FILE_PATH")
    private String hostFilePath;

    @Column(name = "HOST_START_SCRIPT")
    private String hostStartScript;

    @Column(name = "ID_CLASS")
    private String idClass;
}
