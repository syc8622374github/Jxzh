package com.bocop.zyt.bocop.jxplatform.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bocop.zyt.R;
import com.bocop.zyt.bocop.jxplatform.adapter.MyPayHisAdapter;
import com.bocop.zyt.bocop.jxplatform.bean.APJJ08ListXmlBean;
import com.bocop.zyt.bocop.jxplatform.bean.CarPeccancyBean;
import com.bocop.zyt.bocop.jxplatform.config.TransactionValue;
import com.bocop.zyt.bocop.jxplatform.util.CspUtil;
import com.bocop.zyt.bocop.jxplatform.util.LoginUtil;
import com.bocop.zyt.bocop.jxplatform.util.Mcis;
import com.bocop.zyt.bocop.jxplatform.xml.CspRecForAPJJ11;
import com.bocop.zyt.bocop.jxplatform.xml.CspXmlAPJJ11;
import com.bocop.zyt.jx.base.BaseFragment;
import com.bocop.zyt.jx.baseUtil.view.annotation.ViewInject;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PayHisFragment extends BaseFragment
{
	@ViewInject(R.id.lv_myhis_peccancy)
	ListView lvMyHisPeccancy;
	//没有违章，不显示
	@ViewInject(R.id.ll_payhis_nopeccancy)
	View llPayhisNopeccancy;
	//有违章，显示LISTVIEW
	@ViewInject(R.id.ll_payhis_mypeccancy)
	View llPayhisMypeccancy;
	
	private static final int INITIAL_DELAY_MILLIS = 500;
    private MyPayHisAdapter myPayHisAdapter;
    AlphaInAnimationAdapter alphaInAnimationAdapter;
    
    List<CarPeccancyBean> myHisPeccancyDatas = new ArrayList<CarPeccancyBean>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = initView(R.layout.fragment_payhis);
		return view;
	}
	
	@Override
	protected void initData() {
		super.initData();
		requestCspForPayHis();
	}

	@Override
	protected void initView() {
		super.initView();
	}

	private void requestCspForPayHis() {
		try {
			//生成CSP XML报文
			CspXmlAPJJ11 cspXmlForCarList = new CspXmlAPJJ11(LoginUtil.getUserId(getActivity()));
			String strXml = cspXmlForCarList.getCspXml();
			//生成MCIS报文
			Mcis mcis = new Mcis(strXml, TransactionValue.APJJ11);
			final byte[] byteMessage = mcis.getMcis();
			//发送报文
			CspUtil cspUtil = new CspUtil(getActivity());
			Log.i("tag", "发送报文： " + new String(byteMessage, "GBK"));
			cspUtil.postCspLogin(byteMessage, new CspUtil.CallBack() {
				@Override
				public void onSuccess(String responStr) {
					APJJ08ListXmlBean listBean = CspRecForAPJJ11.readStringXml(responStr);
					if(listBean.getErrorcode().equals("00") && listBean.getCarPeccancyListBean().size() > 0){
						llPayhisNopeccancy.setVisibility(View.INVISIBLE);
						llPayhisMypeccancy.setVisibility(View.VISIBLE);
						myHisPeccancyDatas = listBean.getCarPeccancyListBean();
						setListAdapter();
					}
					else{
					llPayhisNopeccancy.setVisibility(View.VISIBLE);
					llPayhisMypeccancy.setVisibility(View.INVISIBLE);
					}
				}
				
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onFailure(String responStr) {
					CspUtil.onFailure(getActivity(), responStr);
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void setListAdapter(){
		myPayHisAdapter = new MyPayHisAdapter(baseActivity, myHisPeccancyDatas);
        alphaInAnimationAdapter = new AlphaInAnimationAdapter(myPayHisAdapter);
        alphaInAnimationAdapter.setAbsListView(lvMyHisPeccancy);
        Log.d("tag", "setAbsListView");
        assert alphaInAnimationAdapter.getViewAnimator() != null;
        alphaInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        Log.d("tag", "setInitialDelayMillis");
        lvMyHisPeccancy.setAdapter(alphaInAnimationAdapter);
	}
}
