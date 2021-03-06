package com.bocop.zyt.bocop.qzt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bocop.zyt.R;
import com.bocop.zyt.jx.base.BaseActivity;
import com.bocop.zyt.jx.baseUtil.view.annotation.ContentView;
import com.bocop.zyt.jx.baseUtil.view.annotation.ViewInject;
import com.bocop.zyt.jx.baseUtil.view.annotation.event.OnClick;

@ContentView(R.layout.qzt_activity_apply_submit_complete)
public class QztApplyCompleteActivity extends BaseActivity {

	@ViewInject(R.id.tv_titleName)
	private TextView tvTitleName;
	
	@ViewInject(R.id.tv_succ_qztnum)
	TextView tvOrderNUm;
	@ViewInject(R.id.tv_qztamt)
	TextView tvQztAmt;
	
	String strOrderNum;
	String strAmt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tvTitleName.setText("缴费成功");
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			strOrderNum = bundle.getString("orderNum");
			strAmt = bundle.getString("amt");
			tvOrderNUm.setText(strOrderNum);
			tvQztAmt.setText(strAmt);
		}

	}

	@OnClick({ R.id.iv_imageLeft, R.id.btn_Back })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_imageLeft:
			finish();
			break;
		case R.id.btn_Back:
			getActivityManager().finishAllWithoutMain();
			break;
		}
	}

}
