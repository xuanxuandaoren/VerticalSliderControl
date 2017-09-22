package com.test.xiaozhu.verticalslidercontrol.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.test.xiaozhu.verticalslidercontrol.R;

/**
 * Created by xiaozhu on 2016/6/8.
 */
public class VerticalSliderControl extends FrameLayout {
    /**
     * 最大值
     */
    private int maxProgress = 100;
    /**
     * 当前的进度
     */
    private int progress = maxProgress/2;
    /**
     * 当前控件的高度，单位是dp
     */
    private int image_height = 203;
    /**
     * 设置滑动的监听
     */
    private OnSliderTouchListener mOnSliderTouchListener;
    /**
     * 设置进度的框件
     */
    private ImageView progressImage;
    /**
     * 处理滑动的界面
     */
    private View sliderView;
    /**
     * 外表面的图形
     */
    private ImageView view_outer;
    /**
     * 当前所在的点的Y坐标
     */
    private int currentY;
    /**
     * 动画控件的父控件
     */
    private FrameLayout fl_slider;


    public VerticalSliderControl(Context context) {
        super(context);
        initView(context, null);
    }

    public VerticalSliderControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    /**
     * 初始化控件
     */
    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.verticalslider, this);
        progressImage = (ImageView) findViewById(R.id.view_mid);
        sliderView = findViewById(R.id.slider_view);
        view_outer = (ImageView) findViewById(R.id.view_outer);
        fl_slider = (FrameLayout) findViewById(R.id.fl_slider);


        //注册监听事件
        registerListener();
    }

    /**
     * 重写时间的监听事件,当从父控件滑倒子控件时，把父控件的事件传递给子控件，当处于上方或者下方时，则设置为100或者0；
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getY() < fl_slider.getTop()) {
            currentY = 0;
            updateProgressByCurrentX();
        } else if (event.getY() > fl_slider.getBottom()) {
            currentY = view_outer.getMeasuredHeight();
            updateProgressByCurrentX();
        } else {
            event.setLocation(event.getX(), event.getY() - fl_slider.getTop());
            sliderView.dispatchTouchEvent(event);
        }


        return true;
    }

    /**
     * 注册监听事件
     */
    private void registerListener() {
        sliderView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getY() > getPxByProgress(100) + 10 || event.getY() < -10) {
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentY = (int) event.getY();
                        updateProgressByTouch();
                        if (mOnSliderTouchListener != null)
                            mOnSliderTouchListener.onStartTrackingTouch(VerticalSliderControl.this, progress * 0.01f);

                        break;
                    case MotionEvent.ACTION_UP:
                        currentY = (int) event.getY();
                        ;
                        updateProgressByTouch();
                        if (mOnSliderTouchListener != null)
                            mOnSliderTouchListener.onStopTrackingTouch(VerticalSliderControl.this, progress * 0.01f);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        currentY = (int) event.getY();
                        ;
                        updateProgressByTouch();

                        if (mOnSliderTouchListener != null)
                            mOnSliderTouchListener.onSliderChanged(VerticalSliderControl.this, progress * 0.01f);

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        event.setLocation(event.getX(), event.getY() + fl_slider.getTop());
                        onTouch((View) v.getParent(), event);
//                        currentY = (int) event.getY();
//                        if (currentY>getPxByProgress(50)){
//                            currentY=getPxByProgress(0);
//                        }else{
//                            currentY=getPxByProgress(100);
//                        }
//                        updateProgressByTouch();
//                        if (mOnSliderTouchListener != null)
//                            mOnSliderTouchListener.onStopTrackingTouch(VerticalSliderControl.this, progress * 0.01f);
                        break;
                    case MotionEvent.ACTION_OUTSIDE:
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 更新进度和改变控件的高度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        Log.i("公子无双", "setProgress" + progress);
        this.progress = progress;
        currentY = getPxByProgress(100 - progress*100/maxProgress);
        updateProgressByCurrentX();
    }

    /**
     * 更新图片的高度和当前的进度
     */
    private void updateProgressByTouch() {
        progress = (getPxByProgress(100) - currentY) * maxProgress / getPxByProgress(100);
        updateProgressByCurrentX();
    }

    /**
     * 根据当前的点改变progressImageView的高度
     */
    private void updateProgressByCurrentX() {
        int progressImageViewHeight = getPxByProgress(100) - currentY;
        progressImageViewHeight = fixProgressImageViewHeight(progressImageViewHeight);
        FrameLayout.LayoutParams params = (LayoutParams) progressImage.getLayoutParams();
        params.height = progressImageViewHeight;
        progressImage.setLayoutParams(params);
        progressImage.requestLayout();
        invalidate();
    }

    /**
     * 对控件的高度进行修正
     *
     * @param progressImageViewHeight
     */
    private int fixProgressImageViewHeight(int progressImageViewHeight) {
        if (progressImageViewHeight > view_outer.getMeasuredHeight()) {
            progressImageViewHeight = view_outer.getMeasuredHeight();
        }
        if (progressImageViewHeight < 0) {
            progressImageViewHeight = 0;
        }
        return progressImageViewHeight;
    }


    /**
     * 根据进度转换为相应的px
     *
     * @param progress
     * @return
     */
    private int getPxByProgress(int progress) {
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        float density = dm2.density;

        return (int) (progress * image_height * density * 0.01f);
    }

    /**
     * 设置滑动监听的接口
     */
    public interface OnSliderTouchListener {

        /**
         * Notification that the slider has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param vSlider The SeekArc whose progress has changed
         * @param percent The current slider level. This will be in the range
         *                0%..100%
         */
        void onSliderChanged(VerticalSliderControl vSlider, float percent);

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the vSlider.
         *
         * @param vSlider The VerticalSliderControl in which the touch gesture began
         * @param percent The current slider level. This will be in the range
         */
        void onStartTrackingTouch(VerticalSliderControl vSlider, float percent);

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the vSlider.
         *
         * @param vSlider The VerticalSliderControl in which the touch gesture began
         * @param percent The current slider level. This will be in the range
         *                0%..100%
         */
        void onStopTrackingTouch(VerticalSliderControl vSlider, float percent);
    }

    /**
     * 给该控件设置滑动监听
     *
     * @param listener
     */
    public void setOnSliderTouchListener(OnSliderTouchListener listener) {
        this.mOnSliderTouchListener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setProgress(progress);
    }

    /**
     * 设置最大值属性
     *
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }
}
