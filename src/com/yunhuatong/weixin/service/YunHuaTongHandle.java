package com.yunhuatong.weixin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yunhuatong.tool.MsgEncryptTool;
import com.yunhuatong.weixin.api.ApiConfig;
import com.yunhuatong.weixin.api.ApiConfigTool;
import com.yunhuatong.weixin.encrypt.WXBizMsgCrypt;
import com.yunhuatong.weixin.message.ReceiveMsgParaser;
import com.yunhuatong.weixin.message.SendMsgXmlBuilder;
import com.yunhuatong.weixin.message.receive.InImageMsg;
import com.yunhuatong.weixin.message.receive.InLinkMsg;
import com.yunhuatong.weixin.message.receive.InLocationMsg;
import com.yunhuatong.weixin.message.receive.InMsg;
import com.yunhuatong.weixin.message.receive.InTextMsg;
import com.yunhuatong.weixin.message.receive.InVideoMsg;
import com.yunhuatong.weixin.message.receive.InVoiceMsg;
import com.yunhuatong.weixin.message.receive.event.InFollowEvent;
import com.yunhuatong.weixin.message.receive.event.InLocationEvent;
import com.yunhuatong.weixin.message.receive.event.InMenuEvent;
import com.yunhuatong.weixin.message.receive.event.InQrCodeEvent;
import com.yunhuatong.weixin.message.receive.speech.InSpeechRecognitionResults;
import com.yunhuatong.weixin.message.send.OutMsg;

public class YunHuaTongHandle extends YunHuaTongFactory{
	private String xmlMsg;
	
	public YunHuaTongHandle(HttpServletRequest request,
			HttpServletResponse response){
		try {
			xmlMsg = getDoc(request);
			ApiConfig apiConfig = ApiConfigTool.getApiConfig();
			if(apiConfig.isEncryptMessage()){
				String msgSignature = request.getParameter("msg_signature");
				String timeStamp = request.getParameter("timestamp");
				String signature =  request.getParameter("signature");
//				System.out.println("signature:" + signature);
//				timeStamp = "1427768302";
				String nonce = request.getParameter("nonce");
				WXBizMsgCrypt wxCrypt = new WXBizMsgCrypt(apiConfig.getToken(), apiConfig.getEncodingAesKey(), apiConfig.getAppId());
				xmlMsg = wxCrypt.decryptMsg(signature, timeStamp, nonce, xmlMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信命令执行入口
	 */
	@Override
	public OutMsg handle() {
		OutMsg outMsg = null;
		InMsg receiveMsg = ReceiveMsgParaser.parse(xmlMsg);
		MessageHandle messageHandle = new MessageHandle(receiveMsg);
		if (receiveMsg instanceof InTextMsg)
			outMsg = messageHandle.processInTextMsg();
		else if (receiveMsg instanceof InImageMsg)
			outMsg = messageHandle.processInImageMsg();
		else if (receiveMsg instanceof InVoiceMsg)
			outMsg = messageHandle.processInVoiceMsg();
		else if (receiveMsg instanceof InVideoMsg)
			outMsg = messageHandle.processInVideoMsg();
		else if (receiveMsg instanceof InLocationMsg)
			outMsg = messageHandle.processInLocationMsg();
		else if (receiveMsg instanceof InLinkMsg)
			outMsg = messageHandle.processInLinkMsg();
		else if (receiveMsg instanceof InFollowEvent)
			outMsg = messageHandle.processInFollowEvent();
		else if (receiveMsg instanceof InQrCodeEvent)
			outMsg = messageHandle.processInQrCodeEvent();
		else if (receiveMsg instanceof InLocationEvent)
			outMsg = messageHandle.processInLocationEvent();
		else if (receiveMsg instanceof InMenuEvent)
			outMsg = messageHandle.processInMenuEvent();
		else if (receiveMsg instanceof InSpeechRecognitionResults)
			outMsg = messageHandle.processInSpeechRecognitionResults();
		else{
			System.err.println("未能识别的消息类型。 消息 xml 内容为：\n" + xmlMsg);
		}
		return outMsg;
	}
	
	/**
	 * 获取post中的xml格式数据
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String getDoc(HttpServletRequest request) throws Exception {
		char[] readerBuffer = new char[request.getContentLength()];
		BufferedReader bufferedReader = request.getReader();
		// Logger.info("开始处理上传数据");
		int portion = bufferedReader.read(readerBuffer);
		int amount = portion;
		while (amount < readerBuffer.length) {
			portion = bufferedReader.read(readerBuffer, amount,
					readerBuffer.length - amount);
			amount = amount + portion;
		}
		StringBuffer stringBuffer = new StringBuffer(
				(int) (readerBuffer.length * 1.5));
		for (int index = 0; index < readerBuffer.length; index++) {
			char c = readerBuffer[index];
			stringBuffer.append(c);
		}
		String xml = stringBuffer.toString();
		// logger.info(xml);
		return xml;
	}

	@Override
	public void send(OutMsg outMsg, HttpServletRequest request,
			HttpServletResponse response) {
		String outMsgXml = SendMsgXmlBuilder.build(outMsg);
		// 开发模式向控制台输出即将发送的 OutMsg 消息的 xml 内容
		ApiConfig apiConfig = ApiConfigTool.getApiConfig();
		if (ApiConfigTool.isDevMode()) {
			System.out.println("发送消息:");
			System.out.println(outMsgXml);
			System.out.println("--------------------------------------------------------------------------------\n");
		}
		if(apiConfig.isEncryptMessage()){
			String timeStamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			outMsgXml = MsgEncryptTool.encrypt(outMsgXml, timeStamp, nonce);
		}
		PrintWriter writer = null;
		response.setHeader("Pragma", "no-cache"); 
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.write(outMsgXml);
		writer.flush();
	}
	
}
