package com.yunhuatong.weixin.service;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.yunhuatong.weixin.api.ApiConfig;
import com.yunhuatong.weixin.api.ApiConfigTool;
import com.yunhuatong.weixin.encrypt.AesException;
import com.yunhuatong.weixin.encrypt.SHA1;
import com.yunhuatong.weixin.encrypt.WXBizMsgCrypt;
import com.yunhuatong.weixin.message.receive.InMsg;
import com.yunhuatong.weixin.message.receive.InTextMsg;
import com.yunhuatong.weixin.message.send.OutMsg;
import com.yunhuatong.weixin.message.send.OutNewsMsg;
import com.yunhuatong.weixin.message.send.OutTextMsg;

public class MessageHandle {
	
	private static final String helpStr = "帮助信息";
	private InMsg inMsg;
	private OutMsg outMsg;
	
	public MessageHandle(InMsg inMsg){
		this.inMsg = inMsg;
	}
	

	private OutMsg renderOutTextMsg(String content) {
		outMsg= new OutTextMsg(inMsg);
		((OutTextMsg)outMsg).setContent(content);
		return outMsg;
	}

	
	protected OutMsg processInTextMsg() {
		String msgContent = ((InTextMsg)inMsg).getContent().trim();
		// 帮助提示
		if ("help".equalsIgnoreCase(msgContent)) {
			outMsg = new OutTextMsg(inMsg);
			((OutTextMsg)outMsg).setContent(helpStr);
		}
		// 图文消息测试
		else {
			outMsg = new OutNewsMsg(inMsg);
			String openId = inMsg.getFromUserName();
			String data = "user=" + openId;
			ApiConfig config = ApiConfigTool.getApiConfig();
			try {
				WXBizMsgCrypt wxmc = new WXBizMsgCrypt(config.getToken(), config.getEncodingAesKey(), config.getAppId());
				data = wxmc.encrypt(wxmc.getRandomStr(), data);
			} catch (AesException e) {
				System.out.println("回复消息加密失败");
				e.printStackTrace();
			}
			((OutNewsMsg)outMsg).addNews("云话通", "现在就加入云话通^_^", "https://mp.weixin.qq.com/misc/getqrcode?fakeid=3018377213&token=297641223&style=1", "http://yunhuaben.nat123.net/index?data = " + data);
		}
		return outMsg;
	}
	
	
	/**
	 * 处理关注/取消关注消息
	 */
	protected OutMsg processInFollowEvent() {
		((OutTextMsg)outMsg).setContent("感谢关注 JFinal Weixin 极速开发，为您节约更多时间，去陪恋人、家人和朋友 :) \n\n\n " + helpStr);
		// 如果为取消关注事件，将无法接收到传回的信息
		return outMsg;
	}


	/**
	 * 图片信息处理
	 * Description: 自己填写
	 * @return
	 */
	protected OutMsg processInImageMsg() {
		return renderOutTextMsg("");
	}

	/**
	 * 语音信息处理
	 * @return
	 */
	public OutMsg processInVoiceMsg() {
		return renderOutTextMsg("");
	}


	/**
	 * 图片信息处理
	 * @return
	 */
	public OutMsg processInVideoMsg() {
		return renderOutTextMsg("");
	}


	/**
	 * 地理位置信息处理
	 * @return
	 */
	public OutMsg processInLocationMsg() {
		return renderOutTextMsg("");
	}


	/**
	 * 连接信息处理
	 * Description: 自己填写
	 * @return
	 */
	public OutMsg processInLinkMsg() {
		return renderOutTextMsg("");
	}


	public OutMsg processInQrCodeEvent() {
		return renderOutTextMsg("");
	}


	public OutMsg processInLocationEvent() {
		return renderOutTextMsg("");
	}


	public OutMsg processInMenuEvent() {
		return renderOutTextMsg("");
	}


	public OutMsg processInSpeechRecognitionResults() {
		return renderOutTextMsg("");
	}
	
	public static void main(String[] args) throws Exception{
		String token = "weixinYHT";
		String encodingAesKey = "hzklS00gcN6ScA6aPk531WbUf7R4kASnmP7VH1ShkJf";
		String appId = "gh_e2e84f992d2e";
		String encryptedMsg = "<xml><Encrypt><![CDATA[9WMQm2y5uva0VKg4sAmqqwmQX83e+/2qfisS3pJLs31GpbaTU1VUcbo4msslcoE9N5k+alpcktfWe6KvsRSjb1uYfFXCeAkjVQANe0Mr7LFdTrfNNWGC/JOnKYaK4AV93OmgrDi0HVxtuAg+WxM7zCBSWvdLjzmjMB5ICU2XuLOWNktACEfTx2Dj0SA087G4yH/2L5dGAL7zArP4Qg9mTB/wt9ETgoBIrdNyPvKdFkhkXXOHD0K1GGRk7485NzLC7W1iGdRWVxRClyQiwwtmxVIxNNKxuCEVHrSGpYxpJbINmNmAq038GB3EYnT6Nw4gD2NhaekHNjEyP0r7ApFx8yrEefMb3W+0juhBrgfb5xGaFyWDcWx0htSiDoD+Dzmp45lcL2GdrDiklKgBeHHSx7PKbZF9CfYP/iH1N28BgHLiFwi2r5k9jfzM2gQXpEV9I/VOYGFd7qpeWrXfae1gTC/c8m4ftpqIiShXWNmKR3hao1GCLydxo+fIyj7UN4cKyU4GhvF27Yd7Q4HSLb724WlDJnKEK44R3e4Q1k43p+TSlTpBOVK0prwwffz6Ef6LzSJgMriYD8HPvsgWlBppus1b4zuKso17UypkiFw9hRvftTiG0mHnRz/WileX0lmqeomsEDkWWKlQeGirgZd5MUBv4JwoxG9de6G2GIBpg+0FQGX/zTBnQHSc14PPF9t/EJGzFfQitYss77py4d1myOt4n6UqYNgX2XaK9ykfdVaL7NGAfXjwUbWc2ooM0SDMFNZTEkIlfmdFi60RJp43qYhz2u7MOuGzfFYz04MWg/5QeL6jOxlaS9rfv8kSYkwRwJ9qwvfDIOxga0YPEMQTV4F+8mKuaaK1vwLHiyrMq+mheiZ5f2BSoC3opOxNgvTi]]>" + 
				"</Encrypt><MsgSignature><![CDATA[ff57afca3468461d28f26796d11c40125b063c23]]>" + 
				"</MsgSignature><TimeStamp>1427811287</TimeStamp><Nonce><![CDATA[660069834]]>" +
				"</Nonce></xml>";
		WXBizMsgCrypt wxmc = new WXBizMsgCrypt(token, encodingAesKey, appId);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		StringReader sr = new StringReader(encryptedMsg);
		InputSource is = new InputSource(sr);
		Document document = db.parse(is);
		
		Element root = document.getDocumentElement();
		NodeList nodelist1 = root.getElementsByTagName("Encrypt");
		NodeList nodelist2 = root.getElementsByTagName("MsgSignature");
		NodeList nodelist3 = root.getElementsByTagName("TimeStamp");
		NodeList nodelist4 = root.getElementsByTagName("Nonce");
		
		String encrypt = nodelist1.item(0).getTextContent();
		String msgSignature = nodelist2.item(0).getTextContent();
		String timeStamp = nodelist3.item(0).getTextContent();
		String nonce = nodelist4.item(0).getTextContent();
		
//		String data = wxmc.decryptMsg(msgSignature, timeStamp, nonce, encrypt);
		String data = SHA1.getSHA1(token, timeStamp, nonce, encrypt);
		System.out.println(msgSignature);
		System.out.println(data);
//		String data = "user=123456";
//		System.out.println(data);
//		data = wxmc.encrypt(wxmc.getRandomStr(), data);
//		System.out.println(data);
//		data = wxmc.decrypt(data);
//		System.out.println(data);
	}
}
