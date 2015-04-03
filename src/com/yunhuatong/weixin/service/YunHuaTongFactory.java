package com.yunhuatong.weixin.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yunhuatong.config.YunHuaTongConfig;
import com.yunhuatong.weixin.message.send.OutMsg;

public abstract class YunHuaTongFactory {
	private static YunHuaTongFactory factory = null;
	private static Object initLock = new Object();
//	private static String className = "com.yunhuatong.weixin.service.YunHuaTongHandle";
	
	public static YunHuaTongFactory getInstance(HttpServletRequest request,
			HttpServletResponse response){
		if(factory == null){
			synchronized (initLock) {
				if(factory == null){
					try {
//						Class c = Class.forName(className) ;
						//初始化配置文件
//						factory = (YunHuaTongHandle) c.newInstance();
						factory = new YunHuaTongHandle(request, response);
					} catch (Exception e) {
						 System.err.println("Failed to load YunHuaTongMsgFactory class. YunHuaTong cannot function normally.");
		                        e.printStackTrace();
		                        return null;
					}
				}
			}
		}
		return factory;
	}
	
	public abstract OutMsg handle();
	
	public abstract void send(OutMsg outMsg, HttpServletRequest request,
			HttpServletResponse response);
}
