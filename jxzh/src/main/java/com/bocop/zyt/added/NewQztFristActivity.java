package com.bocop.zyt.added;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bocop.zyt.R;
import com.bocop.zyt.bocop.jxplatform.activity.WebActivity;
import com.bocop.zyt.bocop.jxplatform.bean.Advertisement;
import com.bocop.zyt.bocop.jxplatform.bean.SchoolBean;
import com.bocop.zyt.bocop.jxplatform.config.BocSdkConfig;
import com.bocop.zyt.bocop.jxplatform.util.CustomProgressDialog;
import com.bocop.zyt.bocop.jxplatform.util.LoginUtil;
import com.bocop.zyt.bocop.jxplatform.view.MyGridView;
import com.bocop.zyt.bocop.qzt.QztApplyActivity;
import com.bocop.zyt.bocop.qzt.QztOrderActivity;
import com.bocop.zyt.jx.ab.view.sliding.AbSlidingPlayView;
import com.bocop.zyt.jx.base.BaseActivity;
import com.bocop.zyt.jx.base.BaseApplication;
import com.bocop.zyt.jx.common.util.AbImageUtil;
import com.bocop.zyt.jx.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 签证通首页
 * 
 * @author luoyang
 * 
 */
public class NewQztFristActivity extends BaseActivity implements LoginUtil.ILoginListener {
	// txm
	private ArrayList<SchoolBean> fristList = new ArrayList<SchoolBean>();
	private FristAdaper adaper;
	private GridView gridview_allmodule;
	private TextView tv_titleName;
	private ImageView iv_title_left;
	protected BaseActivity baseActivity;
	private List<Advertisement> mAdvList = new ArrayList<Advertisement>();
	public BaseApplication baseApplication = BaseApplication.getInstance();

	// BaseApplication application = (BaseApplication) baseActivity
	// .getApplication();
	/**
	 * 集成图片轮播
	 */
	AbSlidingPlayView pv_playview;
	static final int ASPECT_X = 4, ASPECT_Y = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newqztfrist);
		initView();
		setListener();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		baseActivity = (BaseActivity) NewQztFristActivity.this;
		tv_titleName = (TextView) findViewById(R.id.tv_titleName);
		tv_titleName.setText("签证通");
		iv_title_left = (ImageView) findViewById(R.id.iv_title_left);
		gridview_allmodule = (MyGridView) findViewById(R.id.gridview_allmodule);
		pv_playview = (AbSlidingPlayView) findViewById(R.id.spv_photos);
		initHeaderView();

		addSchoolBean();
		adaper = new FristAdaper();
		gridview_allmodule.setAdapter(adaper);
	}

	private void initHeaderView() {
		pv_playview.setNavHorizontalGravity(Gravity.RIGHT);
		Drawable iv_playviewindex_off = getResources().getDrawable(
				R.drawable.iv_playviewindex_off);
		Drawable iv_playviewindex_on = getResources().getDrawable(
				R.drawable.iv_playviewindex_on);
		pv_playview.setPageLineImage(
				AbImageUtil.drawableToBitmap(iv_playviewindex_on),
				AbImageUtil.drawableToBitmap(iv_playviewindex_off));
		initPlayViewSize();
		initPlayViewContent();
		pv_playview.setPlayDuration(3000);
	}

	private void initPlayViewSize() {
		final ViewTreeObserver vto = pv_playview.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			boolean hasSetted = false;

			@Override
			public void onGlobalLayout() {
				if (hasSetted)
					return;
				hasSetted = true;
				LayoutParams params = pv_playview.getLayoutParams();
				int w = pv_playview.getMeasuredWidth();
				int h = w / ASPECT_X * ASPECT_Y;
				// 宽度放开的话就会根据宽高比进行适配
				// params.height = h;
				pv_playview.setLayoutParams(params);
				try {
					vto.removeGlobalOnLayoutListener(this);
				} catch (Exception e) {
				}
			}
		});
	}

	private void initPlayViewContent() {
		getAdPhotos();
	}

	void getAdPhotos() {
		pv_playview.removeAllViews();
		// 第一张
		Advertisement adv1 = new Advertisement();
		adv1.setImageRes(R.drawable.visarollimg1);
		// adv1.setContent("http://mp.weixin.qq.com/s?__biz=MjM5NDg0NzIzNA==&mid=401756730&idx=6&sn=ae322c5365773a4abf94f0a90dc5df60&scene=1&srcid=0115ZGwEkPDOdR1gKIke6zUw#rd");
		// // 网页url
		mAdvList.add(adv1);
		// 第二张
		Advertisement adv2 = new Advertisement();
		adv2.setImageRes(R.drawable.visarollimg2);
		// adv2.setContent("http://mp.weixin.qq.com/s?__biz=MjM5NDg0NzIzNA==&mid=401756730&idx=5&sn=2169aa0f2751322e4d4874b7f3144929&scene=1&srcid=0115QUo3iYtIvU2q9TpYiX8b#rd");
		mAdvList.add(adv2);
		// 第三张
		Advertisement adv3 = new Advertisement();
		adv3.setImageRes(R.drawable.visarollimg3);
		// adv3.setContent("http://mp.weixin.qq.com/s?__biz=MjM5NDg0NzIzNA==&mid=401756730&idx=3&sn=32df249221afd8aa3d2d0de8be8f9792&scene=1&srcid=01158TgGCW2ZeSHQNKtiXMzw#rd");
		mAdvList.add(adv3);
		// 易商app
		// Advertisement adv4 = new Advertisement();
		// adv4.setContent("http://mp.weixin.qq.com/s?__biz=MjM5NDg0NzIzNA==&mid=401756730&idx=4&sn=3060be3f4ed8c4fcb4b6803cb2e2d08b&scene=1&srcid=0115bPM13VwmciqlqsePCPbT#rd");
		// adv4.setImageRes(R.drawable.cyh_adv4);
		// mAdvList.add(adv4);
		if (mAdvList != null && mAdvList.size() > 1) {// 多张图片
			// pv_playview.clear();
			for (Advertisement advertisement : mAdvList) {
				View view = getLayoutInflater().inflate(R.layout.item_adpic,
						null);
				ImageView iv = (ImageView) view.findViewById(R.id.iv_photo);
				iv.setImageResource(advertisement.getImageRes());
				pv_playview.addView(iv);
			}
			pv_playview.startPlay();
		}
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		iv_title_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 轮播图的点击事件
		// pv_playview.setOnItemClickListener(new AbOnItemClickListener() {
		//
		// @Override
		// public void onClick(int position) {
		// Intent intent = new Intent(QztFristActivity.this,
		// CyhAdvDetailActivity.class);
		// intent.putExtra("url", mAdvList.get(position).getContent());
		// startActivity(intent);
		// }
		// });

		// 列表
		gridview_allmodule.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (position) {
				case 0:
					if (baseApplication.isNetStat()) {
						Intent intent = new Intent(NewQztFristActivity.this,
								QztApplyActivity.class);
						startActivity(intent);
					} else {
						CustomProgressDialog
								.showBocNetworkSetDialog(baseActivity);
					}
					break;
				case 1:
					// Toast.makeText(QztFristActivity.this, "尽请期待",
					// Toast.LENGTH_LONG).show();
					if (baseApplication.isNetStat()) {
						if (LoginUtil.isLog(baseActivity)) {
							Intent intent = new Intent(NewQztFristActivity.this,
									QztOrderActivity.class);
							startActivity(intent);
						} else {
							LoginUtil.authorize(NewQztFristActivity.this,
									NewQztFristActivity.this);
						}
					} else {
						CustomProgressDialog
								.showBocNetworkSetDialog(baseActivity);
					}
					break;
				case 2:
					Bundle bundleCard = new Bundle();
					bundleCard.putString("url", BocSdkConfig.CARD);
					bundleCard.putString("name", "办卡通");
					Intent intentCard = new Intent(baseActivity,
							WebActivity.class);
					intentCard.putExtras(bundleCard);
					startActivity(intentCard);
					break;
				case 3:
					Bundle bundle = new Bundle();
					bundle.putString("url", Constants.qztUrlForfeiyong);
					bundle.putString("name", "签证费用");
					Intent intentDotbook = new Intent(NewQztFristActivity.this,
							WebActivity.class);
					intentDotbook.putExtras(bundle);
					startActivity(intentDotbook);
					break;
				case 4:
					Bundle bundle1 = new Bundle();
					bundle1.putString("url", Constants.qztUrlForshichang);
					bundle1.putString("name", "办签时长");
					Intent intentDotbook1 = new Intent(NewQztFristActivity.this,
							WebActivity.class);
					intentDotbook1.putExtras(bundle1);
					startActivity(intentDotbook1);
					break;
				// case 5:
				// Bundle bundle2 = new Bundle();
				// bundle2.putString("url", Constants.qztUrlForChuguo);
				// bundle2.putString("name", "出国金融");
				// Intent intentChuGuo = new Intent(QztFristActivity.this,
				// XmsWebActivity.class);
				// intentChuGuo.putExtras(bundle2);
				// startActivity(intentChuGuo);
				// break;

				case 5:
					Bundle bundle3 = new Bundle();
					bundle3.putString("url", Constants.qztUrlForJIncai);
					bundle3.putString("name", "精彩活动");
					Intent intentJincai = new Intent(NewQztFristActivity.this,
							WebActivity.class);
					intentJincai.putExtras(bundle3);
					startActivity(intentJincai);
					break;
				default:
					break;
				}

			}
		});
	}

	/**
	 * 添加首页选项
	 */
	private void addSchoolBean() {
		fristList.clear();
		fristList.add(new SchoolBean("签证申请", R.drawable.icon_qztapply,
				"qztapply", 0));
		fristList.add(new SchoolBean("签证进度", R.drawable.icon_qzttime,
				"qzttime", 0));
		fristList.add(new SchoolBean("信用卡申请", R.drawable.icon_qztcardapply,
				"qztcardapply", 0));
		fristList.add(new SchoolBean("签证费用", R.drawable.icon_qztmoney,
				"qztmoney", 0));
		fristList.add(new SchoolBean("办签时长", R.drawable.icon_qztusetime,
				"qztusetime", 0));
		fristList.add(new SchoolBean("精彩活动", R.drawable.icon_qztjincai,
				"qztjincai", 0));
	}

	class FristAdaper extends BaseAdapter {
		@Override
		public int getCount() {
			return fristList.size();
		}

		@Override
		public SchoolBean getItem(int position) {
			return fristList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.layout_school, null);
			}
			TextView textView = (TextView) convertView
					.findViewById(R.id.tv_layout_school);
			ImageView tvUnreadNum = (ImageView) convertView
					.findViewById(R.id.tv_unreadnum);
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.iv_layut_school);
			SchoolBean schoolBean = fristList.get(position);
			textView.setText(schoolBean.getName());
			imageView.setImageResource(schoolBean.getIntDraweid());
			int unReadNum = schoolBean.getUnReadNum();
			if (unReadNum > 0) {
				tvUnreadNum.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}

	@Override
	public void onLogin() {
		// TODO Auto-generated method stub
		Toast.makeText(NewQztFristActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCancle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onException() {
		// TODO Auto-generated method stub

	}
}
