package com.bocop.zyt.bocop.xms.view.slidelistview;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.bocop.zyt.R;

public class SlideView extends LinearLayout {
    private static final String TAG = "SlideView";
    private Context mContext;
    //用户放置所有 view 的容口
    private LinearLayout mViewContent;
    //用户放置内置 view 容器，比如删除按钮
    //private RelativeLayout mHolder;
    //弹性滑动对象，提供弹性滑动效果
    private Scroller mScroller;
    //滑动回调接口，用户来向上层通知滑动事件
    private OnSlideListener mOnSlideListener;
    //内置容器的宽度，单位dp
    private int mHolderWidth = 128;
    //分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;
    //用户来控制滑动角度，仅当角度a 满足如下条件才进行滑动：tan a = deltaX/deltaY>2
    private static final int TAN = 2;

    public interface OnSlideListener {
        //SlideView 的三种状态：开始滑动、打开、关闭
        public final static int SLIDE_STATUS_OFF = 0;
        public final static int SLIDE_STATUS_START_SCROLL = 1;
        public final static int SLIDE_STATUS_ON = 2;
        public final static int SLIDE_STATUS_NONE = 3;

        /**
         * @param view   current SlideView
         * @param status SLIDE_STATUS_OFF=0;SLIDE_STATUS_START_SCROLL =1;SLIDE_STATUS_ON = 2;
         */
        public void onSlide(View view, int status);
    }


    public SlideView(Context context) {
        super(context);
        initView();
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mContext = getContext();
        //初始化弹性滑动对象
        mScroller = new Scroller(mContext);
        //设置其方向为横向
        setOrientation(LinearLayout.HORIZONTAL);
        //将selide_view_merge加载进来
        View.inflate(mContext, R.layout.xms_slide_view_merge, this);
        mViewContent = (LinearLayout) findViewById(R.id.view_content);
        mHolderWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mHolderWidth, getResources().getDisplayMetrics()));
    }

    /**
     * 将 View 加入到 ViewContent中
     *
     * @param view
     */
    public void setContentView(View view) {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        mViewContent.addView(view);
    }

    /**
     * 设置滑动回调
     *
     * @param onSlideLinstener
     */
    public void setOnSlideLinstener(OnSlideListener onSlideLinstener) {
        mOnSlideListener = onSlideLinstener;
    }

    /**
     * 将当前状态置为关闭
     */
    public void shrink() {
    	 System.out.println("shrink-->");
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    public void onRequestTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int scrollX = getScrollX();
        Log.d(TAG, "x=" + x + "  y=" + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this, OnSlideListener.SLIDE_STATUS_START_SCROLL);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    /*滑动不满足条件，不做横向滑动*/
                    break;
                }
                //计算滑动终点否合法，防止滑动越界
                int newScrollX = scrollX - deltaX;
                if (deltaX != 0) {
                    if (newScrollX < 0) {
                        newScrollX = 0;
                    } else if (newScrollX > mHolderWidth) {
                        newScrollX = mHolderWidth;
                    }
                    this.scrollTo(newScrollX, 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                int newScrollX = 0;
                /*这里做了下判断，当松开手的时候，会自动向两边滑动，具体向哪边滑，要看肖前所处理的位置*/
                if (scrollX - mHolderWidth * 0.75 > 0) {
                    newScrollX = mHolderWidth;
                }
                /*慢慢滑向终点*/
                this.smoothScrollTo(newScrollX, 0);
                /*通知上层滑动事件*/
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this,
                            newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF :
                                    OnSlideListener.SLIDE_STATUS_ON);
                }
                break;
            }
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
    }

    private void smoothScrollTo(int destX, int destY) {
        /*缓慢滚动到指定位置*/
        int scrollX = getScrollX();
        System.out.println("destX-->" + destX);
        System.out.println("scrollX-->" + scrollX);
        int delta = destX - scrollX;
        /*以三倍时长滑向 destX，效果就是慢慢滑动*/
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

}
