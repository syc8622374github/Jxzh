package com.bocop.zyt.bocop.xms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bocop.zyt.R;
import com.bocop.zyt.bocop.jxplatform.config.TransactionValue;
import com.bocop.zyt.bocop.jxplatform.util.CspUtil;
import com.bocop.zyt.bocop.jxplatform.util.LoginUtil;
import com.bocop.zyt.bocop.jxplatform.util.Mcis;
import com.bocop.zyt.bocop.jxplatform.view.BackButton;
import com.bocop.zyt.bocop.xms.adapter.MyMessageAdapter;
import com.bocop.zyt.bocop.xms.xml.CspXmlXmsCom;
import com.bocop.zyt.bocop.xms.xml.message.MessageBean;
import com.bocop.zyt.bocop.xms.xml.message.MessageListResp;
import com.bocop.zyt.bocop.xms.xml.message.MessageListXmlBean;
import com.bocop.zyt.jx.base.BaseActivity;
import com.bocop.zyt.jx.baseUtil.view.annotation.ContentView;
import com.bocop.zyt.jx.baseUtil.view.annotation.ViewInject;
import com.bocop.zyt.jx.tools.DialogUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的消息
 * 
 * @author ftl
 * 
 */
@ContentView(R.layout.xms_activity_my_message)
public class MyMessageActivity extends BaseActivity {
	
	@ViewInject(R.id.iv_imageLeft)
	private BackButton ivImageLeft;
	@ViewInject(R.id.ivBack)
	private ImageView ivBack;
	@ViewInject(R.id.tv_titleName)
	private TextView tvTitle;
	@ViewInject(R.id.ivRefresh)
	private ImageView ivRefresh;
	@ViewInject(R.id.rlContent)
	public RelativeLayout rlContent;//没有消息布局
	@ViewInject(R.id.ivContent)
	public ImageView ivContent;
	@ViewInject(R.id.tvContent)
	public TextView tvContent;
	@ViewInject(R.id.lvContent)
	private ListView lvContent;
	
	private MyMessageAdapter msgAdapter;
	private ArrayList<MessageBean> msgDatas = new ArrayList<MessageBean>();
	private SharedPreferences sp;
	private Editor editor;
	private String currentRole;
	private int type;//消息类型，0：我的消息，1：金融信息
	
	public static final String SELECT_ROLE = "select_role";
	public static final String FIRST_SELECT = "first_select";
	public static final String CURRENT_ROLE = "current_role";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initTitle();
		initData();
		initListener();
	}
	
	private void initTitle() {
		type = getIntent().getIntExtra("type", 0);
		tvTitle.setText(type == 0 ? "我的消息" : "金融信息");
		ivImageLeft.setVisibility(View.GONE);
		ivBack.setVisibility(View.VISIBLE);
		ivRefresh.setVisibility(View.VISIBLE);
		ivRefresh.setImageResource(R.drawable.icon_secretary_refresh);
	}
	
	private void initData() {
		msgAdapter = new MyMessageAdapter(this, msgDatas);
		lvContent.setAdapter(msgAdapter);
		
		sp = getSharedPreferences(SELECT_ROLE, Context.MODE_PRIVATE);
		editor = sp.edit();

		boolean flag = sp.getBoolean(FIRST_SELECT, true);
		currentRole = sp.getString(CURRENT_ROLE, "man");
		if (flag) {
			showRoleDialog();
			editor.putBoolean(FIRST_SELECT, false);
			editor.commit();
			
			Intent intent = new Intent(MyMessageActivity.this,GuideActivity.class);
			startActivity(intent);
		}
		
		requestMessage();
	}
	
	private void initListener() {
		ivBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ivRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestMessage();
			}
		});
	}
	
	public void showRoleDialog() {
		View view = View.inflate(this, R.layout.xms_dialog_select_role, null);
		Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
		final ImageView ivMan = (ImageView) view.findViewById(R.id.ivMan);
		final ImageView ivWoman = (ImageView) view.findViewById(R.id.ivWoman);
		final AlertDialog dialog = DialogUtil.showWithView(this, view);
		currentRole = sp.getString(CURRENT_ROLE, "man");
		if ("man".equals(currentRole)) {
			ivMan.setImageResource(R.drawable.xms_img_man_selected);
			ivWoman.setImageResource(R.drawable.xms_img_woman_normal);
		} else {
			ivMan.setImageResource(R.drawable.xms_img_man_normal);
			ivWoman.setImageResource(R.drawable.xms_img_woman_selected);
		}
		ivMan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentRole = "man";
				ivMan.setImageResource(R.drawable.xms_img_man_selected);
				ivWoman.setImageResource(R.drawable.xms_img_woman_normal);
			}
		});
		ivWoman.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentRole = "woman";
				ivMan.setImageResource(R.drawable.xms_img_man_normal);
				ivWoman.setImageResource(R.drawable.xms_img_woman_selected);
			}
		});
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if (msgAdapter != null) {
					msgAdapter.notifyDataSetChanged();
				}
				editor.putString(CURRENT_ROLE, currentRole);
				editor.commit();
			}
		});
	}
	
	/**
	 * 请求消息
	 */
	private void requestMessage() {
		try {
			// 生成CSP XML报文
			String txCode = "MS002005";
			CspXmlXmsCom cspXmlXmsCom = new CspXmlXmsCom(LoginUtil.getUserId(this), txCode);
			cspXmlXmsCom.setPageNo("");
			String strXml = cspXmlXmsCom.getCspXml();
			Log.i("tag", "getMSgXml");
			// 生成MCIS报文
			Mcis mcis = new Mcis(strXml, TransactionValue.CSPSZF);
			Log.i("tag", "Mcis");
			final byte[] byteMessage = mcis.getMcis();
			// 发送报文
			CspUtil cspUtil = new CspUtil(this);
			Log.i("tag", "发送报文： " + new String(byteMessage, "GBK"));
//			cspUtil.setTest(true);
			cspUtil.postCspLogin(new String(byteMessage, "GBK"), new CspUtil.CallBack() {
				@Override
				public void onSuccess(String responStr) {
					if(responStr!=null){
						MessageListXmlBean msgListXmlBean = MessageListResp.readStringXml(responStr);
						if(!msgListXmlBean.getErrorcode().equals("00")){
							rlContent.setVisibility(View.VISIBLE);
							lvContent.setVisibility(View.GONE);
							if (msgListXmlBean.getErrorcode().equals("01")) {
								ivContent.setImageResource(R.drawable.xms_img_no_data);
								tvContent.setText("暂无数据");
							} else {
								ivContent.setImageResource(R.drawable.xms_img_data_error);
								tvContent.setText("数据异常");
							}
						} else {
							msgDatas.clear();
							List<MessageBean> mList = msgListXmlBean.getMessageList();
							for (int i = 0; i < mList.size(); i++) {
								MessageBean messageBean = mList.get(i);
								if (type == 0) {
									if (!"20".equals(messageBean.getType())){
										msgDatas.add(messageBean);
									}
								} else if (type == 1) {
									if ("20".equals(messageBean.getType())){
										msgDatas.add(messageBean);
									}
								}
							}
							if (msgDatas.size() != 0) {
								rlContent.setVisibility(View.GONE);
								lvContent.setVisibility(View.VISIBLE);
								msgAdapter.notifyDataSetChanged();
							} else {
								rlContent.setVisibility(View.VISIBLE);
								lvContent.setVisibility(View.GONE);
								ivContent.setImageResource(R.drawable.xms_img_no_data);
								tvContent.setText("暂无数据");
							}
						}
					}
				}

				@Override
				public void onFinish() {

				}
				@Override
				public void onFailure(String responStr) {
					Toast.makeText(MyMessageActivity.this, responStr, Toast.LENGTH_SHORT).show();
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
	}
}
