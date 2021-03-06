package com.bocop.zyt.bocop.jxplatform.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bocop.zyt.jx.base.BaseActivity;
import com.bocop.zyt.jx.base.BaseFragment;

public class MyPeccancyAdapter extends FragmentPagerAdapter
{
	private Class[] fragments;
	private FragmentManager manager;
	private BaseActivity activity;

	public static String[] TITLES = new String[]
	{ "待缴费","缴费记录" };

	public MyPeccancyAdapter(FragmentActivity activity, Class fragments[]) {
		super(activity.getSupportFragmentManager());
		this.activity = (BaseActivity)activity;
		manager = activity.getSupportFragmentManager();
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		BaseFragment fragment = null;
		try {
			fragment = (BaseFragment) manager.findFragmentById(position);
			if(fragment == null){
				fragment = (BaseFragment)(fragments[position].newInstance());
				activity.getBaseApp().getFragmentList().put(position, fragment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fragment;
	}

	@Override
	public int getCount()
	{
		return TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return TITLES[position];
	}

}
