package com.yunhuatong.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.yunhuatong.weixin.api.ApiConfig;
import com.yunhuatong.weixin.api.ApiConfigTool;

public class YunHuaTongConfig {

	private static ApiConfig apiConfig = null;
	private static Object initLock = new Object();
	
	public  void setApiConfig() throws FileNotFoundException {
		if(apiConfig == null){
			synchronized (initLock) {
				if(apiConfig == null){
					String fileName = "YunHuaTongConfig.properties";
					InputStream in =  this.getClass().getClassLoader().getResourceAsStream(fileName);
					Properties config = new Properties();
					try {
						config.load(in);
						initConfig(config);
						ApiConfigTool.setThreadLocalApiConfig(apiConfig);
						System.out.println("初始化配置文件成功");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 初始化配置文件
	 * @param config
	 */
	private  void initConfig(Properties config){
		apiConfig = new ApiConfig();
		apiConfig.setAppId(config.getProperty("appId"));
		apiConfig.setAppSecret(config.getProperty("appSecret"));
		apiConfig.setEncodingAesKey(config.getProperty("encodingAesKey"));
		if("true".equals(config.getProperty("messageEncrypt"))){
			apiConfig.setEncryptMessage(true);
		}else{
			apiConfig.setEncryptMessage(false);
		}
		apiConfig.setToken(config.getProperty("token"));
		ApiConfigTool.setThreadLocalApiConfig(apiConfig);
	}
	
	
	public  void main(String[] args) {
		try {
			YunHuaTongConfig config = new YunHuaTongConfig();
			config.setApiConfig();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(apiConfig.getAppId());
	}
}
