package com.bocop.zyt.bocop.jxplatform.trafficassistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bocop.zyt.R;
import com.bocop.zyt.jx.base.BaseActivity;
import com.bocop.zyt.jx.baseUtil.view.annotation.ContentView;
import com.bocop.zyt.jx.baseUtil.view.annotation.ViewInject;
import com.bocop.zyt.jx.baseUtil.view.annotation.event.OnClick;

@ContentView(R.layout.activity_paysuccess)
public class TrafficPaySuccessActivity extends BaseActivity {

	@ViewInject(R.id.tv_titleName)
	private TextView  tv_titleName;
	@ViewInject(R.id.tv_sjamt)
	TextView tvSjAmt;
	@ViewInject(R.id.tv_succ_peccancynum)
	TextView tvSuccPeccancyNum;
	
	String strPeccancyNum;
	String strSjAmt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		tv_titleName.setText("缴费信息");
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			
			strPeccancyNum = bundle.getString("peccancyNum");
			strSjAmt = bundle.getString("sjAmt");
			tvSuccPeccancyNum.setText(strPeccancyNum);
			tvSjAmt.setText(strSjAmt);
		}
	}
	
	@OnClick(R.id.btnComplete)
	public void btComplete(View v){
//		Intent intent = new Intent(TrafficPaySuccessActivity.this,HomeFragment.class);
//		startActivity(intent);
		getActivityManager().finishAllWithoutMain();
	}
}
