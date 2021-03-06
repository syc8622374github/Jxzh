package com.bocop.zyt.jx.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bocop.zyt.R;
import com.bocop.zyt.jx.constants.Constants;

/**
 * 操作文件/uri/sharedpreference的工具类
 * 
 * @author fenfei.she
 * @version v3.0
 * @date 2014年7月30日
 */
@SuppressLint("NewApi")
public final class ContentUtils {
	// public static void showNotification(Context context, String text) {
	// Notification notification = new NotificationCompat.Builder(context)
	// .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
	// R.drawable.ic_launcher)).setSmallIcon(R.drawable.ic_launcher)
	// .setTicker("showNormal").setContentInfo("contentInfo")
	// .setContentTitle("ContentTitle").setContentText(text)
	// .setNumber(1)
	// .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
	// .build();
	//
	// NotificationManager manager = (NotificationManager)
	// context.getSystemService(Context.NOTIFICATION_SERVICE);
	// manager.notify(0, notification);
	// }
	// 动态计算listview的高度
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 判断Service是不是在运行
	 * 
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 生成二维码Bitmap
	 * 
	 * @param str
	 * @return
	 * @author shaowei.ma
	 * @date 2014年11月19日
	 */
	// public static Bitmap createQrCodeBitmap(String str) {
	// if (TextUtils.isEmpty(str)) return null;
	// try {
	// BitMatrix matrix = new MultiFormatWriter().encode(str,
	// BarcodeFormat.QR_CODE, 300, 300);
	// int width = matrix.width;
	// int height = matrix.height;
	// int[] pixels = new int[width * height];
	// for (int y = 0; y < width; ++y) {
	// for (int x = 0; x < height; ++x) {
	// if (matrix.get(x, y)) {
	// pixels[y * width + x] = 0xff000000; // black pixel
	// } else {
	// pixels[y * width + x] = 0xffffffff; // white pixel
	// }
	// }
	// }
	// Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	// bmp.setPixels(pixels, 0, width, 0, 0, width, height);
	// return bmp;
	// } catch (WriterException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	/**
	 * uri2path
	 * 
	 * @param context
	 * @param contentUri
	 * @return
	 */
	public static String uri2path(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Bitmap2File
	 * 
	 * @param bitName
	 * @param bitmap
	 * @return
	 * @throws java.io.IOException
	 */
	public static File bitmap2file(String bitName, Bitmap bitmap) throws IOException {
		File f = new File("mnt/sdcard/" + bitName + ".jpg");
		f.createNewFile();
		FileOutputStream fOut = null;
		fOut = new FileOutputStream(f);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();
		return f;
	}

	/**
	 * @param mContext
	 *            上下文，来区别哪一个activity调用的
	 * @param whichSp
	 *            使用的SharedPreferences的名字
	 * @param field
	 *            SharedPreferences的哪一个字段
	 * @return
	 */
	// 取出whichSp中field字段对应的string类型的值
	public static String getSharePreStr(Context mContext, String whichSp, String field) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		String s = sp.getString(field, "0");// 如果该字段没对应值，则取出字符串0
		return s;
	}

	// 取出whichSp中field字段对应的int类型的值
	public static int getSharePreInt(Context mContext, String whichSp, String field) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		int i = sp.getInt(field, 0);// 如果该字段没对应值，则取出0
		return i;
	}

	// 取出whichSp中field字段对应的boolean类型的值
	public static boolean getSharePreBoolean(Context mContext, String whichSp, String field) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		boolean i = sp.getBoolean(field, false);
		return i;
	}

	// 保存string类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp, String field, String value) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		sp.edit().putString(field, value).commit();
	}

	// 保存int类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp, String field, int value) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		sp.edit().putInt(field, value).commit();
	}

	public static String getStringFromSharedPreference(Context mContext, String whichSp, String field) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		String s = sp.getString(field, "");// 如果该字段没对应值，则取出空字符串
		return s;
	}

	// 保存boolen类型的value到whichSp中的field字段(主要做登陆状态)
	public static void putSharePre(Context mContext, String whichSp, String field, boolean value) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		sp.edit().putBoolean(field, value).commit();
	}

	// 删除某个表
	public static void deleteField(Context mContext, String whichSp) {
		SharedPreferences sp = mContext.getSharedPreferences(whichSp, 0);
		sp.edit().clear();
	}

	// public static void putNearByFlag(Context context, boolean flag) {
	// SharedPreferences sp =
	// context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
	// sp.edit().putBoolean(Constants.NEAR_BY, flag).commit();
	// }
	//
	// public static boolean getNearBy(Context context) {
	// SharedPreferences sp =
	// context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
	// return sp.getBoolean(Constants.NEAR_BY, true);
	// }
	// // 保持是否接收系统消息flag
	// public static void putSysMessageFlag(Context context, boolean flag) {
	// SharedPreferences sp =
	// context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
	// sp.edit().putBoolean(Constants.SYSTEM_MESSAGE, flag).commit();
	// }
	//
	// public static boolean getSysMessage(Context context) {
	// SharedPreferences sp =
	// context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
	// return sp.getBoolean(Constants.SYSTEM_MESSAGE, true);
	// }
	// //保持是否接收用户消息flag
	// public static void putUserMessageFlag(Context context, boolean flag) {
	// SharedPreferences sp = (SharedPreferences) context
	// .getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
	// sp.edit().putBoolean(Constants.USER_MESSAGE, flag).commit();
	// }
	//
	// public static boolean getUserMessage(Context context) {
	// SharedPreferences sp = (SharedPreferences) context
	// .getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
	// return sp.getBoolean(Constants.USER_MESSAGE, true);
	// }
	/**
	 * Toast的封装
	 * 
	 * @param mContext
	 *            上下文，来区别哪一个activity调用的
	 * @param msg
	 *            你希望显示的值。
	 */
	public static void showMsg(Context mContext, String msg) {
		Toast toast = new Toast(mContext);
		View view = LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null);
		TextView textView = (TextView) view.findViewById(R.id.tv_toast_content);
		textView.setText(msg);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);// 设置居中
		toast.show();// 显示,(缺了这句不显示)
	}

	// ----------------------------以下是想着的相机操作--------------------------------
	/**
	 * 选择相册图片并切割
	 */
	public static Intent pickAndCrop(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);// 向外输出uri
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 600);
		intent.putExtra("outputY", 300);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		return intent;
	}

	/**
	 * 拍照并切割
	 */
	public static void camreaAndCrop() {
		// TODO
	}

	/**
	 * 获取短信验证码
	 * 
	 * @param context
	 */
	public static void getSmsFromPhone(Context context) {
		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "body" };// "_id", "address",
														// "person",, "date",
														// "type
		String where = " address = '10690042229208' AND date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
		Cursor cur = cr.query(Uri.parse("content://sms/"), projection, where, null, "date desc");
		if (null == cur)
			return;
		if (cur.moveToNext()) {
			String number = cur.getString(cur.getColumnIndex("address"));// 手机号
			String body = cur.getString(cur.getColumnIndex("body"));
			// 这里我是要获取自己短信服务号码中的验证码~~
			Pattern pattern = Pattern.compile("(?<!\\d)\\d{6}(?!\\d)");
			Matcher matcher = pattern.matcher(body);
			if (matcher.find()) {
				String res = matcher.group();
				// .substring(1, 11);
			}
		}
		cur.close();
	}

	/**
	 * 计算view的数量
	 * 
	 * @return
	 */
	public static int viewsCount(int viewsCount) {
		int num = 0;
		if (viewsCount > 6) {
			if ((viewsCount % 6) == 0) {
				num = viewsCount / 6;
				return num;
			} else {
				num = viewsCount / 6 + 1;
				return num;
			}
		}
		return viewsCount;
	}

	/**
	 * 获取设备id/imei码
	 * 
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		if (imei == null || imei.equals("")) {
			String did = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			imei = did;
			if (did == null || did.equals("")) {
				imei = "无法获取到设备id";
			}
		}
		return imei;
	}

	/**
	 * 转换xmppmessage
	 * 
	 * @param msg
	 * @return
	 */
	// public static String convertMsg(String msg) {
	// String message = JsonUtils.convertJson(msg);
	// return message;
	// }
	/**
	 * 发送自定底义广播
	 * 
	 * @param context
	 * @param intent
	 */
	public static void sendBroast(Context context, Intent intent) {
		context.sendBroadcast(intent);
	}

	/**
	 * 获取当前的版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentVersionCode(Context context) {
		String versionCode = "";
		PackageManager manager = context.getPackageManager();
		try {
			versionCode = manager.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	// public static void showSuccessToast(Context context) {
	// Toast toast = new Toast(context);
	// View toastView = View.inflate(context, R.layout.toast_success, null);
	// toast.setGravity(Gravity.CENTER, 0, 0);
	// toast.setView(toastView);
	// toast.setDuration(Toast.LENGTH_SHORT);
	// toast.show();
	// }
	public static boolean getLoginStatus(Context context) {
		return ContentUtils.getSharePreBoolean(context, Constants.SHARED_PREFERENCE_NAME, Constants.LOGINED_IN);
	}

	public static void setLocateStatus(Context context, boolean locateStatus) {
		ContentUtils.putSharePre(context, Constants.SHARED_PREFERENCE_NAME, Constants.LOCATE_STATUS, locateStatus);
	}

	public static boolean getLocateStatus(Context context) {
		return ContentUtils.getSharePreBoolean(context, Constants.SHARED_PREFERENCE_NAME, Constants.LOCATE_STATUS);
	}

	public static void saveToken(Context context, String token) {
		// ContentUtils.putSharePre(context, Constants.SHARED_PREFERENCE_NAME,
		// Constants.TOKEN, token);
	}

	// public static String getToken(Context context) {
	// return getStringFromSharedPreference(context,
	// Constants.SHARED_PREFERENCE_NAME, Constants.TOKEN);
	// }
	public static void saveSessionId(Context context, String sessionId) {
		ContentUtils.putSharePre(context, Constants.SHARED_PREFERENCE_NAME, Constants.SESSION_ID, sessionId);
	}

	public static String getSessionId(Context context) {
		return getStringFromSharedPreference(context, Constants.SHARED_PREFERENCE_NAME, Constants.SESSION_ID);
	}

	/**
	 * 获取终端设备像素宽度
	 * 
	 * @param mContxt
	 * @return 终端设备宽度
	 */
	/*public static int getWidthPixels(Context mContxt) {
		if (mContxt == null) {
			return 0;
		}
		WindowManager localWindowManager = (WindowManager) mContxt.getSystemService("window");
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics.widthPixels;
	}*/

	/**
	 * 获取终端设备像素高度
	 * 
	 * @return 终端设备高度
	 */
	/*public static int getHeightPixels(Context mContent) {
		if (mContent == null) {
			return 0;
		}
		WindowManager localWindowManager = (WindowManager) mContent.getSystemService("window");
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics.heightPixels;
	}*/

	public static void WebViewSetting(WebView webView) {
		WebSettings ws = webView.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
		ws.setLoadWithOverviewMode(true);
		ws.setSupportZoom(true);
		ws.setDisplayZoomControls(false);
		ws.setUseWideViewPort(true);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onLoadResource(WebView view, String url) {
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});
	}

	public static BigDecimal transferToAmt(String amt_str, int scale) {
		BigDecimal amt = BigDecimal.ZERO;
		try {
			amt = new BigDecimal(amt_str);
		} catch (Throwable trh) {
		}
		return amt.setScale(scale);
	}

	public static String transferAmt(String amt_str) {
		BigDecimal amt = transferToAmt(amt_str, 2);
		return amt.toEngineeringString();
	}

	public static boolean amtIsZero(String amt_str) {
		BigDecimal amt = transferToAmt(amt_str, 2);
		return (amt.compareTo(BigDecimal.ZERO) == 0);
	}

	public static String getHttpRtnCode(JSONObject jsonObject) throws Exception {
		String zh_code = fetchRtnCode_ZH(jsonObject);
		if (zh_code != null) {
			return zh_code;
		}
		zh_code = fetchRtnCode_ZH1(jsonObject);
		if (zh_code != null) {
			return zh_code;
		}
		try {
			JSONObject bean = jsonObject.getJSONObject("ret_bean");
			if (bean == null) {
				return null;
			}
			return bean.getString("rtn_code");
		} catch (JSONException e) {
			return null;
		}
	}

	private static String fetchRtnCode_ZH(JSONObject jsonObject) throws Exception {
		String rtnCode;
		try {
			rtnCode = jsonObject.getString("rtn_code");
			if (rtnCode != null) {
				throw new Exception("Error:" + rtnCode);
			} else {
				return rtnCode;
			}
		} catch (JSONException exp) {
			return null;
		}
	}

	private static String fetchRtnCode_ZH1(JSONObject jsonObject) throws Exception {
		String rtnCode;
		try {
			rtnCode = jsonObject.getString("msgcde");
			if (rtnCode != null) {
				throw new Exception("Error:" + rtnCode);
			} else {
				return rtnCode;
			}
		} catch (JSONException exp) {
			return null;
		}
	}
}
