package com.bigdata.omp.util;

public class SFTPUtil extends SFTPHandler {
	public static SFTPUtil sftpUtil = null;
	
	public SFTPUtil(String host, int port, String userName, String password) {
		this.host = host;
		this.port = port;
		this.userName = userName;
		this.password = password;
	}
	
	/** 
     * 获取sftp
     * @param host 
     * @param port 
     * @param userName 
     * @param password
     * @return 
     */  
    public static SFTPUtil getSFTPUtilInstance(String host, int port, String userName, String password) {
    	//多线程同步单例模式
		synchronized (SFTPUtil.class) {
			sftpUtil = new SFTPUtil(host,port,userName,password);
		}
		return  sftpUtil;
    }
}
