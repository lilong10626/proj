package com.yunhuatong.tool;

import java.util.Arrays;

import com.yunhuatong.weixin.api.ApiConfigTool;

/**
 * 测试用的账号：
 * appID = wx9803d1188fa5fbda
 * appsecret = db859c968763c582794e7c3d003c3d87
 * url = http://www.jfinal.com/weixin
 * token = __my__token__
 */
public class SignatureCheckTool {
	
	public static final SignatureCheckTool me = new SignatureCheckTool();
	
	/**
	 * php 示例
	 *  $signature = $_GET["signature"];
        $timestamp = $_GET["timestamp"];
        $nonce = $_GET["nonce"];	
        		
		$token = TOKEN;
		$tmpArr = array($token, $timestamp, $nonce);
		sort($tmpArr, SORT_STRING);
		$tmpStr = implode( $tmpArr );
		$tmpStr = sha1( $tmpStr );
		
		if( $tmpStr == $signature ){
			return true;
		}else{
			return false;
		}
	 * @return
	 */
	public boolean checkSignature(String signature, String timestamp, String nonce) {
		String TOKEN = ApiConfigTool.getApiConfig().getToken();
		String array[] = {TOKEN, timestamp, nonce};
		Arrays.sort(array);
		String tempStr = new StringBuilder().append(array[0] + array[1] + array[2]).toString();
		tempStr = EncryptionTool.sha1Encrypt(tempStr);
		return tempStr.equalsIgnoreCase(signature);
	}
	
//	public boolean checkSignature(Controller c) {
//        return checkSignature(c.getPara("signature"), c.getPara("timestamp"), c.getPara("nonce"));
//	}
}



