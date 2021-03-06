package com.bocop.zyt.bocop.jxplatform.trafficassistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.bocop.zyt.R;
import com.bocop.zyt.bocop.jxplatform.config.BocSdkConfig;
import com.bocop.zyt.jx.base.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadService extends Service {
	private static final int NOTIFY_ID = 0;
	private int progress;
	private NotificationManager mNotificationManager;
	private boolean canceled;
	// 返回的安装包url
	private String apkUrl;
	// private String apkUrl = MyApp.downloadApkUrl;
		/* 下载包安装路径 */
	private  String savePath;

	private  String saveFileName;
//	private ICallbackResult callback;
	private BaseApplication app;
	private boolean serviceIsDestroy = false;

	private Context mContext = this;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				app.setDownload(false);
				// 下载完毕
				// 取消通知֪
				mNotificationManager.cancel(NOTIFY_ID);
				installApk();
				break;
			case 2:
				app.setDownload(false);
				// 这里是用户界面手动取消，所以会经过activity的onDestroy();方法
				// 取消通知֪
				mNotificationManager.cancel(NOTIFY_ID);
				break;
			case 1:
				int rate = msg.arg1;
				app.setDownload(true);
				if (rate < 100) {
					RemoteViews contentview = mNotification.contentView;
					contentview.setTextViewText(R.id.tv_progress, rate + "%");
					contentview.setProgressBar(R.id.progressbar, 100, rate, false);
				} else {
					System.out.println("下载完毕!!!!!!!!!!!");
					// 下载完毕后变换通知形式
					mNotification.flags = Notification.FLAG_AUTO_CANCEL;
					mNotification.contentView = null;
					Intent intent = new Intent(mContext, TrafficAssistantMainActivity.class);
					// 告知已完成
					intent.putExtra("completed", "yes");
					// 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
					PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
//					mNotification.setLatestEventInfo(mContext, "下载完成", "文件已下载完毕", contentIntent);
					//
					serviceIsDestroy = true;
					stopSelf();// 停掉服务自身
				}
				mNotificationManager.notify(NOTIFY_ID, mNotification);
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("tag", "是否执行了 onBind");
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("tag", "downloadservice ondestroy");
		// 假如被销毁了，无论如何都默认取消了。
		mNotificationManager.cancel(NOTIFY_ID);
		app.setDownload(false);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("tag", "downloadservice onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.i("tag", "downloadservice onRebind");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		savePath = getSDCardPath() + "/updateApkDemo/";
		saveFileName = savePath + "AppUpdate.apk";
		Log.i("tag", "savePath:" + savePath);
		Log.i("tag", "saveFileName:" + saveFileName);
		apkUrl = intent.getStringExtra("url");
		Log.i("tag", "oldurl:" + apkUrl);
		apkUrl = BocSdkConfig.APP_URL;
//		apkUrl = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";
		Log.i("tag", "newurl:" + apkUrl);
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("tag", "onCreate");
//		binder = new DownloadBinder();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		stopForeground(true);// 这个不确定是否有作用
		app = BaseApplication.getInstance();
		new Thread(new updateRunnable()).start();//这个是下载的重点，是下载的过程  
	}

	public class updateRunnable implements Runnable{

		@Override
		public void run() {
			setUpNotification();
			new Thread() {
				public void run() {
					// 下载
					startDownload();
				};
			}.start();
		} 
		
	}
//	public class DownloadBinder extends Binder {
//		public void start() {
//			if (downLoadThread == null || !downLoadThread.isAlive()) {
//				
//				progress = 0;
//				setUpNotification();
//				new Thread() {
//					public void run() {
//						// 下载
//						startDownload();
//					};
//				}.start();
//			}
//		}
//
//		public void cancel() {
//			canceled = true;
//		}
//
//		public int getProgress() {
//			return progress;
//		}
//
//		public boolean isCanceled() {
//			return canceled;
//		}
//
//		public boolean serviceIsDestroy() {
//			return serviceIsDestroy;
//		}
//
//		public void cancelNotification() {
//			mHandler.sendEmptyMessage(2);
//		}
//
//		public void addCallback(ICallbackResult callback) {
//			DownloadService.this.callback = callback;
//		}
//	}

	private void startDownload() {
		canceled = false;
		downloadApk();
	}

	//
	Notification mNotification;

	// 通知栏
		/**
		 * 创建通知
		 */
	private void setUpNotification() {
		int icon = R.drawable.ic_china;
		CharSequence tickerText = "开始下载";
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);
		;
		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notification_layout);
		contentView.setTextViewText(R.id.name, "易汇通  正在下载...");
		// 指定个性化视图
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, TrafficAssistantMainActivity.class);
		// 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
		// 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
		// 是这么理解么。。。
		// intent.setAction(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// 指定内容意图
		mNotification.contentIntent = contentIntent;
		mNotificationManager.notify(NOTIFY_ID, mNotification);
	}

	//
	/**
	 * ����apk
	 * 
	 * @param url
	 */
	private Thread downLoadThread;

	private void downloadApk() {
		Log.i("tag", "downloadApk");
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	
	private String getSDCardPath() {  
	    File sdcardDir = null;  
	    // 判断SDCard是否存在  
	    boolean sdcardExist = Environment.getExternalStorageState().equals(  
	            Environment.MEDIA_MOUNTED);
	    if (sdcardExist) {  
	        sdcardDir = Environment.getExternalStorageDirectory();  
	    }  
	    return sdcardDir.toString();  
	}

	/**
	 * ��װapk
	 * 
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
//		callback.OnBackResult("finish");

	}

	private int lastRate = 0;
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// ���½��
					Message msg = mHandler.obtainMessage();
					msg.what = 1;
					msg.arg1 = progress;
					if (progress >= lastRate + 1) {
						mHandler.sendMessage(msg);
						lastRate = progress;
//						if (callback != null)
//							callback.OnBackResult(progress);
					}
					if (numread <= 0) {
						// �������֪ͨ��װ
						mHandler.sendEmptyMessage(0);
						// �������ˣ�cancelledҲҪ����
						canceled = true;
						break;
					}
					fos.write(buf, 0, numread);
				} while (!canceled);// ���ȡ���ֹͣ����.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

}
