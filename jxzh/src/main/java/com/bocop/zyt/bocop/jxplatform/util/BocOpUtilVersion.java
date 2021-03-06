package com.bocop.zyt.bocop.jxplatform.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bocop.zyt.R;
import com.bocop.zyt.bocop.jxplatform.config.BocSdkConfig;
import com.bocop.zyt.jx.base.BaseApplication;
import com.bocop.zyt.jx.baseUtil.asynchttpclient.AsyncHttpClient;
import com.bocop.zyt.jx.baseUtil.asynchttpclient.AsyncHttpResponseHandler;
import com.bocop.zyt.jx.baseUtil.asynchttpclient.RequestHandle;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author luoyang
 * @version 创建时间：2015-6-23 下午5:46:43 分行CSP通用接口类，需要验证
 */

@SuppressLint("SimpleDateFormat")
public class BocOpUtilVersion implements LoginUtil.ILoginListener {
	
	String msgcode;
	 String rtnmsg;

	/**
	 * 用户信息
	 */
	public BaseApplication baseApplication = BaseApplication.getInstance();

	/*
	 * http报文头信息
	 */
	public Context mContext;

	public BocOpUtilVersion(Context context) {
		this.mContext = context;
	}

	@SuppressLint("SimpleDateFormat")
	public Header[] getHeaders() {
		Header clientid = new BasicHeader("clentid", BocSdkConfig.CONSUMER_KEY);
		Header userid = new BasicHeader("userid", LoginUtil.getUserId(mContext));
		Header action = new BasicHeader("acton", LoginUtil.getToken(mContext));
		Header type = new BasicHeader("type", "1");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMDD");
		// 获取当前时间
		String nowData = format.format(new Date(System.currentTimeMillis()));
		Header trandt = new BasicHeader("trandt", nowData);

		SimpleDateFormat formatTime = new SimpleDateFormat("HHmmss");
		// 获取当前时间
		String nowTime = formatTime
				.format(new Date(System.currentTimeMillis()));

		Header trantm = new BasicHeader("trantm", nowTime);
		return new Header[] { clientid, type, userid, action, trandt, trantm };
	}

	/**
	 * 
	 * @param context
	 *            :上下文
	 * @param url
	 *            ：中银开放平台CSP通用接口地址(需要验证)
	 * @param mcis
	 *            ：开放平台报文
	 * @param myCallback
	 *            ：回调
	 */

	public void postOpboc(String strGson,String strUrl, CallBackBoc callBack) {

		if (baseApplication.isNetStat()) {
			Log.i("tag", BocSdkConfig.HTTP_VERSION + strUrl);
			post(BocSdkConfig.HTTP_VERSION + strUrl, getHeaders(), strGson, callBack);
		} else {
			CustomProgressDialog.showBocNetworkSetDialog(mContext);
		}
	}

	/**
	 * 
	 * @param context
	 *            :上下文
	 * @param url
	 *            ：中银开放平台CSP通用接口地址
	 * @param mcis
	 *            ：开放平台报文
	 * @param myCallback
	 *            ：回调 罗阳：于20150703封装加载框，并监听DIALOG的取消时间
	 */
	private RequestHandle post(String url, Header[] headers, String strGson,
							   final CallBackBoc callBack) {
		final String contentType = "application/json; charset=UTF-8";
		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-Type", contentType);
		client.addHeader("Cache-Control", "no-cache");
		client.addHeader("Accept-Charset", "UTF-8");
		client.setTimeout(30 * 1000);
		final RequestHandle handle;
		StringEntity strEntity = null;
		try {
			strEntity = new StringEntity(strGson);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		handle = client.post(mContext, url, headers, strEntity, contentType,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						Log.i("tag", "onStart");
						callBack.onStart();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						String strResponseBody = null;
						Log.i("tag", "onSuccess:" + String.valueOf(statusCode));
						try {
							strResponseBody = new String(responseBody, "UTF-8");
//							Log.i("tag","APP的公共报头:"+URLDecoder.decode(headers[0].getValue(), "UTF-8") + "|||"+URLDecoder.decode(headers[1].getValue(), "UTF-8") + "|||"+URLDecoder.decode(headers[2].getValue(), "UTF-8") + "|||" +URLDecoder.decode(headers[3].getValue(), "UTF-8") + "|||" + URLDecoder.decode(headers[4].getValue(), "UTF-8") + "|||" + URLDecoder.decode(headers[5].getValue(), "UTF-8"));	
							Log.i("tag", "onSuccess:" + new String(responseBody,"UTF-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						if(statusCode == 200){
							try {
								strResponseBody = new String(responseBody,"UTF-8");
								if(strResponseBody.contains("appurl")){
									callBack.onSuccess(strResponseBody);
								}
								else{
									callBack.onFailure(strResponseBody);
								}
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							try {
//								msgcode = URLDecoder.decode(headers[3].getValue(), "UTF-8");
//								rtnmsg = URLDecoder.decode(headers[4].getValue(), "UTF-8");
//								if(msgcode.equals("0000000")){
//								callBack.onSuccess(new String(responseBody,"UTF-8"));
//								}
//								else{
//									Log.i("tag", "BocOpUtil call onFailure msgcde ");
//									BocRecComBodyBean bocRecComBodyBean = null;
//									try {
//										bocRecComBodyBean = JsonUtils.getObject(strResponseBody, BocRecComBodyBean.class);
//										if(bocRecComBodyBean.getMsgcde().equals("invalid_token")){
//											Toast.makeText(mContext, "请重新登陆", Toast.LENGTH_LONG).show();
//											LoginUtil.authorize(mContext, BocOpUtilVersion.this);
//										}else{
//											Toast.makeText(mContext, bocRecComBodyBean.getRtnmsg(), Toast.LENGTH_LONG).show();
//										}
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//							} catch (UnsupportedEncodingException e) {
//								callBack.onFailure(e.getMessage().toString());
//							}
						}
						else{
							callBack.onFailure(String.valueOf(statusCode));
						}
						
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						try {
							Log.i("tag", "onFailure");
							if(statusCode == 0){
								Toast.makeText(mContext, R.string.onFailure, Toast.LENGTH_SHORT).show();
							}
							String strError = null;
							Log.i("tag", "onFailure statusCode:" + String.valueOf(statusCode));
							if(responseBody != null){
								strError = new String(responseBody, "UTF-8");
								callBack.onFailure(String.valueOf(statusCode) + " "
										+ strError);
							}else{
								callBack.onFailure(String.valueOf(statusCode));
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFinish() {
						super.onFinish();
						callBack.onFinish();
					}

				});
		return handle;
	}

	public interface CallBackBoc {
		public void onStart();
		
		public void onSuccess(String responStr);

		public void onFailure(String responStr);

		public void onFinish();
	}

	@Override
	public void onLogin() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "登陆成功", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCancle() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "请重新登陆，登录后才能办理相关业务", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "登陆错误", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onException() {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, "登陆异常", Toast.LENGTH_LONG).show();
	}

}
