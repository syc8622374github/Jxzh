package com.bocop.zyt.bocop.xms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bocop.zyt.R;
import com.bocop.zyt.bocop.jxplatform.util.FormsUtil;
import com.bocop.zyt.jx.base.BaseActivity;
import com.bocop.zyt.jx.baseUtil.view.annotation.ContentView;
import com.bocop.zyt.jx.baseUtil.view.annotation.ViewInject;
import com.bocop.zyt.jx.baseUtil.view.annotation.event.OnClick;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 引导页面
 * @author ftl
 *
 */
@ContentView(R.layout.xms_activity_guide)
public class GuideActivity extends BaseActivity {

	@ViewInject(R.id.gvGuide)
	private GifImageView gvGuide;
	
	private GifDrawable gifFromResource;
//	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			//gifFromResource = new GifDrawable(getResources(), R.raw.xms_img_guide);
			Glide.with(this).load(R.raw.xms_img_guide).into(new GlideDrawableImageViewTarget(gvGuide, 1));
			//gvGuide.setImageDrawable(gifFromResource);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) gvGuide.getLayoutParams();
			int width = 127;
			int height = 150;
			System.out.println("width:" + FormsUtil.SCREEN_WIDTH);
			System.out.println("height:" + FormsUtil.SCREEN_HEIGHT);
			System.out.println("density:" + FormsUtil.density);
			params.width = FormsUtil.dip2px(width * 1.6f);
			params.height = FormsUtil.dip2px(height * 1.6f);
			gvGuide.setLayoutParams(params);
			/*gifFromResource.addAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationCompleted() {
					finish();
				}
			});*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		mp = MediaPlayer.create(this, R.raw.birthday);
//		mp.setOnCompletionListener(new OnCompletionListener() {
//			
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				finish();
//			}
//		});
//		mp.start();
	}
	
	@OnClick(R.id.tvSkip)
	public void onClick(View v) {
		finish();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		if(mp != null){
//			mp.pause();
//		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if(mp != null){
//			mp.release();
//		}
	}

}
